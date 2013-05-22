package main.scala

import java.io.FileReader
import scala.util.parsing.combinator._

object TestParser {

  def foo(args : Array[String]) {
    val parser = GraphParser
    val reader = new FileReader("test.graph")

    parser.parseAll(parser.graph, reader)

    println("Got a graph named: " + parser.net.name)
    println("Got nodes: " + parser.net.graphTopology)
    println("Activation: " + parser.net.graphSnapshot)
  }

}
