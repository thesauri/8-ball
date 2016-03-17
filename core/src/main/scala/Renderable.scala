package com.walter.eightball

import com.badlogic.gdx.graphics.glutils.ShapeRenderer

/** Represents a renderable object */ 
trait Renderable {
  
  def render(renderer: ShapeRenderer, scale: Double)
  
}