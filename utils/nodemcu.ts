import crypto from "crypto";
import { ObjectId } from "mongoose";

export const generateAuthToken =  (userId: string | ObjectId, nodemcuId: string) : string => {

    const chars = `ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789`;
    const secret = process.env.NODEMCU_SECRET_KEY as string;

    if (!secret) {
        throw new Error('Secret key not defined');
    }

    const userIdEnc = crypto.createHmac('sha256', secret)
        .update(userId.toString())
        .digest('hex')
        .substring(0, 6)
        .toUpperCase();


    const nodemcuIdEnc = crypto.createHmac('sha256', secret)
        .update(nodemcuId.toString())
        .digest('hex')
        .substring(0, 6)
        .toUpperCase();

    let custom = '';

    for (let i = 0; i < 1; i++) {
        for (let j = 0; j < 4; j++) {
            custom += chars.charAt(Math.floor(Math.random() * chars.length));
        }
    }
    return `${userIdEnc}:${custom}:${nodemcuIdEnc}`;
};