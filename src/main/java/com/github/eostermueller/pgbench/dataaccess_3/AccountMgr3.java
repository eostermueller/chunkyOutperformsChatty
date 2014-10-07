package com.github.eostermueller.pgbench.dataaccess_3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchException;
import com.github.eostermueller.pgbench.PgBenchUtil;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.Transaction;

public class AccountMgr3 {

	public static SqlTextMgr3 m_sqlTextMgr3 = new SqlTextMgr3();
	public Accounts getAccounts(List<Long> randomAccountIds) throws ServletException  {
		Accounts accounts = getAccountsInternal(randomAccountIds);
		
		for( Account a : accounts.getAccounts() )
			try {
				a.transactions = this.getTransactions(a.accountId);
			} catch (SQLException e) {
				throw new ServletException(e);
			} catch (PgBenchException e) {
				throw new ServletException(e);
			}
		return accounts;
	}

	private Accounts getAccountsInternal(List<Long> accountIds) throws ServletException  {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Accounts accounts = new Accounts();
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( m_sqlTextMgr3.getMultipleAccountsSql(accountIds.size()) );
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
	public void getAccountHistory(Account val) throws SQLException, PgBenchException {
		val.transactions = getTransactions(val.accountId);
	}
	private List<Transaction> getTransactions(long accountId) throws SQLException, PgBenchException  {
		
		Connection con = null; 
		PreparedStatement ps = null; 
		ResultSet rs = null;
		List<Transaction> list = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( m_sqlTextMgr3.getHistoryByAccountSql() );
			ps.setLong(1, accountId);
			rs = ps.executeQuery();
			list = new ArrayList<Transaction>();
			while(rs.next()) {
				Transaction t = new Transaction();
				t.tellerId = rs.getInt(1);
				t.historyId = rs.getLong(2);
				t.branchId = rs.getInt(3);
				t.accountId = rs.getInt(4);
				t.delta = rs.getLong(5);
				t.mtime = PgBenchUtil.getDate(rs, 6); 
				t.filler = rs.getString(7);
				list.add(t);
			}
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
		return list;
	}

}
