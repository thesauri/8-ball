package com.walter.eightball

import scala.collection.mutable

/** A mutable class to store and manipulate the current state of the game */
class GameState {

  val balls = mutable.Buffer[Ball]()
  var gameState = GameState.Aiming
  var hasSolids: Option[Int] = None //The player that shoots solids (fully colored balls), None, 1 or 2
  val cueStick = new CueStick(Vector3D(0f, 0f, 0f), 0f, 0f)

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
}

/** Describes the current state of the game
  *
  * Shooting is the time between the aim and
  * the point that the balls start rolling */
object GameState extends Enumeration {
  val Aiming, Shooting, Rolling = Value
}
