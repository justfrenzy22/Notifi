import { NextFunction, Request, Response } from "express";

import jwt from "jsonwebtoken";
import UserI from "../models/interfaces/userI";
import { RequestI } from "../models/interfaces/RequestI";

export interface RequestType {
	[x: string]: unknown;
	user?: RequestI;
    headers: any;
	next: NextFunction;
	
}

const auth = async (req: any, res: Response, next: NextFunction) => {
		req as RequestType;
		const { authorization } = req.headers as unknown as { authorization: string };
		console.log(`headers`, req.headers)
	try {


		if (authorization === "undefined" || !authorization || authorization === "") {
			return res.status(200).json({ status: 401, msg: "No Access Token" });
		}

		const decoded = jwt.verify(
			authorization,
			process.env.JWT_SECRET_KEY as string
		) as unknown as { user: RequestI };

		const user = decoded.user as unknown as RequestI;

		if (!user) {
			return res.status(200).json({ status: 401, msg: "No user" });
		}
		console.log(`user`, user);
		req.user = user;
		next();
	} catch (err) {
		console.error(`Error\n`, err);
		return res.json({
			status: 401,
			msg: "Error Authenticating",
		});
	}
};

export default auth;
