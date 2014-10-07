package com.github.eostermueller.pgbench;

import static org.junit.Assert.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class AccountIdTest {

	@Test
	public void testMaxAccountId() {
		PgBench pgBench = new PgBench();
		pgBench.m_branchCount = new AtomicInteger(10);
		
		assertEquals("Didn't find right account max", 10*100000, pgBench.getMaxAccountId());
		
	}
	@Test
	public void testRandomAccountId() {
		PgBench pgBench = new PgBench();
		pgBench.m_branchCount = new AtomicInteger(10);
		
		assertTrue("Shouldn't find an accountId == 0", pgBench.getRandomAccountId() > 0);
		
	}
	@Test
	public void testMinAccountId() {
		PgBench pgBench = new PgBench();
		
		assertEquals("Default min count of accounts isn't right", 5,pgBench.getMinAccounts());
		
	}
	@Test
	public void testListOfAccountIds() {
		PgBench pgBench = new PgBench();
		pgBench.m_branchCount = new AtomicInteger(10);
		
		List<Long> listOfAccountIds = pgBench.getRandomAccountIds();
		assertNotNull(listOfAccountIds);
		assertTrue("count of accounts must be greater than minAccounts", listOfAccountIds.size() >= pgBench.getMinAccounts());
	}

}
