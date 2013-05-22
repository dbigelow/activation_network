package main.scala

import com.twitter.finagle.Service
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.Http
import main.scala.HttpServer.Respond
import java.io.FileReader

/**
 * Finagle-based HTTP server that exposes a diffusion net to the world.
 *
 * @author Richard Kelley
 */

object HttpServer {

  /**
   * The diffusion Network we're serving
   */
  var network = new DiffusionNetwork

  class Respond extends Service[HttpRequest, HttpResponse] {

    /*
     * Regular expressions for each of the GET and POST requests
     * our server currently handles
     */
    // GET requests
    val graphTopology = """/graph/topology""".r
    val graphSnapshot = """/graph/snapshot""".r
    val nodeActivation = """/graph/(\w+)/activation""".r
    val nodeHistory = """/graph/(\w+)/history""".r

    // POST requests
    val setActivation = """/graph/set\?(\w+)=(\d+\.?\d*)""".r
    val addNode = """/graph/add\?node=(\w+)""".r
    val addEdge = """/graph/add\?from=(\w+)&to=(\w+)""".r
    val delNode = """/graph/del\?node=(\w+)""".r
    val delEdge = """/graph/del\?from=(\w+)&to=(\w+)""".r
    val incrActivation = """/graph/incr\?(\w+)=(\d+\.?\d*)""".r

    def apply(request : HttpRequest) = {
      val response = new DefaultHttpResponse(HTTP_1_1, OK)
      val genericResponse = """{name: "server", age: "wouldn't tell"}"""
      val method = request.getMethod
      val uri = request.getUri

      //println(request)

      val responseContent = method match {
        case HttpMethod.GET => {
          uri match {
            case graphTopology() => {
              val dat = network.graphTopology
              response.addHeader("data", dat)
              dat
            }

            case graphSnapshot() => {
              val dat = network.graphSnapshot
              response.addHeader("data", dat)
              dat
            }

            case nodeActivation(nodeName) => {
              val dat = network.nodeActivation(nodeName)
              response.addHeader("data", dat)
              dat
            }

            case nodeHistory(nodeName) => {
              "NOT IMPLEMENTED. history of " + nodeName + "\n"
            }

            case x => {
              "-1"  + "\n"
            }
          }
        }

        case HttpMethod.POST => {
          uri match {
            case setActivation(nodeName, newVal) => {
              network.setActivation(nodeName, newVal.toDouble)
              "new activation of " + nodeName + " is " + newVal + "\n"
            }

            case addNode(node) => {
                var newNode = new DiffusionNode(0.0, node)
              newNode.start()
                network.addNode(newNode)
                "Added node " + node + " to network.\n"
            }

            case addEdge(fromNode, toNode) => {
              network.addEdge(fromNode, toNode)
              "NOT IMPLEMENTED. new edge from " + fromNode + " to " + toNode + "\n"
            }

            case delNode(nodeName) => {
              network.delNode(nodeName)
              "NOT IMPLEMENTED. deleting node " + nodeName + "\n"
            }

            case delEdge(fromNode, toNode) => {
              network.delEdge(fromNode, toNode)
              "NOT IMPLEMENTED. deleting edge from " + fromNode + " to " + toNode + "\n"
            }

            case incrActivation(nodeName, increment)  => {
              network.incrActivation(nodeName, increment.toDouble)
              "incrementing activation of " + nodeName + " by " + increment + "\n"
            }
          }
        }
      }

      response.addHeader("data", network.graphSnapshot)
      response.addHeader("Access-Control-Allow-Origin", "*")

      response.setContent(copiedBuffer(responseContent, UTF_8))
      Future.value(response)
    }
  }


  def main(args : Array[String]) {

    val reader = new FileReader(args(0))
    val parser = GraphParser

    parser.parseAll(parser.graph, reader)

    network = parser.net

    val now = System.currentTimeMillis()
    val fName = "activations-" + now + ".tsv"
    val l = new Logger(fName)
    network setLogger l

    // set up HTTP server
    val respond = new Respond

    val myService : Service[HttpRequest, HttpResponse] = respond

    val server : Server = ServerBuilder()
      .codec(Http())
      .bindTo(new InetSocketAddress(8888))
      .name("ADNServer")
      .build(myService)

    network.go()
  }

}
