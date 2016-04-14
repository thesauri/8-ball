package com.walter.eightball

object PhysicsHandler {
  
  /** Moves the given balls according to their velocities
   *  
   *  @param the balls to move
   *  @param time since last execution (in seconds) */
  def moveBalls(balls: Seq[Ball], t: Float): Unit = {
    balls foreach (ball => ball += t * ball.velocity)
  }
  
}