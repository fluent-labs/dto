package io.fluentlabs.dto.v1.health

import ReadinessStatus.ReadinessStatus
import play.api.libs.json._

/** @param database Whether service can connect to database to get user data
  * @param content  Whether service can connect to elasticsearch to get language content
  */
case class Readiness(
    database: ReadinessStatus,
    content: ReadinessStatus,
    webster: ReadinessStatus
) {
  def overall: ReadinessStatus =
    List(database, content, webster) match {
      case up if up.forall(_.eq(ReadinessStatus.UP)) => ReadinessStatus.UP
      case down if down.forall(_.eq(ReadinessStatus.DOWN)) =>
        ReadinessStatus.DOWN
      case _ => ReadinessStatus.DEGRADED
    }
}

object Readiness {
  // Allows readiness to be serialized to JSON
  implicit val format: Format[Readiness] = Json.format
}

object ReadinessStatus extends Enumeration {
  type ReadinessStatus = Value
  val UP, DOWN, DEGRADED = Value

  implicit val readinessStatusFormat: Format[ReadinessStatus] =
    new Format[ReadinessStatus] {
      def reads(json: JsValue): JsResult[ReadinessStatus] =
        JsError("We don't read these")
      def writes(status: ReadinessStatus.ReadinessStatus): JsString =
        JsString(status.toString)
    }
}
