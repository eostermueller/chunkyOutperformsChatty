package com.github.eostermueller.pgbench.dataaccess_4;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchException;
import com.github.eostermueller.pgbench.PgBenchUtil;
import com.github.eostermueller.pgbench.dataaccess_4.ListInquiry;
import com.github.eostermueller.pgbench.dataaccess_4.PkInquiry;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.Transaction;

public class AccountMgr4 extends AccountMgr5 {
	private PkInquiry m_pkInquiry;
	private ListInquiry m_listInquiry;
		
	public static SqlTextMgr4 m_sqlTextMgr4 = new SqlTextMgr4();
	public AccountMgr4() {
		m_pkInquiry = PgBench.SINGLETON.getPkInquiry();
		m_listInquiry = PgBench.SINGLETON.getListInquiry();
	}
	public Accounts getAccounts(List<Long> randomAccountIds) {
		Accounts accounts = new Accounts();
		for( long accountId : randomAccountIds) {
			try {
				Account a = m_pkInquiry.getAccount(accountId);
				getAccountHistory(a);
				accounts.addAccount( a );
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (PgBenchException e) {
				e.printStackTrace();
			}
		}
		return accounts;
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
			ps = con.prepareStatement( m_sqlTextMgr4.getHistoryByAccountSql() );
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
