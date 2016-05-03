package com.walter.eightball

import scala.collection.mutable

/** A mutable class to store and manipulate the current state of the game */
class GameState {

  val balls = mutable.Buffer[Ball]()
  var gameState = GameState.Aiming
  var hasSolids: Option[Int] = None //The player that shoots solids (fully colored balls), None, 1 or 2
  val cueStick = new CueStick(Vector3D(0f, 0f, 0f), 0f, 0f)
  var isPlayer1sTurn = true //Whether it's player 1's turn or not

  private var hasPocketedCueBall = false
  private var hasPocketedRightBall = false
  private var hasPocketedWrongBall = false
  private var hasPocketedEightBall = false

  /** Advances the game to the next round */
  def nextRound(): Unit = {
    if (hasPocketedEightBall) {
      gameState = GameState.Lost
    } else if (hasPocketedCueBall) {
      balls += new Ball(0.25f, 0.635f, 0f, 0)
      isPlayer1sTurn = !isPlayer1sTurn
    } else if (!hasPocketedWrongBall || !hasPocketedRightBall) {
      isPlayer1sTurn = !isPlayer1sTurn
    }

    hasPocketedCueBall = false
    hasPocketedRightBall = false
    hasPocketedWrongBall = false
    hasPocketedEightBall = false

    gameState = GameState.Aiming
  }

  /** Adds balls to the game state at their default positions */
  def placeBallsAtDefaultPositions(): Unit = {
    balls += new Ball(0.25f, 0.635f, 0f, 0)

    balls += new Ball(1.69f, 0.635f, 0f, 1)

    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 15)
    balls += new Ball(1.69f + 2.02f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 2)

    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 14)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f, 0f, 8)
    balls += new Ball(1.69f + 4.04f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 13)

    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 3.03f * balls(0).radius, 0f, 4)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f - 1.02f * balls(0).radius, 0f, 12)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 1.02f * balls(0).radius, 0f, 5)
    balls += new Ball(1.69f + 6.06f * balls(0).radius, 0.635f + 3.03f * balls(0).radius, 0f, 11)

    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 4.04f * balls(0).radius, 0f, 6)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f - 2.02f * balls(0).radius, 0f, 10)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f, 0f, 7)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 2.02f * balls(0).radius, 0f, 9)
    balls += new Ball(1.69f + 8.08f * balls(0).radius, 0.635f + 4.04f * balls(0).radius, 0f, 3)
  }

  /** Returns the number of balls that player 1 has remaining */
  def remainingBallsPlayer1: Int = hasSolids match {
      case Some(1) => balls.count( _.number <= 8 )
      case Some(2) => balls.count( _.number >= 8 )
      case None => 8
  }

  /** Returns the number of balls that player 1 has remaining */
  def remainingBallsPlayer2: Int = hasSolids match {
    case Some(1) => balls.count( _.number >= 8 )
    case Some(2) => balls.count( _.number <= 8 )
    case None => 8
  }

  /** Removes a ball from the state
    *
    * If the cue ball is removed, it will be re-added
    * in the beginning of the next round when the
    * nextRound method is called.
    *
    * This method also makes sure that turns will
    * switch properly the next round depending on
    * what balls were pocketed. */
  def removeBall(ball: Ball): Unit = {
    require(balls.contains(ball), s"The game state does not contain the ball $ball")

    if (ball.number == 0) {
      hasPocketedCueBall = true
    } else if (ball.number == 8) {
      hasPocketedEightBall = true
    } else if (ball.number < 8) {

      //Solid ball pocketed
      hasSolids match {
        case Some(1) => if (isPlayer1sTurn) hasPocketedRightBall = true else hasPocketedWrongBall = true
        case Some(2) => if (!isPlayer1sTurn) hasPocketedRightBall = true else hasPocketedWrongBall = true
        case None => {
          hasSolids = Some(1)
          hasPocketedRightBall = true
        }
      }
    } else {

      //Striped ball pocketed
      hasSolids match {
        case Some(1) => if (isPlayer1sTurn) hasPocketedWrongBall = true else hasPocketedRightBall = true
        case Some(2) => if (!isPlayer1sTurn) hasPocketedWrongBall = true else hasPocketedRightBall = true
        case None => {
          hasSolids = Some(2)
          hasPocketedRightBall = true
        }
      }
    }

    balls.remove(balls.indexOf(ball))
  }

  /** Returns true if the ball should be shot by the current player */
  def shouldBeShot(ball: Ball): Boolean = hasSolids match {
    case Some(1) => ball.number <= 8 && isPlayer1sTurn || ball.number >= 8 && !isPlayer1sTurn || ball.number == 0
    case Some(2) => ball.number <= 8 && !isPlayer1sTurn || ball.number >= 8 && isPlayer1sTurn || ball.number == 0
    case None => true
  }
}

/** Describes the current state of the game
  *
  * Shooting is the time between the aim and
  * the point that the balls start rolling */
object GameState extends Enumeration {
  val Aiming, Rolling, Lost = Value
}
