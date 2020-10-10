package org.rocketproplab.marginalstability.flightcomputer.comm;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class SCMPacketHelpers {

  public static final double SCM_DOUBLE_EXPONETIAL_THRESHOLD     = Math.pow(10, SCMPacket.DATA_LENGTH + 1);
  public static final double SCM_NEG_DOUBLE_EXPONETIAL_THRESHOLD = -Math.pow(10, SCMPacket.DATA_LENGTH);

  /**
   * Returns a representation of a double that will fit into an SCM Packet
   * 
   * @param value the double to convert
   * @return the string to use as data
   */
  public static String getSCMDoulbeRepresentation(double value) {
    String doubleAsString = Double.toString(value);

    if (value > SCM_DOUBLE_EXPONETIAL_THRESHOLD || value < SCM_NEG_DOUBLE_EXPONETIAL_THRESHOLD) {
      return SCMPacketHelpers.convertSCMDoulbeExponential(value);
    }

    if (doubleAsString.length() < SCMPacket.DATA_LENGTH) {
      doubleAsString = SCMPacketHelpers.padRight(doubleAsString, SCMPacket.DATA_LENGTH);
    } else if (doubleAsString.length() > SCMPacket.DATA_LENGTH) {
      doubleAsString = doubleAsString.substring(0, SCMPacket.DATA_LENGTH);
    }

    return doubleAsString;
  }

  /**
   * Convert the double value to SCM format with exponential notation
   * 
   * @param value the value to convert
   * @return an exponential string representation
   */
  public static String convertSCMDoulbeExponential(double value) {
    if (Double.isNaN(value)) {
      return SCMPacketHelpers.padRight(Double.toString(Double.NaN), SCMPacket.DATA_LENGTH);
    }

    if (Double.isInfinite(value)) {
      if (value > 0) {
        return "Infin";
      }
      return "-Infi";
    }

    NumberFormat format = new DecimalFormat("#.#E0");
    String       result = format.format(value);
    if (result.length() < SCMPacket.DATA_LENGTH) {
      result = SCMPacketHelpers.padRight(result, SCMPacket.DATA_LENGTH);
    }

    if (result.length() > SCMPacket.DATA_LENGTH) {
      if (!result.contains("E")) {
        return SCMPacketHelpers.padRight(Double.toString(Double.NaN), SCMPacket.DATA_LENGTH);
      }

      String[] numberParts    = result.split("E");
      String   exponent       = numberParts[1];
      String   decimal        = numberParts[0];
      int      exponentLength = exponent.length() + 1;
      decimal = decimal.substring(0, SCMPacket.DATA_LENGTH - exponentLength);
      return decimal + "E" + exponent;
    }

    return result;
  }

  /**
   * Pads the string to be a minimum of count long. It pads with spaces.
   * 
   * @param string    the string to pad on the right
   * @param minLength the minimum length of the string
   * @return a padded string or the original if already long enough.
   */
  public static String padRight(String string, int minLength) {
    return String.format("%-" + minLength + "s", string);
  }

}
