package com.alienvault.report;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CommandLineDestinationTest {

	@Test
	public void testConstructor() {
		String[] args = new String[] { "this", "that" };
		assertArrayEquals(args, new CommandLineDestination(args).getConfigArgs());
	}
}
