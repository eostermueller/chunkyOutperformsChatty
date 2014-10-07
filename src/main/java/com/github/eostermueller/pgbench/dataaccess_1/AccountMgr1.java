package com.github.eostermueller.pgbench.dataaccess_1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchUtil;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.Transaction;

public class AccountMgr1 {
	public static SqlTextMgr1 m_sqlTextMgr1 = new SqlTextMgr1();
	public Accounts getAccounts(List<Long> accountIdsCriteria) throws ServletException  {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Accounts accounts = new Accounts();
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( m_sqlTextMgr1.getAccountAndHistorySql(accountIdsCriteria.size()) );
			for (int i = 1; i <= accountIdsCriteria.size(); i++) {
				ps.setLong(i, accountIdsCriteria.get(i-1).longValue());
			}
			rs = ps.executeQuery();
			short count = 0;
			
			while(rs.next()) {
				getAccountAndTxData( rs, accounts );
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

	private void getAccountAndTxData(ResultSet rs, Accounts accounts) throws SQLException {
		long accountId = rs.getLong(1);
		Account account = accounts.findAccount(accountId);
		if (account==null) {
			account = createAccount(rs);
			accounts.addAccount(account);
		}
		Transaction t = createTransactions(rs);
		account.transactions.add(t);
	}

	private Transaction createTransactions(ResultSet rs) throws SQLException {
		Transaction t = new Transaction();
		t.accountId = rs.getLong(1);
		t.branchId = rs.getInt(3);
		t.tellerId = rs.getInt(5);
		t.historyId = rs.getLong(6);
		t.delta = rs.getLong(7);
		t.mtime = PgBenchUtil.getDate(rs, 8);
		t.filler = rs.getString(9);
		return t;
	}

	private Account createAccount(ResultSet rs) throws SQLException {
		Account a = new Account();
		a.accountId = rs.getLong(1);
		a.branchId = rs.getInt(2);
		a.balance = rs.getLong(3);
		a.filler = rs.getString(4);
		return a;
	}

}
