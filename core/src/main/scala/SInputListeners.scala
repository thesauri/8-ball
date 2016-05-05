package com.walter.eightball

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.{InputEvent, InputListener}

/** Scala-like input listeners. Instead of implementing an interface for input events,
  * simply pass the function to be executed on the desired event  */
object SInputListeners {

  def click(f: => Unit): ClickListener = {

    class cListener extends ClickListener {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = f
    }

    new cListener

  }

  def click(f: (InputEvent) => Unit): ClickListener = {

    class cListener extends ClickListener {
      override def clicked(event: InputEvent, x: Float, y: Float): Unit = f(event)
    }

    new cListener

  }

  def touchDown(f: => Boolean): InputListener = {

    class tdListener extends InputListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean = f
    }

    new tdListener

  }

  def touchDown(f: (InputEvent) => Boolean): InputListener = {

    class tdListener extends InputListener {
      override def touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean =
        f(event)
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

  def touchDragged(f: (InputEvent, Float, Float, Int) => Unit): InputListener = {

    class tdListener extends InputListener {
      override def touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int): Unit =
        f(event, x, y, pointer)
    }

    new tdListener

  }

}
