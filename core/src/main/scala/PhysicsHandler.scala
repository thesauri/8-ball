package com.walter.eightball

object PhysicsHandler {
  
  /** Moves the given balls according to their velocities
   *  
   *  @param the balls to move
   *  @param time since last execution (in seconds) */
  def moveBalls(balls: Seq[Ball], t: Float): Unit = {
    balls foreach (ball => ball += t * ball.velocity)
  }
  
  /** Updates the velocities of the given balls
   *  
   *  @param the balls to update
   *  @param time since last execution (in seconds) */
  def updateVelocities(balls: Seq[Ball], t: Float): Unit = {
    ???
  }
  
  /** Returns the perimeter velocity of a ball
   *  
   *  The perimeter velocity is defined as: (ω x R) + v
   *  where: ω is the angular velocity 
   *         R is a vector from the center of the ball to the touching point with the board (0, 0, -1) */
  def getPerimeterVelocity(ball: Ball): Vector3D = ???
  
}