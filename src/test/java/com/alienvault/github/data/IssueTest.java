package com.alienvault.github.data;

import static com.alienvault.github.data.IssueFixtures.FULL_REPO_PATH;
import static com.alienvault.github.data.IssueFixtures.PRETTY_DATE;
import static com.alienvault.github.data.IssueFixtures.STATE_PRETTY;
import static com.alienvault.github.data.IssueFixtures.cCreatedAt;
import static com.alienvault.github.data.IssueFixtures.cDateDefaultToString;
import static com.alienvault.github.data.IssueFixtures.cIssueNumber;
import static com.alienvault.github.data.IssueFixtures.cRepository;
import static com.alienvault.github.data.IssueFixtures.cState;
import static com.alienvault.github.data.IssueFixtures.cTitle;
import static com.alienvault.github.data.IssueFixtures.cUser;
import static com.alienvault.github.data.IssueFixtures.control;
import static com.alienvault.github.data.IssueFixtures.dCreatedAt;
import static com.alienvault.github.data.IssueFixtures.dIssueNumber;
import static com.alienvault.github.data.IssueFixtures.dRepository;
import static com.alienvault.github.data.IssueFixtures.dState;
import static com.alienvault.github.data.IssueFixtures.dTitle;
import static com.alienvault.github.data.IssueFixtures.dUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IssueTest {

	@Test
	public void equalsTest() {
		assertEquals(control, new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser));
		// Run through changing each arg
		assertNotEquals(control, new Issue(dCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser));
		assertNotEquals(control, new Issue(cCreatedAt, dIssueNumber, cState, cTitle, cRepository, cUser));
		assertNotEquals(control, new Issue(cCreatedAt, cIssueNumber, dState, cTitle, cRepository, cUser));
		assertNotEquals(control, new Issue(cCreatedAt, cIssueNumber, cState, dTitle, cRepository, cUser));
		assertNotEquals(control, new Issue(cCreatedAt, cIssueNumber, cState, cTitle, dRepository, cUser));
		assertNotEquals(control, new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, dUser));
	}

	@Test
	public void equalsTest2() {
		assertTrue(control.equals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser)));

		assertFalse(control.equals(new Issue(dCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser)));
		assertFalse(control.equals(new Issue(cCreatedAt, dIssueNumber, cState, cTitle, cRepository, cUser)));
		assertFalse(control.equals(new Issue(cCreatedAt, cIssueNumber, dState, cTitle, cRepository, cUser)));
		assertFalse(control.equals(new Issue(cCreatedAt, cIssueNumber, cState, dTitle, cRepository, cUser)));
		assertFalse(control.equals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, dRepository, cUser)));
		assertFalse(control.equals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, dUser)));
	}

	@Test
	public void constructorTest() {
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).getCreatedAt(),
				control.getCreatedAt());
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).getIssueNumber(),
				control.getIssueNumber());
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).getRepository(),
				control.getRepository());
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).getState(),
				control.getState());
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).getTitle(),
				control.getTitle());
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).getUser(),
				control.getUser());
	}

	@Test
	public void hashCodeTest() {
		assertEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).hashCode(),
				control.hashCode());

		assertNotEquals(new Issue(dCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser).hashCode(),
				control.hashCode());
		assertNotEquals(new Issue(cCreatedAt, dIssueNumber, cState, cTitle, cRepository, cUser).hashCode(),
				control.hashCode());
		assertNotEquals(new Issue(cCreatedAt, cIssueNumber, dState, cTitle, cRepository, cUser).hashCode(),
				control.hashCode());
		assertNotEquals(new Issue(cCreatedAt, cIssueNumber, cState, dTitle, cRepository, cUser).hashCode(),
				control.hashCode());
		assertNotEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, dRepository, cUser).hashCode(),
				control.hashCode());
		assertNotEquals(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, dUser).hashCode(),
				control.hashCode());
	}

	@Test
	public void sortorderTest() {
		assertEquals(1, control
				.compareTo(new Issue(cCreatedAt.minusDays(1), cIssueNumber, cState, cTitle, cRepository, cUser)));
		assertEquals(0, control.compareTo(new Issue(cCreatedAt, cIssueNumber, cState, cTitle, cRepository, cUser)));
		assertEquals(-1, control
				.compareTo(new Issue(cCreatedAt.minusDays(-1), cIssueNumber, cState, cTitle, cRepository, cUser)));
	}

	@Test
	public void toStringTest() {
		assertTrue(control.toString().contains(cDateDefaultToString));
		assertTrue(control.toString().contains(cState));
		assertTrue(control.toString().contains(cTitle));
		assertTrue(control.toString().contains(cRepository));
		assertTrue(control.toString().contains(cUser));
		assertTrue(control.toString().contains("" + cIssueNumber));
	}

	@Test
	public void convenienceMethodTest() {
		assertEquals(PRETTY_DATE, control.getCreatedAtPretty());
		assertEquals(FULL_REPO_PATH, control.getFullRepositoryPath());
		assertEquals(STATE_PRETTY, control.getStatePretty());
	}
}
