package com.walter.eightball

/** Describes the current state of the game
  *
  * Shooting is the time between the aim and
  * the point that the balls start rolling */
@SerialVersionUID(1L)
object GameStateType extends Enumeration with Serializable {
  val Aiming, Rolling, Lost = Value
}
