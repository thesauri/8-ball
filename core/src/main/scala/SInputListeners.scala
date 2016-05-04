package com.walter.eightball

import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}

/** Scala-like input listeners. Instead of implementing an interface for input events,
  * simply pass the function to be executed on the desired event  */
object SInputListeners {

  def touchDown(f: () => Boolean): InputListener = {

    class tdListener extends InputListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = f()
    }

    new tdListener

  }

  def touchDown(f: (InputEvent, Float, Float, Int, Int) => Boolean): InputListener = {

    class tdListener extends InputListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean =
        f(event, x, y, pointer, button)
    }

    new tdListener

  }

}
