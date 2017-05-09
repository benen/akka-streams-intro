package com.benencahill.akka.streams

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.{Flow, Keep, Sink}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

import scala.concurrent.Future
import scala.util.Success

object KafkaConsumerExample extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val context = system.dispatcher

  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("localhost:9092")
    .withGroupId("testing-akka-streams")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_DOC, "earliest")

  val kafkaSource = Consumer.atMostOnceSource(consumerSettings, Subscriptions.topics("testing-akka-streams"))
    .map( record => record.value())

  val deserialize = Flow[String].mapAsync(1){ data =>
    import UserEventJsonProtocol._
    Unmarshal(data.mkString).to[UserEvent]
  }

  val print = Sink.foreach[UserEvent](event => println(event))

  val blueprint: RunnableGraph[Future[Done]] = kafkaSource.take(100).via(deserialize).toMat(print)(Keep.right)

  val materialzied = blueprint.run()

  materialzied.andThen { case Success(_) => system.terminate() }
}
