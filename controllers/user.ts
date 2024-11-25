import User from "../models/user";
import bcrypt from "bcrypt";
import { Response, Request } from "express";
import jwt from "jsonwebtoken";
import UserI from "../models/interfaces/userI";
import { RequestI } from "../models/interfaces/RequestI";
import NodeMCU from "../models/nodeMCU";
import { RequestType } from "../middleware/auth";

export const register = async (req: Request, res: Response) => {
	console.log(`works`, req.query);
	const { firstName, lastName, email, password, fcmToken } =
		req.query as unknown as UserI;

	try {
		// const salt = await bcrypt.genSalt(10);
		// const hashedPassword = await bcrypt.hash(password, salt);

		const userEmail: UserI | null = await User.findOne({ email }).lean().exec();

		if (userEmail) {
			console.log(`email check`);
			res.status(200).json({ status: 400, msg: "Email already exists" });
		} else {
			console.log(`create new user`);

			const salt = await bcrypt.genSalt(10);
			const hashedPassword = await bcrypt.hash(password, salt);

			const newUser = new User({
				email,
				password: hashedPassword,
				firstName,
				lastName,
				fcmToken,
				isAlarmOn: false,
			});
			await newUser.save();
			console.log(`created new user dang`);
			const token = jwt.sign(
				{ user: { id: newUser._id, fcmToken: newUser.fcmToken } },
				process.env.JWT_SECRET_KEY as string,
				{
					expiresIn: 60 * 60 * 24 * 28,
				}
			);
			res.status(200).json({
				status: 200,
				accessToken: token,
				msg: "User created successfully",
			});
		}
	} catch (err) {
		res.status(200).json({ status: 500, msg: err });
	}
};

export const login = async (req: Request, res: Response) => {
	console.log(`deeba`);
	const { email, password, fcmToken } = req.query as unknown as UserI;
	console.log(`req.query`, req.query);

	try {
		const user = await User.findOne({ email }).lean().exec();

		if (!user || !bcrypt.compareSync(password, user.password)) {
			return res.status(200).json({ status: 400, msg: `invalid data, Please try again`});
		}

		if (fcmToken && fcmToken !== "" && user.fcmToken !== fcmToken) {
			user.fcmToken = fcmToken;
			await User.findByIdAndUpdate(user._id, { fcmToken });
		}

		const accessTokenUser = {
			_id: user._id,
			fcmToken: user.fcmToken,
		} as {
			_id: UserI["_id"];
			fcmToken: UserI["fcmToken"];
		};

		const accessToken = jwt.sign(
			{ user: accessTokenUser },
			process.env.JWT_SECRET_KEY as string,
			{
				expiresIn: 60 * 60 * 24 * 28,
			}
		);

		return res.json({
			status: 200,
			accessToken: accessToken,
			msg: "Sign In successful",
		});
	} catch (err) {
		return res.json({ status: 500, msg: err });
	}
};

//tslint:disable-next-line

// RequestType
export const load = async (req:  any, res: Response) => {
	// req as RequestType;
	console.log(`deeba`);
	const user  = req.user as unknown as UserI;
	console.log(`kura`, req.user);
	try {
		console.log(`user`, user);
		const getUser = await User.findById(user._id).lean().exec() as UserI;

		const nodemcus = await NodeMCU.find({ userId: user._id }).lean().exec() ?? [];
		
		const userStructure = {
			firstName: getUser?.firstName,
			lastName: getUser?.lastName,
			email: getUser?.email,
			isAlarmOn: getUser?.isAlarmOn,
		};

		return res.json({
			status: 200,
			msg: "User loaded successfully",
			user: userStructure,
			nodemcus: nodemcus || [],
		});
	} catch (err) {
		console.error(`Error\n`, err);
		return res.json({
			status: 500,
			msg: "Error Authenticating",
		});
	}
};
