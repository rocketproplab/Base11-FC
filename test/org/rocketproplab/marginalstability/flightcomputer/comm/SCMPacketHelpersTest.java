package org.rocketproplab.marginalstability.flightcomputer.comm;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SCMPacketHelpersTest {

  @Test
  public void valueOfOneIsValidForDoulbeToString() {
    String result = SCMPacketHelpers.getSCMDoulbeRepresentation(1.0);
    assertEquals("1.0  ", result);
  }

  @Test
  public void negativeValueConvertedForDouble() {
    String result = SCMPacketHelpers.getSCMDoulbeRepresentation(-1.0);
    assertEquals("-1.0 ", result);
  }

  @Test
  public void tooBigNumberTruncated() {
    String result = SCMPacketHelpers.getSCMDoulbeRepresentation(-100.0);
    assertEquals("-100.", result);
  }

  @Test
  public void muchToLargeNumberUsesExponential() {
    String result = SCMPacketHelpers.getSCMDoulbeRepresentation(-1000000.0);
    assertEquals("-1E6 ", result);
  }

  @Test
  public void exponentialConvertsOne() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(1);
    assertEquals("1E0  ", result);
  }

  @Test
  public void exponentialWithDecomalConverted() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(1.23);
    assertEquals("1.2E0", result);
  }

  @Test
  public void negativeValueExponentConvertedWithoutDecimal() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(-1.23);
    assertEquals("-1.E0", result);
  }

  @Test
  public void largeExponentConverted() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(-1.23E10);
    assertEquals("-1E10", result);
  }

  @Test
  public void veryLargeExponentConverted() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(-1.23E100);
    assertEquals("-E100", result);
  }

  @Test
  public void maxDoubleConverted() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(Double.MAX_VALUE);
    assertEquals("1E308", result);
  }

  @Test
  public void minDoubleConverted() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(Double.MIN_VALUE);
    assertEquals("E-324", result);
  }

  @Test
  public void negativeMaxConverted() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(-Double.MAX_VALUE);
    assertEquals("-E308", result);
  }

  @Test
  public void nanConvertedViaExponential() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(Double.NaN);
    assertEquals("NaN  ", result);
  }
  
  @Test
  public void infinityConvertedViaExponential() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(Double.POSITIVE_INFINITY);
    assertEquals("Infin", result);
  }
  
  @Test
  public void negativeInfinityConvertedViaExponential() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(Double.NEGATIVE_INFINITY);
    assertEquals("-Infi", result);
  }
  
  @Test
  public void zeroConvertedViaExponential() {
    String result = SCMPacketHelpers.convertSCMDoulbeExponential(0);
    assertEquals("0E0  ", result);
  }

  @Test
  public void padRightPadsRight() {
    String result = SCMPacketHelpers.padRight("abc", 5);
    assertEquals("abc  ", result);
  }

  @Test
  public void toLongStringDoesNotTruncate() {
    String result = SCMPacketHelpers.padRight("abcdef", 5);
    assertEquals("abcdef", result);
  }

}
