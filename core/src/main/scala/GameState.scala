package com.walter.eightball

import scala.collection.mutable

/** A mutable class to store and manipulate the current state of the game */
class GameState {

  val balls = mutable.Buffer[Ball]()
  var gameState = GameState.Aiming
  var hasSolids: Option[Int] = None //The player that shoots solids (fully colored balls), None, 1 or 2
  val cueStick = new CueStick(Vector3D(0f, 0f, 0f), 0f, 0f)
  var playerTurn = 1 //Whose turn it is, player 1 or 2

  private var hasPocketedCueBall = false
  private var hasPocketedRightBall = false
  private var hasPocketedWrongBall = false
  private var hasPocketedEightBall = false

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
        case Some(1) => if (playerTurn == 1) hasPocketedRightBall = true else hasPocketedWrongBall = true
        case Some(2) => if (playerTurn == 2) hasPocketedRightBall = true else hasPocketedWrongBall = true
        case None => {
          hasSolids = Some(1)
          hasPocketedRightBall = true
        }
      }
    } else {

      //Striped ball pocketed
      hasSolids match {
        case Some(1) => if (playerTurn == 1) hasPocketedWrongBall = true else hasPocketedRightBall = true
        case Some(2) => if (playerTurn == 2) hasPocketedWrongBall = true else hasPocketedRightBall = true
        case None => {
          hasSolids = Some(2)
          hasPocketedRightBall = true
        }
      }
    }

    balls.remove(balls.indexOf(ball))
  }
}

/** Describes the current state of the game
  *
  * Shooting is the time between the aim and
  * the point that the balls start rolling */
object GameState extends Enumeration {
  val Aiming, Shooting, Rolling = Value
}
