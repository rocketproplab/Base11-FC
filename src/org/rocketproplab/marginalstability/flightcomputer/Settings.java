package org.rocketproplab.marginalstability.flightcomputer;

public class Settings {

  @SettingSectionHeader(name = "Flight State Settings")

  @UserSetting(comment = "The speed while at apogee", units = "m/s")
  public static double APOGEE_SPEED = 10;

  @UserSetting(comment = "The speed we need to be moving less than to be considered landed", units = "m/s")
  public static double LANDED_SPEED = 1;

  @SettingSectionHeader(name = "Parachute Deploy Settings")

  @UserSetting(comment = "The height at which we should deploy the main chute in meters above sea level", units = "m")
  public static double MAIN_CHUTE_HEIGHT = 5000;

  @UserSetting(comment = "The pressure at which we should deploy the main chute", units = "hPa")
  public static double MAIN_CHUTE_PRESSURE = 0; // TODO: set main chute pressure

  /**
   * Time threshold needed to exceed to deploy the main chute
   */
  @UserSetting(comment = "Time threshold needed to exceed to deploy the main chute", units = "s")
  public static double MAIN_CHUTE_PRESSURE_TIME_THRESHOLD = 10; // TODO: set time exceeding the threshold needed to
                                                               // deploy main chute

  public static boolean[] ENGINE_ON_VALVE_STATES = {true, true, true, true, true};

  public static boolean[] ENGINE_ABORT_VALVE_STATES = {true, true, true, true, true};

  // Unit conversions

  /**
   * Conversion constant for how many milliseconds are in a second
   */
  public static final double MS_PER_SECOND = 1000; // ms/s

  /**
   * How far off we are allowed to be to be equal
   */
  public static final double EQUALS_EPSILON = 0.00000001;

  @SettingSectionHeader(name = "Heartbeat Settings")

  @UserSetting(comment = "Threshold for periodic heart beat signal", units = "s")
  public static double HEARTBEAT_THRESHOLD = 1;

  @SettingSectionHeader(name = "PT Quadratic Regression")

  @UserSetting(comment = "'a' constant for quadratic regression for pressure transducers", units = "hPa / V^2")
  public static double[] A_PT_CONSTANTS = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0 };

  @UserSetting(comment = "'b' constant for quadratic regression for pressure transducers", units = "hPa / V")
  public static double[] B_PT_CONSTANTS = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
      1.0 };

  @UserSetting(comment = "'c' constant for quadratic regression for pressure transducers", units = "hPa")
  public static double[] C_PT_CONSTANTS = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
      0.0 };

  @UserSetting(comment = "Phone number to text position to", units = "1xxxyyyyyyy")
  public static String PHONE_NUMBER = "13150001111";

  @SettingSectionHeader(name = "MAX14830 settings")

  @UserSetting(comment = "Frequency of reference oscillator for the MAX14830, currently a LFXTAL003260 labeled X1 in the schematic.", units = "Hz")
  public static int MAX14830_F_REF = 3686400;

  private static Set<Field> getSettingFields() {
    Set<Field> result = new LinkedHashSet<>();
    for (Field field : Settings.class.getDeclaredFields()) {
      if (field.isAnnotationPresent(UserSetting.class)) {
        result.add(field);
      }
    }
    return result;
  }

  public static String getConfigContents() {
    String     result = "";
    Set<Field> set    = getSettingFields();
    for (Field field : set) {
      result += fieldAsString(field) + "\n";
    }
    return result;
  }

  public static String fieldAsString(Field field) {
    String result = "";
    if (field.isAnnotationPresent(SettingSectionHeader.class)) {

      String sectionHeader = "# " + field.getAnnotation(SettingSectionHeader.class).name() + " |\n";
      int    dashLength    = sectionHeader.length() - 2;
      String delimiter     = "#" + new String(new char[dashLength]).replace('\0', '-') + "\n";
      result += delimiter + sectionHeader + delimiter + "\n";
    }

    result += "# Type: ";
    try {
      if (field.getType().equals(double[].class)) {
        result += "double[" + ((double[]) field.get(double[].class)).length + "]\n";
      } else {
        result += field.getType() + "\n";
      }
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }

    result += "# Description: " + field.getAnnotation(UserSetting.class).comment() + "\n";
    result += "# Units: " + field.getAnnotation(UserSetting.class).units() + "\n";
    result += getNameFromField(field) + "=" + getFieldValueAsString(field) + "\n";
    return result;
  }

  private static String getFieldValueAsString(Field field) {
    try {
      if (field.getType().equals(double[].class)) {
        double[] array = ((double[]) field.get(double[].class));
        if (array.length == 0) {
          return "[]";
        }
        String result = "[" + array[0];
        for (int i = 1; i < array.length; i++) {
          result += ",\t" + array[i];
        }
        return result + "]";
      }
      return field.get(Object.class).toString();
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return "ERROR";
  }

  private static String getNameFromField(Field field) {
    String annotatedName = field.getAnnotation(UserSetting.class).name();
    if (annotatedName.equals("")) {
      return field.getName();
    }
    return annotatedName;
  }

  protected static Set<String> getUsefulLinesFromConfig(List<String> config) {
    Set<String> usefulLines = new HashSet<>();
    for (String line : config) {
      String lineWithoutComment = line;
      int    commentStartIdx    = line.indexOf('#');
      if (commentStartIdx >= 0) {
        lineWithoutComment = line.substring(0, commentStartIdx);
      }
      lineWithoutComment = lineWithoutComment.trim();
      if (lineWithoutComment.length() == 0) {
        continue;
      }
      usefulLines.add(lineWithoutComment);
    }
    return usefulLines;
  }

  protected static Map<String, String> getFieldNameValueMap(List<String> config) {
    Set<String>         usefulLines       = getUsefulLinesFromConfig(config);
    Map<String, String> fieldNameValueMap = new HashMap<>();
    for (String line : usefulLines) {
      int equalsIndex = line.indexOf('=');
      if (equalsIndex < 0) {
        System.err.println("Unable to parse line: " + line);
        continue;
      }
      String fieldName = line.substring(0, equalsIndex);
      String value     = line.substring(equalsIndex + 1);
      fieldNameValueMap.put(fieldName.trim(), value);
    }
    return fieldNameValueMap;
  }

  protected static void setFieldDoubleArray(Field field, String line) throws IllegalArgumentException, IllegalAccessException {
    line = line.trim().replace("[", "").replace("]", "");
    String[] newValues = line.split(",");
    double[] fieldValues = (double[]) field.get(null);
    if(newValues.length != fieldValues.length) {
      System.err.println("Array length mismatch: " + line);
    }
    int minValid = Math.min(fieldValues.length, newValues.length);
    for(int i = 0; i<minValid; i++) {
      fieldValues[i] = Double.parseDouble(newValues[i].trim());
    }
  }

  protected static void setFieldDouble(Field field, String line) throws IllegalArgumentException, IllegalAccessException {
    line = line.trim();
    double value = Double.parseDouble(line);
    field.set(null, value);
  }

  protected static void setFieldInt(Field field, String line) throws IllegalArgumentException, IllegalAccessException {
    line = line.trim();
    int value = Integer.parseInt(line);
    field.set(null, value);
  }

  protected static void setFieldFromConfigLine(Field field, String line) {
    Class<?> fieldType = field.getType();
    try {
      if (fieldType.equals(double[].class)) {
        setFieldDoubleArray(field, line);
      } else if (fieldType.equals(Double.TYPE)) {

        setFieldDouble(field, line);

      } else if (fieldType.equals(Integer.TYPE)) {
        setFieldInt(field, line);
      }
    } catch (IllegalArgumentException | IllegalAccessException e) {
      System.err.println("Unable to parse " + fieldType + ": " + line);
      e.printStackTrace();
    }
  }

  private static boolean readSettingsFromConfig(List<String> config) {
    Set<Field>          settingFields     = getSettingFields();
    Map<String, String> fieldNameValueMap = getFieldNameValueMap(config);
    boolean outOfDate = false;
    for (Field field : settingFields) {
      String fieldName = getNameFromField(field);
      if (fieldNameValueMap.containsKey(fieldName)) {
        setFieldFromConfigLine(field, fieldNameValueMap.get(fieldName));
      } else {
        System.err.println("Can't find key for " + fieldName);
        outOfDate = true;
      }
    }
    return outOfDate;
  }

  private static String getSettingsFileLocation() {
    String home = System.getProperty("user.home");
    return home + "/settings.cfg";
  }

  public static void readSettings() {
    String configFileLocation = getSettingsFileLocation();
    List<String> lines = Collections.emptyList();
    try {
      lines = Files.readAllLines(Paths.get(configFileLocation));
    } catch(IOException e) {
      e.printStackTrace();
    }
    boolean outOfDate = readSettingsFromConfig(lines);
    if(!outOfDate) {
      return;
    }
    String config = getConfigContents();
    try {
      PrintWriter out = new PrintWriter(configFileLocation);
      out.write(config);
      out.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    readSettings();
    System.out.println(Settings.MAX14830_F_REF);
  }
}
