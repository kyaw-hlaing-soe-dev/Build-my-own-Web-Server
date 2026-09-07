const path = require("node:path");
const compression = require("compression");
const express = require("express");

const app = express();
const port = 8080;

// Compress even these small demo responses so the behavior is easy to inspect.
app.use(compression({ threshold: 0 }));

app.use(
  "/static",
  express.static(path.join(__dirname, "src", "main", "resources")),
);

app.get("/", (req, res) => {
  res.send("<h1>Hello World!</h1>");
});

app.post("/", (req, res) => {
  res.send("Got a POST request");
});

app.listen(port, () => {
  console.log(`App listening at http://localhost:${port}`);
});
