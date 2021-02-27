package org.rocketproplab.marginalstability.flightcomputer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.function.Executable;

public class TestMethods {

  public static <E> void assertThrows(Class<E> clazz, Executable executable) {
    try {
      executable.execute();
    } catch (Throwable exception) {
      assertEquals(clazz, exception.getClass());
      return;
    }
    fail();
  }
  
}
