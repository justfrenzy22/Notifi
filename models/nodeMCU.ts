import { Model,model, Schema } from "mongoose";
import NodeMCUI from "./interfaces/nodeMCUI";

const nodeMCUSchema = new Schema<NodeMCUI>({
    userId: {
        type: Schema.Types.ObjectId,
        ref: 'User',
        required: true
    },
    name: {
        type: String,
        required: true
    },
    authToken: {
        type: String,
    },
    alarmTriggeredAt: [{
        type: Date,
	}],
    createdAt: {
        type: Date,
        default: Date.now
    },
});

const NodeMCU : Model<NodeMCUI> = model<NodeMCUI>('NodeMCU', nodeMCUSchema);

export default NodeMCU;