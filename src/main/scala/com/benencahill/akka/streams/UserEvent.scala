package com.benencahill.akka.streams

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

/**
  * Created by benen on 09/05/17.
  */
case class UserEvent(id: String, userId: Option[String], page: String, ip: String, eventType: UserEventType)

sealed abstract class UserEventType(val code: String)
case object PageView extends UserEventType("page-view")
case object ButtonClick extends UserEventType("button-click")

object UserEventTypeJsonFormat extends RootJsonFormat[UserEventType] {
  override def write(obj: UserEventType): JsValue = JsString(obj.code)
  override def read(json: JsValue): UserEventType = json match {
    case JsString(PageView.code) => PageView
    case JsString(ButtonClick.code) => ButtonClick
  }
}

object UserEventJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val eventTypeFormat = UserEventTypeJsonFormat
  implicit val userEventFormat = jsonFormat5(UserEvent)
}
