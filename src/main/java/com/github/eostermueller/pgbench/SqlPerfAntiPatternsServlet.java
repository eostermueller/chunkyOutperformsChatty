package com.github.eostermueller.pgbench;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import com.github.eostermueller.pgbench.dataaccess_1.AccountMgr1;
import com.github.eostermueller.pgbench.dataaccess_2.AccountMgr2;
import com.github.eostermueller.pgbench.dataaccess_3.AccountMgr3;
import com.github.eostermueller.pgbench.dataaccess_4.AccountMgr4;
import com.github.eostermueller.pgbench.dataaccess_5.AccountMgr5;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.SerializationUtil;

public class SqlPerfAntiPatternsServlet extends HttpServlet implements ServletContextListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 681821029539761430L;
	private static final String PARAM_NUM_ACCOUNTS = "pgbenchNumAccounts";
	private static final String PARAM_SCENARIO_NUM = "pgbenchScenarioNum";
	private static final String PARAM_ACCOUNT_IDS = "pgbenchAccountIds";
	private static final int SCENARIO_1_SINGLE_QUERY = 1;
	private static final int SCENARIO_2_TWO_BULK_QUERIES = 2;
	private static final int SCENARIO_3_BULK_ACCOUNT_PIECEMEAL_HISTORY = 3;
	private static final int SCENARIO_4_SEPARATE_ACCOUNT_QUERIES = 4;
	private static final int SCENARIO_5_PK_LOOKUP_ONLY = 5;
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException
    {
		long start = System.currentTimeMillis();
		setSingletonParameters(req);
		List<Long> accountIds_criteria = this.getAccountIds(req);
		Accounts accounts = null;
		String stats = null;
		int scenario = PgBench.SINGLETON.getNumScenario();
		switch(scenario) {
			case SCENARIO_1_SINGLE_QUERY:
				AccountMgr1 acctMgr1 = new AccountMgr1();
				accounts = acctMgr1.getAccounts(accountIds_criteria);
				stats = acctMgr1.m_sqlTextMgr1.m_stats.getXmlStats();
				break;
			case SCENARIO_2_TWO_BULK_QUERIES:
				AccountMgr2 acctMgr2 = new AccountMgr2();
				accounts = acctMgr2.getAccounts(accountIds_criteria);
				stats = acctMgr2.m_sqlTextMgr2.m_stats.getXmlStats();
				break;
			case SCENARIO_3_BULK_ACCOUNT_PIECEMEAL_HISTORY:
				AccountMgr3 acctMgr3 = new AccountMgr3();
				accounts = acctMgr3.getAccounts(accountIds_criteria);
				stats = acctMgr3.m_sqlTextMgr3.m_stats.getXmlStats();
				break;
			case SCENARIO_4_SEPARATE_ACCOUNT_QUERIES:
				AccountMgr4 acctMgr4 = new AccountMgr4();
				accounts = acctMgr4.getAccounts(accountIds_criteria);
				stats = acctMgr4.m_sqlTextMgr4.m_stats.getXmlStats();
				break;
			case SCENARIO_5_PK_LOOKUP_ONLY:
				AccountMgr5 acctMgr5 = new AccountMgr5(); 
				accounts = acctMgr5.getAccounts(accountIds_criteria);
				stats = acctMgr5.m_sqlTextMgr5.m_stats.getXmlStats();
				break;
			default:
				throw new ServletException("Found URL Parameter [" + PARAM_SCENARIO_NUM + "=" + PgBench.SINGLETON.getNumScenario() + "].  Was expecting a value of 1-5." );
		}
		
		long end = System.currentTimeMillis();
		
		resp.getOutputStream().print("<Root>");
		SerializationUtil as;
		try {
			as = new SerializationUtil();
			as.setOutputStream(resp.getOutputStream());
			as.serialize(accounts);
			displayStats(resp, stats, end-start, scenario);
			resp.getOutputStream().print("</Root>");
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		} catch (TransformerException e) {
			throw new ServletException(e);
		}
    }
	private void displayStats(HttpServletResponse response, String xmlStats, long duration, int scenario) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("<PgBenchStats>");
		sb.append("<Scenario>").append(Integer.toString(scenario)).append("</Scenario>");
		sb.append( xmlStats );
		sb.append("<duration>").append(duration).append("</duration>");
		sb.append("</PgBenchStats>");
	 	response.setContentType("text/xml");
	    response.getOutputStream().print(sb.toString()); 
		
	}
	private void setSingletonParameters(HttpServletRequest req) throws ServletException {
		String temp = req.getParameter(PARAM_NUM_ACCOUNTS);
		if (temp!=null) {
			try {
				int intTemp = Integer.parseInt(temp);
				PgBench.SINGLETON.setNumAccounts(intTemp);
			} catch (NumberFormatException e) {
				throw new ServletException("Invalid HTTP Parameter value [" + temp + "] for variable ["  + PARAM_NUM_ACCOUNTS + "]. Expecting 0 for random or >= 1 to specify the number of accounts used for each inquiry");
			}
		}
		temp = req.getParameter(PARAM_SCENARIO_NUM);
		if (temp!=null) {
			try {
				int intTemp = Integer.parseInt(temp);
				PgBench.SINGLETON.setNumScenario(intTemp);
			} catch (NumberFormatException e) {
				throw new ServletException("Invalid HTTP Parameter value [" + temp + "] for variable ["  + PARAM_SCENARIO_NUM + "]. Expecting 1 or 2");
			}
		}
	}
	List<Long> getAccountIds(HttpServletRequest req) throws ServletException {
		//		
		//		List<Long> randomAccountIds = PgBench.SINGLETON.getRandomAccountIds();
		List<Long> accountIds = null;
		String temp = req.getParameter(PARAM_ACCOUNT_IDS);
		if (temp!=null) {
			try {
				String[] strAccountIds = temp.split(",");
				accountIds = new ArrayList<Long>();
				for(String oneAccountId : strAccountIds)
					accountIds.add( new Long(Long.parseLong(oneAccountId)));
				
			} catch (NumberFormatException e) {
				throw new ServletException("Invalid HTTP Parameter value [" + temp + "] for variable ["  + PARAM_ACCOUNT_IDS + "]. Expecting a comma-delimited list of accountIds.");
			}
		} else 
			accountIds = PgBench.SINGLETON.getRandomAccountIds();
		return accountIds;

	}
	
	public void contextInitialized(ServletContextEvent sce) {

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement(AccountMgr5.m_sqlTextMgr5.getBranchCount());
			rs = ps.executeQuery();
			int branchCount = -1;
			while(rs.next()) {
				branchCount = rs.getInt(1);
			}
			PgBench.SINGLETON.m_branchCount = new AtomicInteger(branchCount);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(con);
		}
		
	}
	
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}
		// TODO Auto-generated method stub
		
}
