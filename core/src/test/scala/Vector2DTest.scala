package com.walter.eightball

import org.scalatest._

class Vector2DTest extends FlatSpec with Matchers {

  "+" should "return the sum of two vectors" in {
    val v = Vector2D(2f, 3f) + Vector2D(10f, 2f)
    v should be (Vector2D(12f, 5f))
  }
  
}