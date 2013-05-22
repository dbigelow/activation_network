package main
package scala

/**
 * A diffusion network.
 *
 * Consists of a network of nodes and a clock.
 */
class DiffusionNetwork {

  var name = "Default"
  val clock = new Clock((1000.0 / 150.0))

  /**
   * List of all nodes in the network. Network topology is maintained
   * locally at each node.
   */
  var nodes = List[DiffusionNode]()

  var log = new Logger("dummy")

  def setLogger(l : Logger) {
    log = l
    nodes foreach {n => n setLogger l}
  }

  /**
   * Add a node to the network.
   * @param node The node we're adding
   */
  def addNode(node: DiffusionNode) {
    nodes = node :: nodes
  }

  /**
   * Return the structure of the network
   * @return A string representation of the network.
   */
  def graphTopology : String = {
    var topo = "{"
    var total = nodes.size
    var ct = 0
    for (node <- nodes) {
      ct += 1
      topo += node.neighborString
      topo += (if (ct < total) {
        ","
      } else {
        ""
      })
    }
    topo + "}"
  }

  /**
   * Return the structure and current activations of the network
   * @return A string containing a JSON object representing the state of the graph.
   */
  def graphSnapshot : String = {
    var snapshot = "{"
    var total = nodes.size
    var ct = 0
    for (node <- nodes) {
      ct += 1
      snapshot += node.toString
      snapshot += (if (ct < total) {
        ","
      } else {
        ""
      })
    }
    snapshot + "}"
  }

  /**
   * Get the activation for a node.
   *
   * @param nodeName The node whose activation we're setting.
   * @return A string representation of the current activation of nodeName
   */
  def nodeActivation(nodeName : String) : String = {
    val target = nodes.find(
      (n : DiffusionNode) => n.label == nodeName
    )

    target match {
      case Some(node) => {
        node.toString
      }
      case None => {
        "{}"
      }
    }

  }

  /**
   * Return a string representing the history of activations for the node
   * with the given name.
   *
   * @param nodeName The node whose history we're querying.
   * @return
   */
  def nodeHistory(nodeName : String) : String = {
    // TODO IMPLEMENT
    ""
  }

  /**
   * Set the activation of the given node to the given value.
   *
   * Used in conjunction with the HTTP interface
   */
  def setActivation(nodeName : String, newVal : Double) {
    val target = nodes.find(
      (n : DiffusionNode) => n.label == nodeName
    )

    target match {
      // TODO convert Option usage to more idiomatic Scala.
      case Some(node) => {
        node.activation = newVal
      }
      case None => {}
    }
  }

  /**
   * Add an edge between fromNode and toNode
   * @param fromNode Source of the edge
   * @param toNode Target of the edge
   */
  def addEdge(fromNode : String, toNode : String) {
    // get the node for the from
    val source = nodes.find(
      (n : DiffusionNode) => n.label == fromNode
    )
    // get the node for the to
    val target = nodes.find(
      (n : DiffusionNode) => n.label == toNode
    )

    // add the node for the to to the neighbors for the from
    source match {
      case Some(sNode) => {
        target match {
          case Some(tNode) => {
            println("Adding edge: " + sNode.label + " " + tNode.label)
            sNode.addChild(tNode)
          }
          case None => println("Target node doesn't exist :-(")
        }
      }
      case None => println("Source node doesn't exist :-(")
    }
  }

  /**
   * Delete the node with the given name
   * @param nodeName Name of node to remove.
   */
  def delNode(nodeName : String) {
    // TODO IMPLEMENT
  }

  /**
   * Delete the edge connecting fromNode and toNode.
   * @param fromNode Source of the edge.
   * @param toNode Target of the edge.
   */
  def delEdge(fromNode : String, toNode : String) {
    // TODO IMPLEMENT
  }

  /**
   * Increment the activation of the given node by the given amount
   *
   * Used in conjunction with the HTTP interface
   */
  def incrActivation(nodeName : String, increment : Double) {
    val target = nodes.find(
      (n : DiffusionNode) => n.label == nodeName
    )

    target match {
      case Some(node) => {
        node.activation += increment
      }
      case None => {}
    }
  }

  /**
   * Initialize the actors in the network and start the clock.
   *
   * This method never returns!
   */
  def go() {
    // start the actor nodes
    for (node <- nodes) {
      node.start()
    }

    // let the clock run, send Ticks to the nodes.
    while (true) {
      if (clock.tick()) {
        log.ticks += 1
        val d = clock.delta
        for (node <- nodes) {
          //print(node.label + ", ")
          node ! Tick(d)
        }
        //println()
        if (log.ticks % 50 == 0) {
          println("iteration: " + log.ticks)
        }
      }
    }
  }

}
