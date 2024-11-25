import { Document, ObjectId } from "mongoose";

 interface UserI extends Document {
    _id: string | ObjectId;
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    fcmToken: string;
    isAlarmOn: boolean;
}

export default UserI;