package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class TestVector3 {
  @Test
  public void equalVectorsEqual() {
    Vector3 v1 = new Vector3(0, 1, 2);
    Vector3 v2 = new Vector3(0, 1, 2);
    assertEquals(v1, v2);
  }
  
  @Test
  public void nullNotEqual() {
    Vector3 v1 = new Vector3(0, 1, 2);
    assertFalse(v1.equals(null));
  }
  
  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void stringNotEqual() {
    Vector3 v1 = new Vector3(0, 1, 2);
    assertFalse(v1.equals(""));
  }
  
  @Test
  public void nonEqualNotEqual() {
    Vector3 v1 = new Vector3(0, 1, 2);
    Vector3 v2 = new Vector3(0, 1, 5);
    assertNotEquals(v1, v2);
  }
}
