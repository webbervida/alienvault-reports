package com.alienvault;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.alienvault.report.github.IssuesReportRunner;

public class MainTest {

	@Test
	public void testMain() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		PrintStream old = System.out;
		System.setOut(ps);
		Main.main(new String[] { "AUTHTOKEN:bogus", "user/repo" });
		System.out.flush();
		System.setOut(old);
		String textToStdOut = baos.toString();

		assertTrue(textToStdOut.indexOf(IssuesReportRunner.resourceBundle.getString("dataSourceError")) > -1);
		assertTrue(textToStdOut.indexOf(IssuesReportRunner.resourceBundle.getString("workerThreadDiedError")) > -1);
	}
}
