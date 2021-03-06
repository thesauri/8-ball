package com.walter.eightball

import com.walter.eightball.math.Vector3D
import com.walter.eightball.objects.Ball
import org.scalatest._
import com.walter.eightball.state.PhysicsHandler
import com.walter.eightball.state.PhysicsHandler.CollisionType

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
    PhysicsHandler.timeUntilCollision(ball1, ball2) foreach (_ should be (1f))
  }
  
  "timeUntilCollision" should "return the time to the first of two upcoming collisions" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    ball1.velocity = Vector3D(1f, 0f, 0f)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    PhysicsHandler.timeUntilCollision(ball1, ball2) foreach (_ should be (2f))
  }
  
  "timeUntilCollision" should "return -1 if the balls are not colliding and standing still" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    PhysicsHandler.timeUntilCollision(ball1, ball2) should be (None)
  }
  
  "timeUntilCollision" should "return -1 if the balls are moving but won't collide" in {
    val ball1 = new LargeBall(-2f, 0f, 0f, 1)
    ball1.velocity = Vector3D(0f, 1f, 0f)
    val ball2 = new LargeBall(2f, 0f, 0f, 1)
    PhysicsHandler.timeUntilCollision(ball1, ball2) should be (None)
  }
  
  "timeUntilHorizontalCollision" should "return the time when a ball will collide with a horzintal wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    ball.velocity = Vector3D(0f, 2f, 0f)
    PhysicsHandler.timeUntilHorizontalWallCollision(ball, 2f) should be (Some(0.5f))
  }
  
  "timeUntilHorizontalCollision" should "return None if the ball won't collide with a wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    PhysicsHandler.timeUntilHorizontalWallCollision(ball, 2f) should be (None)
  }
  
  "timeUntilVerticalCollision" should "return the time when a ball will collide with a vertical wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    ball.velocity = Vector3D(2f, 0f, 0f)
    PhysicsHandler.timeUntilVerticalWallCollision(ball, 2f) should be (Some(0.5f))
  }
  
  "timeUntilVerticalCollision" should "return None if the ball won't collide with a wall" in {
    val ball = new LargeBall(0f, 0f, 0f, 1)
    PhysicsHandler.timeUntilVerticalWallCollision(ball, 2f) should be (None)
  }

  "getNextCollisions" should "return None as time and an empty Vector if no collisions will occur" in {
    val ball1 = new LargeBall(200f, 100f, 0f, 1)
    val ball2 = new LargeBall(500f, -100f, 0f, 1)
    val (time, collision) = PhysicsHandler.getNextCollisions(Vector(ball1, ball2))
    time should be (None)
    collision.isEmpty should be (true)
  }

  "getNextCollisions" should "return two collision events if they will occur simultaneously" in {
    val ball1 = new LargeBall(10f, 10f, 0f, 1)
    ball1.velocity = Vector3D(2f, 0f, 0f)
    val ball2 = new LargeBall(10f, 14f, 0f, 1)
    ball2.velocity = Vector3D(2f, 0f, 0f)
    val ball3 = new LargeBall(14f, 10f, 0f, 1)
    val ball4 = new LargeBall(14f, 14f, 0f, 1)
    val (time, collision) = PhysicsHandler.getNextCollisions(Vector(ball1, ball2, ball3, ball4))
    time should be (Some(1f))
    collision.length should be (2)
    collision(0)._1 should be (CollisionType.BallBall)
    collision(1)._1 should be (CollisionType.BallBall)
  }

  "getNextCollisions" should "return the nearest collision if two will occur" in {
    val ball1 = new LargeBall(10f, 10f, 0f, 1)
    ball1.velocity = Vector3D(2f, 0f, 0f)
    val ball2 = new LargeBall(10f, 14f, 0f, 1)
    ball2.velocity = Vector3D(1f, 0f, 0f)
    val ball3 = new LargeBall(14f, 10f, 0f, 1)
    val ball4 = new LargeBall(14f, 14f, 0f, 1)
    val (time, collision) = PhysicsHandler.getNextCollisions(Vector(ball1, ball2, ball3, ball4))
    time should be (Some(1f))
    collision.length should be (1)
    collision(0)._1 should be (CollisionType.BallBall)
  }

  "getNextCollisions" should "detect horizontal wall collisions" in {
    val ball1 = new LargeBall(5f, -2f, 0f, 1)
    ball1.velocity = Vector3D(0f, 1f, 0f)
    val (time, collision) = PhysicsHandler.getNextCollisions(Vector(ball1))
    time should be (Some(1f))
    collision.length should be (1)
    collision(0)._1 should be (CollisionType.HorizontalWall)
  }

  "getNextCollisions" should "detect vertical wall collisions" in {
    val ball1 = new LargeBall(-2f, 5f, 0f, 1)
    ball1.velocity = Vector3D(1f, 0f, 0f)
    val (time, collision) = PhysicsHandler.getNextCollisions(Vector(ball1))
    time should be (Some(1f))
    collision.length should be (1)
    collision(0)._1 should be (CollisionType.VerticalBall)
  }

  "getNextCollisions" should "detect pocketed events" in {
    val ball1 = new Ball(0.1f, 0.1f, 0f, 1)
    ball1.velocity = Vector3D(-0.1f, -0.1f, 0f)
    val (time, collision) = PhysicsHandler.getNextCollisions(Vector(ball1))
    time.isDefined should be (true)
    time.get should be (0.47465f +- 0.001f)
    collision.length should be (1)
    collision(0)._1 should be (CollisionType.Pocketed)
  }

  "timeUntilPocketed" should "return None if the ball won't be pocketed" in {
    val ball1 = new Ball(1f, 1f, 0f, 1)
    PhysicsHandler.timeUntilPocketed(ball1) should be (None)
  }

  "timeUntilPocketed" should "return the time until the ball will be pocketed" in {
    val ball1 = new Ball(0.1f, 0.1f, 0f, 1)
    ball1.velocity = Vector3D(-0.1f, -0.1f, 0f)
    val result = PhysicsHandler.timeUntilPocketed(ball1)
    result.isDefined should be (true)
    result.get should be (0.47465f +- 0.001f)
  }

  "areStill" should "return true if all balls are still" in {
    val ball1 = new Ball(0f, 0f, 0f, 1)
    val ball2 = new Ball(10f, 10f, 10f, 1)
    PhysicsHandler.areStill(Vector(ball1, ball2)) should be (true)
  }

  "areStill" should "return true if there are moving balls" in {
    val ball1 = new Ball(0f, 0f, 0f, 1)
    val ball2 = new Ball(10f, 10f, 10f, 1)
    ball2.velocity = Vector3D(2f, 0f, 0f)
    PhysicsHandler.areStill(Vector(ball1, ball2)) should be (false)
  }
  
  /** A ball with a radius of 1m and a mass of 1 kg */
  private class LargeBall(x: Float, y: Float, z: Float, number: Int) extends Ball(x, y, z, number) {
    override val mass = 1f
    override val radius = 1f
  }
  
}