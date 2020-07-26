package org.rocketproplab.marginalstability.flightcomputer.math;

/**
 * Quaternion class.
 * 
 * @author Enlil Odisho
 *
 */
public class Quaternion {
	
	private double w;
	private double x;
	private double y;
	private double z;
	
	public Quaternion()
	{
		this.w = 1.0;
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}
	
	public Quaternion(double w, double x, double y, double z)
	{
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Quaternion add(Quaternion other)
	{
		Quaternion result = new Quaternion();
		result.w = this.w + other.w;
		result.x = this.x + other.x;
		result.y = this.y + other.y;
		result.z = this.z + other.z;
		return result;
	}
	
	public Quaternion subtract(Quaternion other)
	{
		Quaternion result = new Quaternion();
		result.w = this.w - other.w;
		result.x = this.x - other.x;
		result.y = this.y - other.y;
		result.z = this.z - other.z;
		return result;
	}
	
	public Quaternion multiply(Quaternion other)
	{
		Quaternion result = new Quaternion();
		result.w = this.w*other.w - this.x*other.x - this.y*other.y - this.z*other.z;
		result.x = this.w*other.x + this.x*other.w + this.y*other.z - this.z*other.y;
		result.y = this.w*other.y - this.x*other.z + this.y*other.w + this.z*other.x;
		result.z = this.w*other.z + this.x*other.y - this.y*other.x + this.z*other.w;
		return result;
	}
	
	public Quaternion multiply(double scalar)
	{
		Quaternion result = new Quaternion();
		result.w = this.w * scalar;
		result.x = this.x * scalar;
		result.y = this.y * scalar;
		result.z = this.z * scalar;
		return result;
	}
	
	public Quaternion conjugate()
	{
		Quaternion result = new Quaternion();
		result.w = this.w;
		result.x = -this.x;
		result.y = -this.y;
		result.z = -this.z;
		return result;
	}
	
	public Quaternion inverse()
	{
		double magnitude = this.getMagnitude();
		return this.conjugate().multiply(1.0 / (magnitude * magnitude));
	}
	
	public double getMagnitude()
	{
		return Math.sqrt(this.w*this.w + this.x*this.x + this.y*this.y + this.z*this.z);
	}
	
	/**
	 * @return the w component
	 */
	public double getW()
	{
		return w;
	}
	
	/**
	 * @return the x component
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * @return the y component
	 */
	public double getY()
	{
		return y;
	}
	
	/**
	 * @return the z component
	 */
	public double getZ()
	{
		return z;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Quaternion)) {
	      return false;
	    }
	    final double EQUAL_TOLERANCE = 0.00001; // TODO may need adjusting
	    Quaternion otherQuaternion = (Quaternion) other;
	    return (Math.abs(this.w-otherQuaternion.w) < EQUAL_TOLERANCE)
	    		&& (Math.abs(this.x-otherQuaternion.x) < EQUAL_TOLERANCE)
	    		&& (Math.abs(this.y-otherQuaternion.y) < EQUAL_TOLERANCE)
	    		&& (Math.abs(this.z-otherQuaternion.z) < EQUAL_TOLERANCE);
	}
	
	@Override
	public String toString()
	{
		return "(" + this.w + ", " + this.x + ", " + this.y + ", " + this.z + ")";
	}
	
}
