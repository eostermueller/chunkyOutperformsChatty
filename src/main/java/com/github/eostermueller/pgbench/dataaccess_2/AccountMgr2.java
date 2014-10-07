package com.github.eostermueller.pgbench.dataaccess_2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchException;
import com.github.eostermueller.pgbench.PgBenchUtil;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.Transaction;

public class AccountMgr2 {

	public static SqlTextMgr2 m_sqlTextMgr2 = new SqlTextMgr2();
	public Accounts getAccounts(List<Long> randomAccountIds) throws ServletException {
		Accounts accounts = getAccountsInternal(randomAccountIds);
		
		getTransactions(accounts);
		
		return accounts;
	}

	private Accounts getAccountsInternal(List<Long> accountIds) throws ServletException  {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Accounts accounts = new Accounts();
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( m_sqlTextMgr2.getMultipleAccountsSql(accountIds.size()) );
			for (int i = 1; i <= accountIds.size(); i++) {
				ps.setLong(i, accountIds.get(i-1).longValue());
			}
			rs = ps.executeQuery();
			short count = 0;
			
			while(rs.next()) {
				Account a = createAccount(rs);
				accounts.addAccount(a);
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
		return accounts;
	}

	private Account createAccount(ResultSet rs) throws SQLException {
		Account a = new Account();
		a.accountId = rs.getLong(1);
		a.branchId = rs.getInt(2);
		a.balance = rs.getLong(3);
		a.filler = rs.getString(4);
		return a;
	}
	private void getTransactions(Accounts accounts) throws ServletException  {
		
		Connection con = null; 
		PreparedStatement ps = null; 
		ResultSet rs = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( m_sqlTextMgr2.getHistorySql(accounts.getAccounts().size()) );
			
			for (int i = 1; i <= accounts.getAccounts().size();i++) {
				ps.setLong(i, accounts.getAccounts().get(i-1).accountId );
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				Transaction t = new Transaction();
				t.tellerId = rs.getInt(1);
				t.historyId = rs.getLong(2);
				t.branchId = rs.getInt(3);
				t.accountId = rs.getInt(4);
				t.delta = rs.getLong(5);
				t.mtime = PgBenchUtil.getDate(rs, 6); 
				t.filler = rs.getString(7);
				Account a = accounts.findAccount(t.accountId);
				if (a!=null)
					a.transactions.add(t);
				else
					throw new ServletException("Found transaction with account id = [" + t.accountId + "], but it didn't have a matching account.  Full transactions [" + t.toString() + "]");
			}
		} catch (SQLException e) {
			throw new ServletException(e);
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
	}

}
