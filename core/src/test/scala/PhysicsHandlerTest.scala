package com.walter.eightball

import org.scalatest._

class PhysicsHandlerTest extends FlatSpec with Matchers {
  
  "moveBalls" should "move the balls" in {
    val balls = Vector(new Ball(0f, 0f, 0f, 1), new Ball(-10f, 5f, 0f, 2))
    balls(0).velocity = Vector3D(1f, 1f, 0f)
    balls(1).velocity = Vector3D(-5f, 0f, 0f)

    //Should not have moved before called
    balls(0).x should be (0f)
    balls(0).y should be (0f)
    balls(1).x should be (-10f)
    balls(1).y should be (5f)
    
    //Move once with a timestep of 1 s (1s movement)
    PhysicsHandler.moveBalls(balls, 1f)
    balls(0).x should be (1f)
    balls(0).y should be (1f)
    balls(1).x should be (-15f)
    balls(1).y should be (5f)
    
    //Move them 9 more times (=> total of 10s movement)
    (0 until 9).foreach(x => PhysicsHandler.moveBalls(balls, 1f))
    balls(0).x should be (10f)
    balls(0).y should be (10f)
    balls(1).x should be (-60f)
    balls(1).y should be (5f)
  }
  
  "moveBalls" should "not move the balls if their velocity is 0" in {
    val balls = Vector(new Ball(0f, 0f, 0f, 1), new Ball(-10f, 5f, 0f, 2))
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
  
  "updateVelocities" should "only update velocity according to its velocity if it is not spinning" in {
    val balls = Vector(new Ball(0f, 0f, 0f, 1))
    balls(0).velocity = Vector3D(1f, 0f, 0f)
    PhysicsHandler.updateVelocities(balls, 0.1f)
    
    //v = v0 - µgv∆t, µ = 0.2, g = 9.8, v = (1,0,0), ∆t = 0.1s
    balls(0).velocity should be (Vector3D(0.804f, 0f, 0f))
  }
  
}