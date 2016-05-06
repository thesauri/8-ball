package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

/** Represents the in-game board */
object Board {

  val Height = 1.17f
  val Width = 2.34f
  val PocketRadius = 1.6f * Ball.Radius //1.6x ball radius

  /** Renders the board
    *
    * @param renderer The shape renderer to use
    * @param scale    Screen pixels per in-game meter
    */
  def render(renderer: ShapeRenderer, scale: Float) = {
    renderer.setColor(Styles.BoardColor)

    //Draw the board
    renderer.set(ShapeType.Filled)
    renderer.rect(0f, 0f, Board.Width * scale, Board.Height * scale)

    //Draw the pockets
    renderer.setColor(Styles.PocketColor)

    for (px <- 0.0f to 1.0f by 0.5f;
         py <- 0.0f to 1.0f by 1f) {
      renderer.circle(px * Board.Width * scale, py * Board.Height * scale, scale * Board.PocketRadius)
    }
  }
}