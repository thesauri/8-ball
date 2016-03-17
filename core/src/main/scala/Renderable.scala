package com.walter.eightball

import com.badlogic.gdx.graphics.Camera

/** Represents a renderable object */ 
trait Renderable {
  
  def render(camera: Camera)
  
}