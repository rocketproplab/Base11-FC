package org.rocketproplab.marginalstability.flightcomputer.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestMethods {

  public interface Executable {
    void execute() throws Throwable;
  }

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
