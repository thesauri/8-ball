package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Represents a renderable object */
@SerialVersionUID(1L)
trait Renderable extends Serializable {

  def render(renderer: ShapeRenderer, scale: Float): Unit

}
