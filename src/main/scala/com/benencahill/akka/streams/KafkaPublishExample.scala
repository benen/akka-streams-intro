package com.benencahill.akka.streams

import java.util.UUID

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Source}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer

import scala.util.{Random, Success}

object KafkaPublishExample extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val context = system.dispatcher

  val events = Source.fromIterator(() => Iterator.continually(
    UserEvent(
      id = UUID.randomUUID().toString,
      userId = Random.shuffle(Seq(None, Some(UUID.randomUUID().toString))).head,
      page = "/home",
      ip = "127.0.0.1",
      eventType = Random.shuffle(Seq(PageView, ButtonClick)).head)
    )
  )

  val serialize = Flow[UserEvent].map { userEvent =>
    import UserEventJsonProtocol._
    import spray.json._
    userEvent.toJson.toString()
  }

  val toKafkaRecord = Flow[String].map { message =>
    new ProducerRecord[String, String]("testing-akka-streams", message)
  }

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val publishToKafka = Producer.plainSink(producerSettings)

  val blueprint = events
    .take(10000)
    .via(serialize)
    .via(toKafkaRecord)
    .toMat(publishToKafka)(Keep.right)


  val materialized = blueprint.run()

  materialized.andThen { case Success(_) => system.terminate() }
}
