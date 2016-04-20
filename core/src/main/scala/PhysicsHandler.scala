package com.walter.eightball

import scala.math._

object PhysicsHandler {
  
  val g = 9.8f //Gravitational constant
  val cfs = 0.2f //Coefficient of friction while sliding
  val cfr = 0.01f //Coefficient of friction while rolling
  
  /** Moves the given balls according to their velocities
   *  
   *  @param the balls to move
   *  @param time since last execution (in seconds) */
  def moveBalls(balls: Seq[Ball], t: Float): Unit = {
    balls foreach (ball => ball += t * ball.velocity)
  }
  
  /** Updates the velocities of the given balls
   *  
   *  The logic is based on the equations from the following link:
   *  http://archive.ncsa.illinois.edu/Classes/MATH198/townsend/math.html
   *  
   *  @param the balls to update
   *  @param time since last execution (in seconds) */
  def updateVelocities(balls: Seq[Ball], t: Float): Unit = {
    balls.foreach {
      ball => {
        if (getPerimeterVelocity(ball).norm <= 0.02f) {
          val newVelocity = ball.velocity + (-cfr * 9.8f * ball.velocity.normalized * t)
          if (ball.velocity.norm < 0.01f && ball.angularVelocity.norm < 0.01f ) {
            ball.velocity = Vector3D(0f, 0f, 0f)
            ball.angularVelocity = Vector3D(0f, 0f, 0f)
            ball.state = BallState.Still
          } else {
            ball.velocity = newVelocity
            val newAngularVelocity = Vector3D(-newVelocity.y / ball.radius, newVelocity.x / ball.radius, 0)
            ball.angularVelocity = newAngularVelocity
          }  
        } else {
          val pv = getPerimeterVelocity(ball).normalized
          ball.velocity += -cfs * 9.8f * pv * t
          ball.angularVelocity += (5f * t / (2f * ball.mass * pow(ball.radius.toDouble, 2).toFloat)) * (Vector3D(0f,0f,-ball.radius) cross (-cfs * ball.mass * g * ball.radius * pv))
        }
      }
    }
  }
  
  /** Returns the perimeter velocity of a ball
   *  
   *  The perimeter velocity is defined as: (ω x R) + v
   *  where: ω is the angular velocity 
   *         R is a vector from the center of the ball to the touching point with the board (0, 0, -r) */
  def getPerimeterVelocity(ball: Ball): Vector3D =
    (ball.angularVelocity cross Vector3D(0f, 0f, -ball.radius)) + ball.velocity
  
}