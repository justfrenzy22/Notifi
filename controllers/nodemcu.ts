import { Request, Response } from "express";
import NodeMCU from "../models/nodeMCU";
import User from "../models/user";
import crypto from "crypto";
import { messaging } from "./firebaseInit";
import { generateAuthToken } from "../utils/nodemcu";
import NodeMCUI from "../models/interfaces/nodeMCUI";
import UserI from "../models/interfaces/userI";

export const generateNodeMCU = async (req: Request, res: Response) => {
	const { userId, name, accessToken } = req.query as unknown as {
		userId: NodeMCUI['userId'];
		name: NodeMCUI['name'];
		accessToken: string;
	};

	try {
		const newDevice = new NodeMCU({
			userId: userId,
			name: name,
			authToken: "",
			
		}) as NodeMCUI;
		await newDevice.save();

		const nodeMCUId = newDevice._id.toString();

		const authToken: string = generateAuthToken(userId, nodeMCUId) as string;

		newDevice.authToken = authToken;

		await newDevice.save();

		return res.json({
			status: 200,
			nodeMCUToken: authToken,
			msg: "NodeMCU created successfuly",
		});
	} catch (err: any) {
		return res.json({ status: 500, msg: err.message });
	}
};

export const getAlarmSystemStatus = async (req: Request, res: Response) => {
	const { authToken } = req.body;

	const [userId, custom, nodemcuId] = authToken.split(":");

	try {
		const nodeMCU = await NodeMCU.findOne({ authToken: authToken })
			.lean()
			.exec();

		if (!nodeMCU) {
			return res.json({ status: 404, msg: "NodeMCU not found" });
		}

		const userIdIsValid =
			crypto
				.createHmac("sha256", process.env.NODEMCU_SECRET_KEY as string)
				.update(nodeMCU.userId.toString())
				.digest("hex")
				.substring(0, 6)
				.toUpperCase() === userId;

		const nodemcuIdIsValid =
			crypto
				.createHmac("sha256", process.env.NODEMCU_SECRET_KEY as string)
				.update(nodeMCU._id.toString())
				.digest("hex")
				.substring(0, 6)
				.toUpperCase() === nodemcuId;

		if (!userIdIsValid || !nodemcuIdIsValid) {
			return res.json({ status: 401, msg: "Invalid token" });
		}

		const findUser = User.findById(nodeMCU.userId) as unknown as UserI;

		return res.json({ status: 200, statusAlarm: findUser.isAlarmOn });
	} catch (err: any) {
		return res.json({ status: 500, msg: err.message });
	}
};

export const regenerateNodeMCUToken = async (req: Request, res: Response) => {
	const { userId, nodeMCUId } = req.body;

	try {
		const oldNodeMCU = await NodeMCU.findById(nodeMCUId).exec();

		if (!oldNodeMCU) {
			return res.status(404).json({ msg: "NodeMCU not found" });
		}

		const newNodeMCU = new NodeMCU({
			userId: oldNodeMCU.userId,
			name: oldNodeMCU.name,
			alarmTriggeredAt: oldNodeMCU.alarmTriggeredAt,
			authToken: "",
		}) as NodeMCUI;
		await newNodeMCU.save();

		const newNodeMCUId = newNodeMCU._id.toString();

		const authToken: string = generateAuthToken(userId, newNodeMCUId) as string;

		newNodeMCU.authToken = authToken;
		newNodeMCU.alarmTriggeredAt = oldNodeMCU.alarmTriggeredAt;

		await newNodeMCU.save();

		await NodeMCU.findByIdAndDelete(oldNodeMCU._id).exec();

		return res.json({
			status: 200,
			nodeMCUToken: authToken,
			msg: "NodeMCU token regenerated successfully",
		});
	} catch (err: any) {
		return res.json({ status: 200, msg: err.message });
	}
};

export const logAlarmTrigger = async (req: Request, res: Response) => {
	const { authToken } = req.body;
	const secretKey = process.env.NODEMCU_SECRET_KEY;

	if (!authToken) {
		return res.json({ status: 400, msg: "Authorization token is required" });
	}

	const tokenParts = authToken.split(":");

	if (tokenParts.length !== 3) {
		return res.json({ status: 400, msg: "Invalid authorization token format" });
	}

	const [userId, custom, nodemcuId] = tokenParts;

	try {
		const nodeMCU = await NodeMCU.findOne({ authToken });

		if (!nodeMCU) {
			return res.json({ status: 404, msg: "NodeMCU not found" });
		}

		const userIdIsValid =
			crypto
				.createHmac("sha256", secretKey as string)
				.update(nodeMCU.userId.toString())
				.digest("hex")
				.substring(0, 6)
				.toUpperCase() === userId;

		const nodemcuIdIsValid =
			crypto
				.createHmac("sha256", secretKey as string)
				.update(nodeMCU._id.toString())
				.digest("hex")
				.substring(0, 6)
				.toUpperCase() === nodemcuId;

		if (!userIdIsValid || !nodemcuIdIsValid) {
			return res.json({ status: 401, msg: "Invalid token" });
		}

		nodeMCU.alarmTriggeredAt = nodeMCU.alarmTriggeredAt || [];

		nodeMCU.alarmTriggeredAt.push(new Date());

		await nodeMCU.save();

		const user = await User.findById(nodeMCU.userId).exec();

		if (!user) {
			return res.json({ status: 404, msg: "User not found" });
		}

		const message = {
			notification: {
				title: "Alarm triggered",
				body: "Your alarm has been triggered",
			},
			token: user.fcmToken as string,
		};

		await messaging.send(message);

		return res.json({ status: 200, msg: "Alarm triggered" });
	} catch (err: any) {
		return res.json({ status: 500, msg: err.message });
	}
};

/*
const nodeMCU = await NodeMCU.findById(userId);

// Alarm triggers by minute
const alarmTriggersPerMinute = await AlarmLog.aggregate([
  { $match: { nodeMCUId: nodeMCU._id } },
  { $group: {
    _id: { $dateToString: { format: "%Y-%m-%d %H:%M", date: "$alarmTriggeredAt" } },
    count: { $count: {} }
  }},
  { $sort: { _id: 1 }}
]);

// Alarm triggers by 30 minutes
const alarmTriggersPerThirtyMinutes = await AlarmLog.aggregate([
  { $match: { nodeMCUId: nodeMCU._id } },
  { $group: {
    _id: { $dateToString: { format: "%Y-%m-%d %H:%m", date: "$alarmTriggeredAt" } },
    count: { $count: {} }
  }},
  { $sort: { _id: 1 }}
]);

// Alarm triggers by day
const alarmTriggersPerDay = await AlarmLog.aggregate([
  { $match: { nodeMCUId: nodeMCU._id } },
  { $group: {
    _id: { $dateToString: { format: "%Y-%m-%d", date: "$alarmTriggeredAt" } },
    count: { $count: {} }
  }},
  { $sort: { _id: 1 }}
]);

// Alarm triggers by month
const alarmTriggersPerMonth = await AlarmLog.aggregate([
  { $match: { nodeMCUId: nodeMCU._id } },
  { $group: {
    _id: { $dateToString: { format: "%Y-%m", date: "$alarmTriggeredAt" } },
    count: { $count: {} }
  }},
  { $sort: { _id: 1 }}
]);

// Alarm triggers by year
const alarmTriggersPerYear = await AlarmLog.aggregate([
  { $match: { nodeMCUId: nodeMCU._id } },
  { $group: {
    _id: { $dateToString: { format: "%Y", date: "$alarmTriggeredAt" } },
    count: { $count: {} }
  }},
  { $sort: { _id: 1 }}
]);
*/
