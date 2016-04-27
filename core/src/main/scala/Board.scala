package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

class Board(var x: Float = 0.0f, var y: Float = 0.0f) extends Vector2D with Rectangle with Renderable {

  var height = Board.Height
  var width = Board.Width

  def render(renderer: ShapeRenderer, scale: Float) = {
    renderer.setColor(Styles.BoardColor)
    renderer.set(ShapeType.Filled)
    renderer.rect(x * scale, y * scale, width * scale, height * scale)
  }
}

object Board {

  val Height = 1.17f
  val Width = 2.34f
  val PocketRadius = 1.6f * Ball.Radius //1.6x ball radius

}