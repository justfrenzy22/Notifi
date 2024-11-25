import { Document, ObjectId, Schema } from "mongoose";

interface NodeMCUI extends Document {
    _id: string | ObjectId;
    userId: Schema.Types.ObjectId;
    name: string;
    authToken: string;
    alarmTriggeredAt: Array<Date> | null;
    createdAt: Date;
}

export default NodeMCUI;