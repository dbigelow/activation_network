package main.scala

case class Tick(delta : Double)

class Clock(var ticksPerSecond: Double) {

  // milliseconds since start of program.
  var startTime: Long = System.currentTimeMillis()
  var lastTick = startTime
  var delta = 0.0

  // if enough time has passed since the last tick, return true.
  def tick(): Boolean = {
    var currentTime = System.currentTimeMillis()
    delta = currentTime - lastTick

    if (delta > (1000.0 / ticksPerSecond)) {
      lastTick = currentTime
      true
    } else {
      false
    }
  }
}
