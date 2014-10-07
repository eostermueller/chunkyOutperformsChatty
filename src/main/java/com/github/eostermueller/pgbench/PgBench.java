package com.github.eostermueller.pgbench;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.github.eostermueller.pgbench.dataaccess_5.ListInquiry;
import com.github.eostermueller.pgbench.dataaccess_5.PkInquiry;

public class PgBench {
	//Defined by the C program pgbench that creates all of this schema and the data too.
	private static final int ACCOUNTS_PER_BRANCH = 100000;

	public static final int NUM_ACCOUNTS_RANDOM = 0;


	public static PgBench SINGLETON = new PgBench();
	
	public AtomicInteger m_branchCount = null;
	private AtomicInteger m_minAccounts = new AtomicInteger(5);
	private AtomicInteger m_maxAccounts = new AtomicInteger(20);
	private AtomicInteger m_numAccounts = new AtomicInteger(5);

	private PkInquiry m_pkInquiry_3 = new PkInquiry();

	private ListInquiry m_listInquiry_3 = new ListInquiry();

	private AtomicInteger m_numScenario = new AtomicInteger(1);
	
	public Connection getConnection() throws SQLException {
		InitialContext cxt = null;
		try {
			cxt = new InitialContext();
		} catch (NamingException e) {
			
			throw new RuntimeException(e);
		}
		if ( cxt == null ) {
		   throw new RuntimeException("Uh oh -- no context!");
		}

		DataSource ds = null;
		String jndiName= "java:/comp/env/jdbc/pgbench";
		try {
			ds = (DataSource) cxt.lookup( jndiName );
		} catch (NamingException e) {
			RuntimeException se = new RuntimeException("Error during Context.lookup() of [" + jndiName + "]", e);
			throw se;
		}
		
		if ( ds == null ) {
			   throw new RuntimeException("Data source not found!");
			} else {
				return ds.getConnection();
			}
	}
	public long getMaxAccountId() {
		return this.getBranchCount() * ACCOUNTS_PER_BRANCH;
	}
	public int getNumAccounts() {
		int numAccounts;
		if (m_numAccounts.intValue()==NUM_ACCOUNTS_RANDOM) {
				
			Random rand = new Random();
			
			numAccounts = rand.nextInt(PgBench.SINGLETON.getMaxAccounts());
			if (numAccounts < PgBench.SINGLETON.getMinAccounts())
				numAccounts = PgBench.SINGLETON.getMinAccounts();
		} else 
			numAccounts = m_numAccounts.intValue();
		
		return numAccounts;
	}
	public List<Long> getRandomAccountIds() {
		List<Long> accountIds = new ArrayList<Long>();
		Random r = new Random();
		
		int numAccounts = PgBench.SINGLETON.getNumAccounts();
		for(int i = 0; i < numAccounts; i++) {
			long randomAccountId = getRandomAccountId();
			accountIds.add( randomAccountId );
		}
			
		return accountIds;
	}
	public long getRandomAccountId() {
		Random r = new Random();
		return (long)(r.nextDouble()* getMaxAccountId());		
	}
	public PkInquiry getPkInquiry() {
		return m_pkInquiry_3;
	}
	public ListInquiry getListInquiry() {
		return m_listInquiry_3;
	}
	private void log(String msg) {
		System.out.println("PgBench: " + msg);
	}
	public int getBranchCount() {
		return this.m_branchCount.get();
	}
	public int getMinAccounts() {
		return this.m_minAccounts.get();
	}
	public int getMaxAccounts() {
		return this.m_maxAccounts.get();
	}
	public int getNumScenario() {
		return this.m_numScenario.get();
	}
	public void setBranchCount(int val) {
		this.m_branchCount.set(val);
		log("Set branchCount to [" + val + "]");
	}
	public void setMinAccounts(int val) {
		this.m_minAccounts.set(val);
		log("Set minAccounts to [" + val + "]");
	}
	public void setMaxAccounts(int val) {
		this.m_maxAccounts.set(val);
		log("Set maxAccounts to [" + val + "]");
	}
	/**
	 * Dictates how many accounts are inquired upon for each web request.
	 * If 0, then the getter returns a random number.
	 * @param val
	 */
	public void setNumAccounts(int val) {
		this.m_numAccounts.set(val);
		log("Set numAccounts to [" + val + "]");
	}
	public void setNumScenario(int val) {
		this.m_numScenario.set(val);
		log("Set Scenario Num to [" + val + "]");
	}
}
