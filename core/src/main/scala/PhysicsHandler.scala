package com.walter.eightball

import scala.math._
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map

object PhysicsHandler {
  
  val g = 9.8f //Gravitational constant
  val cfc = 0.1f //Coefficient of friction between two colliding balls
  val cfs = 0.2f //Coefficient of friction while sliding
  val cfr = 0.01f //Coefficient of friction while rolling
  val td = 0.0002f //Duration of a collision between two balls
  val separationOffset = 0.001f //Minimum separation between objects when calling method separate

  case class VelocityState(velocity: Vector3D, angularVelocity: Vector3D)
  
  /** Updates the velocities of the balls after a collision
   *  
   *  Both the velocity and the angular velocity of the balls
   *  are updated. The collision is assumed to be perfectly
   *  elastic, conserving both the total kinetic energy,
   *  momentum, and angular momentum. */
  def collide(ball1: Ball, ball2: Ball): Unit = {
    val n = 1f/(ball2 - ball1).norm * (ball2 - ball1)
    val nNorm = n.normalized
    
    //Calculate the normal components of the velocity vectors
    val vn1 = (ball1.velocity dot (-1f * n))*(-1f * n)
    val vn2 = (ball2.velocity dot n)*n
    
    //Calculate the tangential components of the velocity vectors
    val vt1 = ball1.velocity - vn1
    val vt2 = ball2.velocity - vn2
    
    /* Add the tangential component of the velocity vector with the
     * normal component of the other vector to get the resulting velocity */
    ball1.velocity = vt1 + vn2
    ball2.velocity = vt2 + vn1
    
    //Vectors to the touching points between the balls
    val r1 = ball1.radius * nNorm
    val r2 = -1f * ball2.radius * nNorm
    
    //Relative speed at the point of contact
    val vpr = (r2 cross ball2.angularVelocity) - (r1 cross ball1.angularVelocity)
    
    //∆v of the normal velocities before and after the collisions
    val dvn1 = signum((vn2 - vn1) dot (nNorm)) * (vn2 - vn1).norm
    val dvn2 = signum((vn1 - vn2) dot (nNorm)) * (vn1 - vn2).norm
    
    //Calculate the new angular speeds
    ball1.angularVelocity += (5f/2f) * (r1 cross (1/td * -cfc * (ball1.mass*dvn2) * (vpr + vt1).normalized)) *
                             (td/(ball1.mass * pow(ball1.radius.toDouble, 2).toFloat))
    ball2.angularVelocity += (5f/2f) * (r2 cross (1/td * -cfc * (ball2.mass*dvn1) *
                             (vpr + vt2).normalized))*(td/(ball2.mass * pow(ball2.radius.toDouble, 2).toFloat))
    ()
  }

  /**  Returns the resulting delta velocities of a collision
    *
    *  Both the velocity and the angular velocity of the balls
    *  are updated. The collision is assumed to be perfectly
    *  elastic, conserving both the total kinetic energy,
    *  momentum, and angular momentum. */
  def collideImmutable(ball1: Ball, ball2: Ball): (VelocityState, VelocityState) = {
    val n = 1f/(ball2 - ball1).norm * (ball2 - ball1)
    val nNorm = n.normalized

    //Calculate the normal components of the velocity vectors
    val vn1 = (ball1.velocity dot (-1f * n))*(-1f * n)
    val vn2 = (ball2.velocity dot n)*n

    //Calculate the tangential components of the velocity vectors
    val vt1 = ball1.velocity - vn1
    val vt2 = ball2.velocity - vn2

    /* Add the tangential component of the velocity vector with the
     * normal component of the other vector to get the resulting velocity */
    val newDVelocity1 = vn2 - vn1
    val newDVelocity2 = vn1 - vn2

    //Vectors to the touching points between the balls
    val r1 = ball1.radius * nNorm
    val r2 = -1f * ball2.radius * nNorm

    //Relative speed at the point of contact
    val vpr = (r2 cross ball2.angularVelocity) - (r1 cross ball1.angularVelocity)

    //∆v of the normal velocities before and after the collisions
    val dvn1 = signum((vn2 - vn1) dot (nNorm)) * (vn2 - vn1).norm
    val dvn2 = signum((vn1 - vn2) dot (nNorm)) * (vn1 - vn2).norm

    //Calculate the new angular speeds
    val newDAngularVelocity1 = (5f/2f) * (r1 cross (1/td * -cfc * (ball1.mass*dvn2) * (vpr + vt1).normalized)) *
      (td/(ball1.mass * pow(ball1.radius.toDouble, 2).toFloat))
    val newDAngularVelocity2 = (5f/2f) * (r2 cross (1/td * -cfc * (ball2.mass*dvn1) *
      (vpr + vt2).normalized))*(td/(ball2.mass * pow(ball2.radius.toDouble, 2).toFloat))

    (new VelocityState(newDVelocity1, newDAngularVelocity1), new VelocityState(newDVelocity2, newDAngularVelocity2))
  }
  
  /** Returns the relative velocity between the table and the touching point of the ball
   *  
   *  This velocity is determined by: (ω x R) + v
   *  where: ω is the angular velocity 
   *         R is a vector from the center of the ball to the touching point with the board (0, 0, -r) */
  def getRelativeVelocity(ball: Ball): Vector3D =
    (ball.angularVelocity cross Vector3D(0f, 0f, -ball.radius)) + ball.velocity
  
  /** Moves the given balls according to their velocities
   *  
   *  @param balls the balls to move
   *  @param t time since last execution (in seconds) */
  def moveBalls(balls: Seq[Ball], t: Float): Unit = {
    balls foreach (ball => ball += t * ball.velocity)
  }

  /** Separates two overlapping balls */
  def separate(ball1: Ball, ball2: Ball): Unit = {
    if ((ball2 - ball1).norm <= ball1.radius + ball2.radius) {
      //Desired displacement between the balls
      val newD = (ball1.radius + ball2.radius + separationOffset) * (ball2 - ball1).normalized
      ball2 += newD - (ball2 - ball1)
    }
  }
  
  /** Returns the time until the next collision between two balls
   *  
   *  The collision time is determined by treating the balls as if they were having a linear
   *  trajectory. This allows us to use a quadratic equation to determine the distance d
   *  between the balls for a certain time t. The potential collision occurs when the
   *  distance between the balls is the sum of their radii.
   *  
   *  The equation is as following:
   *  
   *  d(t) = t^2 (∆v.∆v) + 2t (∆r.∆v) + (∆r.∆r) - (R1+R1)^2 
   *  where: ∆r is the difference in position between the balls
   *  			 R  are the radii of the balls
   *  
   *  This method is based on the following source:
   *  http://twobitcoder.blogspot.fi/2010/04/circle-collision-detection.html */
  def timeUntilCollision(ball1: Ball, ball2: Ball): Option[Float] = {
    
    val v12 = ball1.velocity - ball2.velocity
    val r12 = ball1 - ball2
    
    val a = v12 dot v12
    val b = 2 * (r12 dot v12)
    val c = (r12 dot r12) - pow(ball1.radius.toDouble + ball2.radius, 2).toFloat
    
    val disc = pow(b.toDouble, 2).toFloat - 4*a*c


    if ((ball2 - ball1).norm < ball1.radius + ball2.radius) {
      None
    } else if (disc < 0f) {
      //No real solutions => no upcoming collisions
      None
    } else if (disc == 0f) {
      //Both velocities are zero => they won't collide
      if (2*a == 0f || -b/(2*a) < 0f) {
        None
      } else {
        //Otherwise calculate the collision time
        Some(-b/(2*a))
      }
    } else {
      //Two upcoming collisions, choose the next one (but not any past solutions)
      val t1 = ((-b - sqrt(disc.toDouble).toFloat)/(2*a))
      val t2 = ((-b + sqrt(disc.toDouble).toFloat)/(2*a))
      val tmp = min(if (t1 < 0f) Float.MaxValue else t1,
                    if (t2 < 0f) Float.MaxValue else t2)
      if (tmp == Float.MaxValue) None else Some(tmp)
    }
  }
  
  /** Returns the time when the ball will collide with a horizontal wall (-1 if no collision)
   *  
   *  The wall is assumed to be infinitely wide (which is OK as the
   *  game board is enclosed anyways)
   *  
   *  @param ball the ball
   *  @param wallY the y coordinate of the ball */
  def timeUntilHorizontalWallCollision(ball: Ball, wallY: Float): Float = {
    if (ball.velocity.y == 0f) {
      -1f
    } else {
      val t = (wallY - ball.radius - ball.y)/ball.velocity.y
      if (t < 0f) {
        -1f
      } else {
        t
      }
    }
  }
  
  /** Returns the time when the ball will collide with a vertical wall (-1 if no collision)
   *  
   *  The wall is assumed to be infinitely tall (which is OK as the
   *  game board is enclosed anyways)
   *  
   *  @param ball the ball
   *  @param wallX the x coordinate of the ball */
  def timeUntilVerticalWallCollision(ball: Ball, wallX: Float): Float = {
    if (ball.velocity.x == 0f) {
      -1f
    } else {
      val t = (wallX - ball.radius - ball.x)/ball.velocity.x
      if (t < 0f) {
        -1f
      } else {
        t
      }
    }
  }

  /** Updates the state of the balls
    *
    *  Call once every step
    *
    *  @param balls the balls to update
    *  @param t the duration of the time step
    */
  def update(balls: Seq[Ball], t: Float): Unit = {

    updateVelocities(balls, t)

    def applyCollisionsRecursive(rt: Float, depth: Int = 0): Unit = {

      var nextCollision: Option[Float] = None
      val collisions = Buffer[(Ball, Ball)]()

      //Checks all unordered pairs and finds the next set of balls that will collide
      for (i <- 0 until balls.size) {
        for (n <- i + 1 until balls.size) {
          val time = timeUntilCollision(balls(i), balls(n))
          if (time.exists(ct => nextCollision.forall(ct < _))) {
            collisions.clear()
            nextCollision = time
            collisions += ((balls(i), balls(n)))
          } else if (time.exists(ct => nextCollision.forall(ct == _))) {
            collisions += ((balls(i), balls(n)))
          }
        }
      }

      if (nextCollision.exists( _ < rt )) {

        val newVelocity = Map[Ball, Vector[Vector3D]]().withDefaultValue(Vector[Vector3D]())
        val newAngularVelocity = Map[Ball, Vector[Vector3D]]().withDefaultValue(Vector[Vector3D]())

        //A collision is going to occur this timestep
        for (collidingPair <- collisions) {

          val result = collideImmutable(collidingPair._1, collidingPair._2)

          newVelocity += collidingPair._1 -> (newVelocity(collidingPair._1) :+ result._1.velocity)
          newVelocity += collidingPair._2 -> (newVelocity(collidingPair._2) :+ result._2.velocity)

          newAngularVelocity += collidingPair._1 -> (newAngularVelocity(collidingPair._1) :+ result._1.angularVelocity)
          newAngularVelocity += collidingPair._2 -> (newAngularVelocity(collidingPair._2) :+ result._2.angularVelocity)
        }

        //Move the balls to collision positions before updating the velocities
        nextCollision foreach { ct => {
          //updateVelocities(balls, ct)
          moveBalls(balls, ct)
        }}

        //Update the velocity
        newVelocity foreach { case (ball, dVelocities) => {
          val p = (1f / dVelocities.size)
          ball.velocity +=  p * dVelocities.foldLeft(Vector3D(0f, 0f, 0f))(_ + _)
        }}

        //Update the angular velocity
        newAngularVelocity foreach { case (ball, dAVelocities) => {
          val p = 1f / dAVelocities.size
          ball.angularVelocity += p * dAVelocities.foldLeft(Vector3D(0f, 0f, 0f))(_ + _)
        }}

        if (depth < 100) {
          applyCollisionsRecursive(rt - nextCollision.get, depth + 1)
        }

      } else {
        moveBalls(balls, rt)
      }
    }

    applyCollisionsRecursive(t)

  }

  /** Updates the velocities of the given balls
    *
    *  The logic is based on the equations from the following link:
    *  http://archive.ncsa.illinois.edu/Classes/MATH198/townsend/math.html
    *
    *  @param balls the balls to update
    *  @param t time since last execution (in seconds) */
  def updateVelocities(balls: Seq[Ball], t: Float): Unit = {
    balls.foreach {
      ball => {

        /* Determine if the ball is rolling or not, this is
         * deemed to be the case if the relative velocity
         * between the edge of the ball and the table is
         * almost zero. */

        if (getRelativeVelocity(ball).norm <= 0.02f) {

          //Calculate the new velocity according to ∆v = -µg (v/|v|) ∆t
          val newVelocity = ball.velocity + (-cfr * 9.8f * ball.velocity.normalized * t)

          //If both the velocity and the angular velocity are almost zero, stop the ball completely
          if (ball.velocity.norm < 0.01f && ball.angularVelocity.norm < 0.01f ) {
            ball.velocity = Vector3D(0f, 0f, 0f)
            ball.angularVelocity = Vector3D(0f, 0f, 0f)
            ball.state = BallState.Still
          } else {

            //Otherwise update the velocities
            ball.velocity = newVelocity

            //As the ball is rolling, the angular velocity should equal the velocity
            ball.angularVelocity = Vector3D(-newVelocity.y / ball.radius, newVelocity.x / ball.radius, 0)
          }
        } else {

          //--Sliding ball--
          val pv = getRelativeVelocity(ball).normalized

          //Calculate the new velocity according to ∆v = -µg (v/|v|) ∆t
          ball.velocity += -cfs * 9.8f * pv * t

          /* Calculate the new angular velocity using ∆ω = 5/2 (R x (-µmgr (v/|v|))) ∆t/(m*r^2)
           * where R is a vector pointing from the middle of the ball to the touching point
           * with the table (0,0,-r) */
          ball.angularVelocity += (5f * t / (2f * ball.mass * pow(ball.radius.toDouble, 2).toFloat)) * (Vector3D(0f,0f,-ball.radius) cross (-cfs * ball.mass * g * ball.radius * pv))
        }
      }
    }
  }
  
}