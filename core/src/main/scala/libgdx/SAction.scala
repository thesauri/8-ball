package com.walter.eightball.libgdx

import com.badlogic.gdx.scenes.scene2d.Action

/** Scala-like libgdx Action. Works by passing a function literal to be executed every act(deltaTime) */
object SAction {

  class SActionImplementation(f: (Float) => Boolean) extends Action {
    def act(delta: Float): Boolean = f(delta)
  }

  def apply(f: => Boolean): Action = new SActionImplementation( (t: Float) => f )
  def apply(f: (Float) => Boolean): Action = new SActionImplementation( f )

}
