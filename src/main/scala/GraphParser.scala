package main.scala

import scala.util.parsing.combinator._

object GraphParser extends JavaTokenParsers {

  // A graph to build.
  var net : DiffusionNetwork = new DiffusionNetwork

  def graph : Parser[Any] = "graph" ~ ident ~ "{" ~ graphBody ~ "}" ^^
    (x => setName(x._1._1._1._2))

  def graphBody : Parser[Any] = opt(nodeTypeDecls) ~ "nodes" ~ "{" ~ vertices ~ "}" ~ ";" ~ "edges" ~ "{" ~ edges ~ "}" ~ ";"

  def nodeTypeDecls : Parser[Any] = rep(nodeTypeDec)

  def nodeTypeDec : Parser[Any] = "nodeType" ~ "{" ~ "}" ~ ";"

  def vertices : Parser[Any] = rep(vertex)

  def vertex : Parser[Any] = ident ~ opt(activation) ~ ";" ^^
    (x => {
      val id = x._1._1
      val act = x._1._2.getOrElse(0.0)
      addVertex(id, act)
    })

  def activation : Parser[Double] = "(" ~ floatingPointNumber ~ ")" ^^ (x => x._1._2.toDouble)

  def edges : Parser[Any] = rep(edge);

  def edge : Parser[Any] = ident ~ opt("(" ~ edgeWeight ~ ")") ~ "->" ~ ident ~ ";" ^^
  (x => {
    val to = x._1._2
    val weight = x._1._1._1._2.getOrElse(1.0)
    val from = x._1._1._1._1

    net.addEdge(from, to)
  })

  def edgeWeight : Parser[Double] = floatingPointNumber ^^ (x => x.toDouble)

  /**
   * Set the name of the DiffusionNetwork
   * @param x
   */
  def setName(x : String) {
    net.name = x
  }

  /**
   * Add a vertex to the graph
   * @param id
   * @param value
   */
  def addVertex(id : String, value : Double) {
    net.addNode(new DiffusionNode(value, id))
  }

}
