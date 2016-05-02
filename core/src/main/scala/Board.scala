package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

class Board(var x: Float = 0.0f, var y: Float = 0.0f) extends Vector2D with Rectangle with Renderable {

  var height = Board.Height
  var width = Board.Width

  def render(renderer: ShapeRenderer, scale: Float) = {
    renderer.setColor(Styles.BoardColor)

    //Draw the board
    renderer.set(ShapeType.Filled)
    renderer.rect(x * scale, y * scale, width * scale, height * scale)

    //Draw the pockets
    renderer.setColor(Styles.PocketColor)

    for (px <- (0.0f to 1.0f by 0.5f);
         py <- (0.0f to 1.0f by 0.5f)) {
      if (px != 0.5f && py != 0.5f)
        renderer.circle(px * Board.Width * scale, py * Board.Height * scale, scale * Board.PocketRadius)
    }


  }
}

object Board {

  val Height = 1.17f
  val Width = 2.34f
  val PocketRadius = 1.6f * Ball.Radius //1.6x ball radius

}