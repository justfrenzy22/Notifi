import express from 'express';
import * as handle from '../controllers/nodemcu';

const router = express.Router();

router.get('/add', handle.generateNodeMCU);
router.get('/get', handle.getAlarmSystemStatus);
router.get('/regenerate', handle.regenerateNodeMCUToken);
router.get('/log', handle.logAlarmTrigger);


export default router;