package com.swayapp.search

import com.amazonaws.services.dynamodbv2.document.Table
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.QueryRequest

import collection.mutable
import scala.collection.JavaConverters._
//import java.util._
import scala.util._
import spray.json._
import com.amazonaws.ClientConfiguration
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.retry.PredefinedBackoffStrategies.ExponentialBackoffStrategy
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, ScanRequest}
import scala.collection.mutable.ListBuffer

import spray.json._


/*
    "body": {
      "categories": [
      "food"
      ],
      "socialmedia": [
      "instagram"
      ]
    },
 */
case class Body(categories: Option[List[String]], socialmedia: Option[List[String]])

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat2(Body)
}
import MyJsonProtocol._

class Main extends RequestHandler[JsValue, APIGatewayProxyResponseEvent] {

  override def handleRequest(event: JsValue, context: Context): APIGatewayProxyResponseEvent = {
    val dynamoDBTableName = "influencer"
    val retryPolicy = new RetryPolicy(
      null,
      new ExponentialBackoffStrategy(1000, 10000),
      3,
      false
    )
    val clientConf = new ClientConfiguration().withConnectionTimeout(2000)
    clientConf.setRetryPolicy(retryPolicy)

    val dynamoClient = AmazonDynamoDBClientBuilder.standard
      .withRegion(Regions.US_WEST_2)
      .withClientConfiguration(clientConf)
      .build()

    val dynamoDB = new DynamoDB(dynamoClient)
    val testJsonCaseClass = event.asJsObject().fields("body").asJsObject.convertTo[Body]
    println("categories: " + testJsonCaseClass.categories.getOrElse(None))
    println("social medias: " + testJsonCaseClass.socialmedia.getOrElse(None))

    val scanSpec = new ScanRequest().withTableName(dynamoDBTableName)
    val scanResults = dynamoClient.scan(scanSpec)
    val userUUIDResults = List.newBuilder[String]
    scanResults.getItems.forEach(dynamoRecord => {
      val dynamoRecordCategories = dynamoRecord.get("categories").getSS.asScala
      val dynamoRecordSocialMedia = dynamoRecord.get("socialmedia").getSS.asScala

      if (dynamoRecordCategories.toList.exists(testJsonCaseClass.categories.get.contains) && dynamoRecordSocialMedia.toList.exists(testJsonCaseClass.socialmedia.get.contains)){
        userUUIDResults += dynamoRecord.get("influencerUUID").getS
      }
    })

    val results = List.newBuilder[String]
    val table: Table = new Table(dynamoClient, "user")
    val querySpec = new QuerySpec()
    userUUIDResults.result.foreach(uuid => {
      querySpec
        .withKeyConditionExpression("userUUID = :v_userUUID")
        .withValueMap(new ValueMap().withString(":v_userUUID", uuid))
      table.query(querySpec).asScala.foreach(results += _.getString("first_name"))
    })

    println("Matching users: " + results.result)
    println(results.result.toJson.toString)
    new APIGatewayProxyResponseEvent().withBody(results.result.toJson.toString).withStatusCode(200)
  }
}