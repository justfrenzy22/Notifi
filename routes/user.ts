import express from "express";
import * as handle from "../controllers/user";
import auth from "../middleware/auth";

const router = express.Router();

router.get("/login", handle.login);
router.get("/register", handle.register);
router.get("/load", auth, handle.load);

export default router;
