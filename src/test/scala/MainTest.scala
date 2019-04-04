import java.util

import scala.collection.JavaConverters._
import com.swayapp.search._
import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec
import spray.json._
import com.amazonaws.services.lambda.runtime._



class MainTest extends FlatSpec with MockFactory {

  val testContext = new Context {
    override def getAwsRequestId: String = ""

    override def getLogGroupName: String = ""

    override def getLogStreamName: String = ""

    override def getFunctionName: String = ""

    override def getFunctionVersion: String = ""

    override def getInvokedFunctionArn: String = ""

    override def getIdentity: CognitoIdentity = new CognitoIdentity {
      override def getIdentityId: String = ""

      override def getIdentityPoolId: String = ""
    }

    override def getClientContext: ClientContext = new ClientContext {
      override def getClient: Client = new Client {
        override def getInstallationId: String = ""

        override def getAppTitle: String = ""

        override def getAppVersionName: String = ""

        override def getAppVersionCode: String = ""

        override def getAppPackageName: String = ""
      }

      override def getCustom: util.Map[String, String] = Map[String, String](("String", "String")).asJava

      override def getEnvironment: util.Map[String, String] = Map[String, String]().empty.asJava
    }

    override def getRemainingTimeInMillis: Int = 99999999

    override def getMemoryLimitInMB: Int = 999

    override def getLogger: LambdaLogger = new LambdaLogger {
      override def log(message: String): Unit = ""

      override def log(message: Array[Byte]): Unit = ""
    }
  }
  val jsonTest: JsValue = """
  {
    "body": {
      "categories": [
      "food",
      "travel"
      ],
      "socialmedia": [
      "instagram"
      ]
    },
    "resource": "/{proxy+}",
    "path": "/path/to/resource",
    "httpMethod": "POST",
    "isBase64Encoded": true,
    "queryStringParameters": {
      "foo": "bar"
    },
    "pathParameters": {
      "proxy": "/path/to/resource"
    },
    "stageVariables": {
      "baz": "qux"
    },
    "headers": {
      "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
      "Accept-Encoding": "gzip, deflate, sdch",
      "Accept-Language": "en-US,en;q=0.8",
      "Cache-Control": "max-age=0",
      "CloudFront-Forwarded-Proto": "https",
      "CloudFront-Is-Desktop-Viewer": "true",
      "CloudFront-Is-Mobile-Viewer": "false",
      "CloudFront-Is-SmartTV-Viewer": "false",
      "CloudFront-Is-Tablet-Viewer": "false",
      "CloudFront-Viewer-Country": "US",
      "Host": "1234567890.execute-api.us-west-2.amazonaws.com",
      "Upgrade-Insecure-Requests": "1",
      "User-Agent": "Custom User Agent String",
      "Via": "1.1 08f323deadbeefa7af34d5feb414ce27.cloudfront.net (CloudFront)",
      "X-Amz-Cf-Id": "cDehVQoZnx43VYQb9j2-nvCh-9z396Uhbp027Y2JvkCPNLmGJHqlaA==",
      "X-Forwarded-For": "127.0.0.1, 127.0.0.2",
      "X-Forwarded-Port": "443",
      "X-Forwarded-Proto": "https"
    },
    "requestContext": {
      "accountId": "123456789012",
      "resourceId": "123456",
      "stage": "prod",
      "requestId": "c6af9ac6-7b61-11e6-9a41-93e8deadbeef",
      "requestTime": "09/Apr/2015:12:34:56 +0000",
      "requestTimeEpoch": 1428582896000,
      "identity": {
      "cognitoIdentityPoolId": null,
      "accountId": null,
      "cognitoIdentityId": null,
      "caller": null,
      "accessKey": null,
      "sourceIp": "127.0.0.1",
      "cognitoAuthenticationType": null,
      "cognitoAuthenticationProvider": null,
      "userArn": null,
      "userAgent": "Custom User Agent String",
      "user": null
    },
      "path": "/prod/path/to/resource",
      "resourcePath": "/{proxy+}",
      "httpMethod": "POST",
      "apiId": "1234567890",
      "protocol": "HTTP/1.1"
    }
  }
  """.parseJson

  "" should "" in {
    val Main = new Main
    Main.handleRequest(jsonTest, testContext)
  }
}
