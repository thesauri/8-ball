package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType

class Board extends Rectangle with Renderable {
  
  var x = 0.0
  var y = 0.0
  var width = 2.54
  var height = 1.27
  
  def render(renderer: ShapeRenderer) = {
    renderer.setColor(Styles.BoardColor)
    renderer.set(ShapeType.Filled)
    renderer.rect(x.toFloat, y.toFloat, width.toFloat, height.toFloat)
  }
}