package com.walter.eightball

import org.scalatest._

class Vector3DTest extends FlatSpec with Matchers {

  "+" should "return the sum of two vectors" in {
    val v = Vector3D(2f, 3f, -2f) + Vector3D(10f, 2f, 2f)
    v should be (Vector3D(12f, 5f, 0f))
  }
  
  "+=" should "add a vector to an existing vector" in {
    val v = Vector3D(10f, 2f, 10f)
    val expectedResult = Vector3D(12f, 7f, 5f)
    (v += Vector3D(2f, 5f, -5f)) should be (expectedResult)
    v should be (expectedResult)
  }
  
  "*" should "return the vector multiplied by a constant" in {
    (Vector3D(12f, 6f, 4f) * 0.25f) should be (Vector3D(3f, 1.5f, 1f))
  }
  
  
  "float * vector" should "be supported" in {
    (0.25f * Vector3D(12f, 6f, 4f)) should be (Vector3D(3f, 1.5f, 1f))
  }
}