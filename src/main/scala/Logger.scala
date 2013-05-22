package main.scala

/**
 * Object for logging activation levels
 */

import java.io.FileWriter

class Logger(val fileName : String) {
  var ticks = 0
  val file = new FileWriter(fileName)

  def log(str : String) {
    file.write(str + "\n")
  }
}