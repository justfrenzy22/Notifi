import express, { Request, Response } from "express";
import userRouter from "./routes/user";
import nodeMCURouter from './routes/nodemcu';
import cors from "cors";
import mongoose from "mongoose";

const app = express();

require("dotenv").config();

app.use(
	cors({
		origin: "*",
	})
);

mongoose
	.connect(`mongodb://justfrenzy:22045522@192.168.0.101:27017`, {
		dbName: "nodemcu",
	})
	.then(() => console.log(`> Successfully connected to Database`))
	.catch((err) => console.error(`Error\n`, err));

app.get("/", (req: Request, res: Response) => {
	res.json({ status: 200, msg: "hello" });
});

app.use("/user", userRouter);
app.use('/nodemcu', nodeMCURouter);

app.listen(process.env.PORT, () => {
	console.log("Server started on port 8080");
});
