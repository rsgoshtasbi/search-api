package com.swayapp.search


import java.util._
import spray.json._

import com.amazonaws.ClientConfiguration
import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.{APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent, KinesisEvent}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.retry.PredefinedBackoffStrategies.ExponentialBackoffStrategy
import com.amazonaws.retry.RetryPolicy
import com.amazonaws.services.dynamodbv2.model.ScanRequest


class Main extends RequestHandler[Map[String, Any], APIGatewayProxyResponseEvent] {

  override def handleRequest(event: Map[String, Any], context: Context): APIGatewayProxyResponseEvent = {
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

    var counter = 0
    val requestBody = event
    println("request body: " + requestBody.get("body").asInstanceOf[Map[String, String]])

    val spec = new ScanRequest()
      .withTableName("INFLUENCER_INFO")
    val scanResults = dynamoClient.scan(spec)
    scanResults.getItems.forEach(record => {
      println("record: " + record.toString)
    })

    new APIGatewayProxyResponseEvent().withBody("works").withStatusCode(200)
  }
}