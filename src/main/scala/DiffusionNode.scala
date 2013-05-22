package main.scala


/**
 * A node in a diffusion network.
 */

import scala.actors._

case class Message(increment: Double)

class DiffusionNode(var activation: Double, val label: String) extends Actor {

  // Used to compute linear function of activation.
  val weight: Double = 1.0
  val bias: Double = 0.0

  var log : Option[Logger] = None

  // Neighbors that are to receive activation.
  var neighbors = List[DiffusionNode]()

  // Weight for sending a message
  val epsilon = 0.1

  // decay parameter - activation gets multiplied by 1 - beta at each tick.
  val beta = 0.05

  /**
   * Add a node that we'll send activation to.
   * @param node the node that is to receive activation
   */
  def addChild(node: DiffusionNode) {
    neighbors = node :: neighbors
  }

  def setLogger(l : Logger) {
    log = Some(l)
  }

  override def toString : String = {
//    '"' + label + '"' + " : {" + """ "activation" : """ + activation + "}"
	label + ": " + activation
     //{"label" : """ + '"' + label + '"' + """ { "activation" : """ + activation + "}"
  }

  def neighborString : String = {
    var total = neighbors.size
    var ct = 0
    var str = """{"label": """ + '"' + label + '"' + """, "neighbors": ["""
    for (node <- neighbors) {
      ct += 1
      str += node.label
      str += (if (ct < total) {
        ", "
      } else {
        ""
      })
    }
    str + "]}"
  }

  /**
   * Listen for messages and perform accordingly.
   */
  def act() {
    loop {
      /*
      // decay activation
      activation *= (1 - beta)
      if (activation < 1e-7) {
        activation = 0
      }
      */
      // listen for messages
      react {
        case Tick(d) => {
          //println(label + ": " + "received tick")
          // decay activation
          activation *= (1 - beta)
          if (activation < 1e-3) {
            activation = 0
          }

          log match {
            case None => println(label + "\t\t\t" + activation)
            case Some(l) => l.log(l.ticks + "\t" + label + "\t" + activation)
          }

          // send activation to each neighbor
          for (node <- neighbors) {
            node ! Message(epsilon * activation)
          }
        }

        case Message(incr) => {
          activation += weight * incr + bias
        }

        case x: Any => println("Unrecognized message: " + x)
      }
    }
  }

}
