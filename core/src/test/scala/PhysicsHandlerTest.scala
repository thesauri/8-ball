package com.walter.eightball

import org.scalatest._

class PhysicsHandlerTest extends FlatSpec with Matchers {
  
  "moveBalls" should "not move the balls if their velocity is 0" in {
    val balls = Vector(new Ball(0f, 0f, 1), new Ball(-10f, 5f, 2))
    PhysicsHandler.moveBalls(balls, 1f)
    balls(0).x should be (0f)
    balls(0).y should be (0f)
    balls(1).x should be (-10f)
    balls(1).y should be (5f)
    (0 to 1000).foreach(x => PhysicsHandler.moveBalls(balls, 1f))
    balls(0).x should be (0f)
    balls(0).y should be (0f)
    balls(1).x should be (-10f)
    balls(1).y should be (5f)
  }
  
}