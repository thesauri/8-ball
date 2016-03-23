package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

class Board extends Rectangle with Renderable {

  var x = 0.0f
  var y = 0.0f
  var width = 2.54f
  var height = 1.27f

  def render(renderer: ShapeRenderer, scale: Float) = {
    renderer.setColor(Styles.BoardColor)
    renderer.set(ShapeType.Filled)
    renderer.rect(x * scale, y * scale, width * scale, height * scale)
  }
}
