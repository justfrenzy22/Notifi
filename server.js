"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var express_1 = require("express");
var app = (0, express_1.default)();
app.get('/', function (req, res) {
    res.json({ status: 200, message: 'hello' });
});
app.listen(8080, function () {
    console.log('Server started on port 8080');
});
