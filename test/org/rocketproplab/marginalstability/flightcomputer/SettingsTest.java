package org.rocketproplab.marginalstability.flightcomputer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SettingsTest {
  @UserSetting(comment = "An Integer", units = "mph")
  public static int INT_FIELD;

  @UserSetting(comment = "The double array", units = "kpsi")
  public static double[] ARRAY_FIELD = new double[0];

  @UserSetting(name = "BOOL", comment = "True or False?", units = "")
  public static boolean BOOLEAN_FIELD;

  @SettingSectionHeader(name = "Best Settings")
  @UserSetting(comment = "String", units = "String")
  public static String HEADER_FIELD;

  @Test
  public void fieldAsStringGivesHeaderForField() throws NoSuchFieldException, SecurityException {
    INT_FIELD = 0;
    Field  field       = SettingsTest.class.getField("INT_FIELD");
    String fieldString = Settings.fieldAsString(field);
    String expected    = "# Type: int\n" + "# Description: An Integer\n" + "# Units: mph\n" + "INT_FIELD=0\n";
    assertEquals(expected, fieldString);
  }

  @Test
  public void fieldAsStringGivesHeaderForFieldWithChangedValue() throws NoSuchFieldException, SecurityException {
    INT_FIELD = 5324;
    Field  field       = SettingsTest.class.getField("INT_FIELD");
    String fieldString = Settings.fieldAsString(field);
    String expected    = "# Type: int\n" + "# Description: An Integer\n" + "# Units: mph\n" + "INT_FIELD=5324\n";
    assertEquals(expected, fieldString);
  }

  @Test
  public void fieldAsStringForArray() throws NoSuchFieldException, SecurityException {
    ARRAY_FIELD = new double[] { 0.1, 35, 1000, -3 };
    Field  field       = SettingsTest.class.getField("ARRAY_FIELD");
    String fieldString = Settings.fieldAsString(field);
    String expected    = "# Type: double[4]\n" + "# Description: The double array\n" + "# Units: kpsi\n"
        + "ARRAY_FIELD=[0.1,\t35.0,\t1000.0,\t-3.0]\n";
    assertEquals(expected, fieldString);
  }

  @Test
  public void fieldAsStringForArrayLenZero() throws NoSuchFieldException, SecurityException {
    ARRAY_FIELD = new double[] {};
    Field  field       = SettingsTest.class.getField("ARRAY_FIELD");
    String fieldString = Settings.fieldAsString(field);
    String expected    = "# Type: double[0]\n" + "# Description: The double array\n" + "# Units: kpsi\n"
        + "ARRAY_FIELD=[]\n";
    assertEquals(expected, fieldString);
  }

  @Test
  public void fieldAsStringForBoolean() throws NoSuchFieldException, SecurityException {
    BOOLEAN_FIELD = true;
    Field  field       = SettingsTest.class.getField("BOOLEAN_FIELD");
    String fieldString = Settings.fieldAsString(field);
    String expected    = "# Type: boolean\n" + "# Description: True or False?\n" + "# Units: \n" + "BOOL=true\n";
    assertEquals(expected, fieldString);
  }

  @Test
  public void sectionHeaderAdded() throws NoSuchFieldException, SecurityException {
    HEADER_FIELD = "Best Exclamation Ever!";
    Field  field       = SettingsTest.class.getField("HEADER_FIELD");
    String fieldString = Settings.fieldAsString(field);
    String expected    = "#----------------\n" + "# Best Settings |\n" + "#----------------\n\n"
        + "# Type: class java.lang.String\n" + "# Description: String\n" + "# Units: String\n"
        + "HEADER_FIELD=Best Exclamation Ever!\n";
    assertEquals(expected, fieldString);
  }

  @Test
  public void usefulLinesDontContainHashes() {
    String       lines       = "A line\n" + "# Not# a line $\n" + "Starts but # has a hash $\n" + "";
    List<String> allLines    = Arrays.asList(lines.split("\n"));
    Set<String>  usefulLines = Settings.getUsefulLinesFromConfig(allLines);
    assertEquals(2, usefulLines.size());
    assertTrue(usefulLines.contains("A line"));
    assertTrue(usefulLines.contains("Starts but"));
  }

  @Test
  public void fieldValueMapContainsSeperatedLines() {
    String              lines    = "A=32\n" + "Be=Hello World=\n" + "Bad Line!";
    List<String>        lineList = Arrays.asList(lines.split("\n"));
    Map<String, String> valueMap = Settings.getFieldNameValueMap(lineList);
    assertEquals(2, valueMap.size());
    assertTrue(valueMap.containsKey("A"));
    assertEquals("32", valueMap.get("A"));
    assertTrue(valueMap.containsKey("Be"));
    assertEquals("Hello World=", valueMap.get("Be"));
  }

}
