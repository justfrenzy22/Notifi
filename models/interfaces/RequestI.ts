import { Request } from "express";
import UserI from "./userI";

export interface RequestI extends Request {
		_id: UserI['_id'];
		fcmToken: UserI["fcmToken"];
}
