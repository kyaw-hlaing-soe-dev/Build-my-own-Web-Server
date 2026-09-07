const fs = require("node:fs");
const https = require("node:https");
const path = require("node:path");
const compression = require("compression");
const express = require("express");

const app = express();
const port = 8080;
const certificateDirectory = path.join(__dirname, "certs");
const tlsOptions = {
  key: fs.readFileSync(path.join(certificateDirectory, "server.key")),
  cert: fs.readFileSync(path.join(certificateDirectory, "server.cert")),
};

// Compress even these small demo responses so the behavior is easy to inspect.
app.use(compression({ threshold: 0 }));

app.use(
  "/static",
  express.static(path.join(__dirname, "public")),
);

app.get("/", (req, res) => {
  res.send("<h1>Hello World!</h1>");
});

app.post("/", (req, res) => {
  res.send("Got a POST request");
});

https.createServer(tlsOptions, app).listen(port, () => {
  console.log(`App listening at https://localhost:${port}`);
});
