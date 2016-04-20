package com.walter.eightball

/** Describes the current state of a ball
 *  
 *  Sliding: v ≠ ωr
 *  Rolling: v = ωr
 *  Still:   v = 0
 */
object BallState extends Enumeration {
  val Sliding, Rolling, Still = Value
}