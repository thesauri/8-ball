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
  
  "A ball" should "should start spinning after sliding for a while" in {
    val balls = Vector(new Ball(0f, 0f, 0f, 1))
    balls(0).velocity = Vector3D(1f, 0f, 0f)
    PhysicsHandler.updateVelocities(balls, 0.1f)
    
    /* The angular velocity should be around (0, 17.147858, 0)
     * 
     * ω = 5/2 (R x (-µ*m*g*r*v)) * ∆t/(m*r^2)
     * where: R a vector pointing from the center of the ball to the touching point with the table (0, 0, -r)
     * 			  r is the radius of the ball
     * 				m the mass of the ball
     *        v its perimeter velocity (angular velocity at edge + velocity)
   	 *        g gravitational acceleration (9.8) */
    
    balls(0).angularVelocity should be (Vector3D(0f, 0.49000004f, 0f))
  }
  
  "The relative velocity" should "be the relative velocity between the table and the touching point of a ball" in {
    val ball = new Ball(0f, 0f, 0f, 1)
    ball.velocity = Vector3D(2f, 1f, 0f)
    ball.angularVelocity = Vector3D(5f, -2f, 0f)
    
    //(5f, -2f, 0f) x (0f, 0f, -1f) + (2f, 1f, 0f)
    PhysicsHandler.getRelativeVelocity(ball) should be (Vector3D(2.05715f, 1.142875f, 0f))
  }
  
  "timeUntilCollision" should "return the time correctly for two balls with one upcoming collision" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    ball1.velocity = Vector3D(1f, 0f, 0f)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    ball2.velocity = Vector3D(-1f, 0f, 0f)
    PhysicsHandler.timeUntilCollision(ball1, ball2) should be (1f) 
  }
  
  "timeUntilCollision" should "return the time to the first of two upcoming collisions" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    ball1.velocity = Vector3D(1f, 0f, 0f)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    PhysicsHandler.timeUntilCollision(ball1, ball2) should be (2f) 
  }
  
  "timeUntilCollision" should "return -1 if the balls are not colliding and standing still" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    PhysicsHandler.timeUntilCollision(ball1, ball2) should be (-1f) 
  }
  
  "timeUntilCollision" should "return -1 if the balls are moving but won't collide" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    ball1.velocity = Vector3D(0f, 1f, 0f)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    PhysicsHandler.timeUntilCollision(ball1, ball2) should be (-1f) 
  }
  
  "timeUntilHorizontalCollision" should "return the time when a ball will collide with a horzintal wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    ball.velocity = Vector3D(0f, 2f, 0f)
    PhysicsHandler.timeUntilHorizontalWallCollision(ball, 2f) should be (0.5f)
  }
  
  "timeUntilHorizontalCollision" should "return -1 if the ball won't collide with a wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    PhysicsHandler.timeUntilHorizontalWallCollision(ball, 2f) should be (-1f)
  }
  
  "timeUntilVerticalCollision" should "return the time when a ball will collide with a vertical wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    ball.velocity = Vector3D(2f, 0f, 0f)
    PhysicsHandler.timeUntilVerticalWallCollision(ball, 2f) should be (0.5f)
  }
  
  "timeUntilVerticalCollision" should "return -1 if the ball won't collide with a wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    PhysicsHandler.timeUntilVerticalWallCollision(ball, 2f) should be (-1f)
  }
  
  /** A ball with a radius of 1m */
  private class LargeBall(x: Float, y: Float, z: Float, number: Int) extends Ball(x, y, z, number) {
    override val radius = 1f
  }
  
}