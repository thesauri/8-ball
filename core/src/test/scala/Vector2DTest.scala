package com.walter.eightball

import org.scalatest._
import com.walter.eightball.Vector2D

class Vector2DTest extends FlatSpec with Matchers {

  "+" should "return the sum of two vectors" in {
    val v = Vector2D(2f, 3f) + Vector2D(10f, 2f)
    v should be (Vector2D(12f, 5f))
  }
  
  "+=" should "add a vector to an existing vector" in {
    val v = Vector2D(10f, 2f)
    val expectedResult = Vector2D(12f, 7f)
    (v += Vector2D(2f, 5f)) should be (expectedResult)
    v should be (expectedResult)
  }
  
  "*" should "return the vector multiplied by a constant" in {
    (Vector2D(12f, 6f) * 0.25f) should be (Vector2D(3f, 1.5f))
  }
  
  
  "float * vector" should "be supported" in {
    (0.25f * Vector2D(12f, 6f)) should be (Vector2D(3f, 1.5f))
  }
}