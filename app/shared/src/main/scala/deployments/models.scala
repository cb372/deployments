package deployments

import scala.scalajs.js.Date

case class Deployment(
  timestamp: String,
  appName: String,
  environment: String,
  buildId: String,
  status: String
)
