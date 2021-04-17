package org.rocketproplab.marginalstability.flightcomputer.hal;

/**
 * An interface to make accessing each value of a register easier. The
 * {@link #getValueMask()} returns a bitmask of what section of the register
 * should be read and the {@link #getValueLSBPos()} method returns how many bits
 * to the left of the LSB the LSB of the value is.
 *
 * @author Max Apodaca
 */
public interface RegisterValue {

  /**
   * Get a mask for the register in which this value is in. The mask will only
   * cover the specified value. <br>
   * For instance a register with three values aabbbccc would mean that value a
   * has a mask of 0b11000000;
   *
   * @return the mask for this value for its register
   */
  public int getValueMask();

  /**
   * Get how many bits to the left of the register's LSB the LSB of the value is.
   * <br>
   * For instance if we have a register with three values aabbbccc the LSBPos for
   * value b would be 3 as the LSB of b is three bits to the left of the LSB of
   * the register as a whole. The LSBPos of c would be 0 and the LSBPos of a would
   * be 6.
   *
   * @return how many bits to the left of the register's LSB the LSB of the value
   * is
   */
  public int getValueLSBPos();

  /**
   * The value associated with the given register value. Setting the appropriate
   * bits in the value's register to this value will result in application of the
   * value. <br>
   * If this instance corresponds to a value of 01 for a in the register aabbbccc
   * then ordinal would return 0b01.<br>
   * <br>
   * <b>NOTE</b>: the implementation relies on enums which means the enum values
   * must be ordered correctly to yield a correct return value for ordinal. If the
   * enum has 2 members A_0 and A_1 and A_0 should have value 0 then A_0 must be
   * the first element in the enum.
   *
   * @return the value of this value
   */
  public int ordinal();

}
