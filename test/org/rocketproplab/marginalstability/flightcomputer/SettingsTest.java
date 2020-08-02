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

  @UserSetting(comment = "double", units = "number")
  public static double DOUBLE_FIELD = 0;

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

  @Test
  public void setFieldDoubleArraySetsCorrectLengthFieldOfDoubles()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String array = "[5.2, 3, 235.1e5]";
    SettingsTest.ARRAY_FIELD = new double[3];
    Field arrayField = SettingsTest.class.getField("ARRAY_FIELD");
    Settings.setFieldDoubleArray(arrayField, array);
    assertEquals(5.2, SettingsTest.ARRAY_FIELD[0], 0.000001);
    assertEquals(3, SettingsTest.ARRAY_FIELD[1], 0.000001);
    assertEquals(235.1e5, SettingsTest.ARRAY_FIELD[2], 0.000001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void setFieldDoubleArrayThrowsIllegalArgument()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String array = "[5.2, 3a, 235.1e5]";
    SettingsTest.ARRAY_FIELD = new double[3];
    Field arrayField = SettingsTest.class.getField("ARRAY_FIELD");
    Settings.setFieldDoubleArray(arrayField, array);
  }

  @Test
  public void setFieldDoubleArrayFillsUpToGiven()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String array = "[5.2, 3]";
    SettingsTest.ARRAY_FIELD = new double[3];
    Field arrayField = SettingsTest.class.getField("ARRAY_FIELD");
    Settings.setFieldDoubleArray(arrayField, array);
    assertEquals(5.2, SettingsTest.ARRAY_FIELD[0], 0.000001);
    assertEquals(3, SettingsTest.ARRAY_FIELD[1], 0.000001);
    assertEquals(0, SettingsTest.ARRAY_FIELD[2], 0.000001);
  }

  @Test
  public void setFieldDoubleArrayFillsUpToMax()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String array = "[5.2, 3, 3, 2, 1, 3]";
    SettingsTest.ARRAY_FIELD = new double[3];
    Field arrayField = SettingsTest.class.getField("ARRAY_FIELD");
    Settings.setFieldDoubleArray(arrayField, array);
    assertEquals(5.2, SettingsTest.ARRAY_FIELD[0], 0.000001);
    assertEquals(3, SettingsTest.ARRAY_FIELD[1], 0.000001);
    assertEquals(3, SettingsTest.ARRAY_FIELD[2], 0.000001);
  }

  @Test
  public void setFieldDoubleSetsTheField()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String line = "854.3";
    SettingsTest.DOUBLE_FIELD = 0;
    Field field = SettingsTest.class.getField("DOUBLE_FIELD");
    Settings.setFieldDouble(field, line);
    assertEquals(854.3, SettingsTest.DOUBLE_FIELD, 0.000001);
  }

  @Test
  public void setFieldIntSetsTheField()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String line = "52";
    SettingsTest.INT_FIELD = 0;
    Field field = SettingsTest.class.getField("INT_FIELD");
    Settings.setFieldInt(field, line);
    assertEquals(52, SettingsTest.INT_FIELD);
  }

  @Test
  public void setFieldFromConfigLineSetsDouble() throws NoSuchFieldException, SecurityException {
    String line = "854.3";
    SettingsTest.DOUBLE_FIELD = 0;
    Field field = SettingsTest.class.getField("DOUBLE_FIELD");
    Settings.setFieldFromConfigLine(field, line);
    assertEquals(854.3, SettingsTest.DOUBLE_FIELD, 0.000001);
  }

  @Test
  public void setFieldFromConfigLineSetsInt() throws NoSuchFieldException, SecurityException {
    String line = " 1337 ";
    SettingsTest.INT_FIELD = 0;
    Field field = SettingsTest.class.getField("INT_FIELD");
    Settings.setFieldFromConfigLine(field, line);
    assertEquals(1337, SettingsTest.INT_FIELD);
  }

  @Test
  public void setFieldFromConfigLineSetDoubleArray()
      throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    String array = "[5.2, 3, 235.1e5]";
    SettingsTest.ARRAY_FIELD = new double[3];
    Field arrayField = SettingsTest.class.getField("ARRAY_FIELD");
    Settings.setFieldFromConfigLine(arrayField, array);
    assertEquals(5.2, SettingsTest.ARRAY_FIELD[0], 0.000001);
    assertEquals(3, SettingsTest.ARRAY_FIELD[1], 0.000001);
    assertEquals(235.1e5, SettingsTest.ARRAY_FIELD[2], 0.000001);
  }
  
  @Test
  public void setFieldFromConfigLineDosntThrowArgs() throws NoSuchFieldException, SecurityException {
    String line = " 1337a ";
    SettingsTest.INT_FIELD = 0;
    Field field = SettingsTest.class.getField("INT_FIELD");
    Settings.setFieldFromConfigLine(field, line);
    assertEquals(0, SettingsTest.INT_FIELD);
  }

}
