package com.github.eostermueller.pgbench.dataaccess_4;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchException;
import com.github.eostermueller.pgbench.PgBenchUtil;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Branch;
import com.github.eostermueller.pgbench.model.Transaction;


public class PkInquiry4 {
	public Account getAccount(long accountId) throws SQLException, PgBenchException {
		Account account = new Account();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( AccountMgr4.m_sqlTextMgr4.getAccountPkInquirySql() );
			ps.setLong(1, accountId);
			
			rs = ps.executeQuery();
			short count = 0;
			while(rs.next()) {
				if (++count >1)
					throw new PgBenchException("Expecting only a single account record, but found at least 2 for accountId [" + accountId + "]");
				account.accountId = rs.getLong(1);
				account.branchId = rs.getInt(2);
				account.balance = rs.getLong(3);
				account.filler = rs.getString(4);
			}
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
		return account;
	}
}
