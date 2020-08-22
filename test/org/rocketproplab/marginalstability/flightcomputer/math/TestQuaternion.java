package org.rocketproplab.marginalstability.flightcomputer.math;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestQuaternion {
	
	@Test
	public void defaultQuaternionConstructorIsCorrectValue()
	{
		Quaternion q1 = new Quaternion();
		assertEquals(q1.getW(), 1.0, 0.0);
		assertEquals(q1.getX(), 0.0, 0.0);
		assertEquals(q1.getY(), 0.0, 0.0);
		assertEquals(q1.getZ(), 0.0, 0.0);
	}
	
	@Test
	public void equalQuaternionsEqual()
	{
		Quaternion q1 = new Quaternion(0.4, 2.0, 4.0, 7.3);
		Quaternion q2 = new Quaternion(0.4, 2.0, 4.0, 7.3);
		assertEquals(q1, q2);
	}
	
	@Test
	public void nonEqualQuaternionsNotEqual()
	{
		Quaternion q1 = new Quaternion(0.4, 2.0, 4.0, 7.3);
		Quaternion q2 = new Quaternion(0.4, 2.0, 4.0, 7.2);
		assertNotEquals(q1, q2);
	}
	
	@Test
	public void notEqualWithNull()
	{
		Quaternion q1 = new Quaternion();
		assertFalse(q1.equals(null));
	}
	
	@Test
	public void addingQuaternionsProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		Quaternion q2 = new Quaternion(-0.205, 0.241, 0.077, -0.946);
		Quaternion result = new Quaternion(0.445, 0.239, -0.644, -0.706);
		assertTrue(q1.add(q2).equals(result));
		assertTrue(q2.add(q1).equals(result));
	}
	
	@Test
	public void subtracingQuaternionsProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		Quaternion q2 = new Quaternion(-0.205, 0.241, 0.077, -0.946);
		Quaternion result = new Quaternion(0.855,-0.243,-0.798,1.186);
		assertTrue(q1.subtract(q2).equals(result));
	}
	
	@Test
	public void multiplyQuaternionsProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		Quaternion q2 = new Quaternion(-0.205, 0.241, 0.077, -0.946);
		Quaternion result = new Quaternion(0.14979, 0.82065, 0.2538, -0.49049);
		assertTrue(q1.multiply(q2).equals(result));
	}
	
	@Test
	public void multiplyQuaternionByScalarProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		final double scalar = 1.7;
		Quaternion result = new Quaternion(1.105, -0.0034, -1.2257, 0.408);
		assertTrue(q1.multiply(scalar).equals(result));
	}
	
	@Test
	public void conjugateOfQuaternionProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		Quaternion result = new Quaternion(0.65,0.002,0.721,-0.24);
		assertTrue(q1.conjugate().equals(result));
	}
	
	@Test
	public void inverseOfQuaternionProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		Quaternion result = new Quaternion(0.6500358, 0.0020001, 0.7210397, -0.2400132);
		assertTrue(q1.inverse().equals(result));
	}
	
	@Test
	public void quaternionGetMagnitudeProducesCorrectResult()
	{
		Quaternion q1 = new Quaternion(0.65, -0.002, -0.721, 0.24);
		final double result = 0.9999724996218646;
		assertEquals(q1.getMagnitude(), result, 0.000001);
	}
	
}
