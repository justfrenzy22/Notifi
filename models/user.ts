import { Schema, model, Model } from "mongoose";
import UserI from "./interfaces/userI";
import bcrypt from 'bcrypt';

const userSchema = new Schema<UserI>({
	firstName: {
		type: String,
		required: true,
	},
	lastName: {
		type: String,
		required: true,
	},
	email: {
		type: String,
		required: true,
		unique: true,
	},
	password: {
		type: String,
		required: true,
	},
	fcmToken: {
		type: String,
	},
	isAlarmOn: {
		type: Boolean,
	}
});

const User: Model<UserI> = model<UserI>("User", userSchema);

export default User;
