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
import com.github.eostermueller.pgbench.model.Transaction;

public class ListInquiry {

	public List<Long> getTransactions(long accountId) throws SQLException, PgBenchException  {
		
		Connection con = null; 
		PreparedStatement ps = null; 
		ResultSet rs = null;
		List<Long> list = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( AccountMgr5.m_sqlTextMgr5.getHistoryListSql() );
			ps.setLong(1, accountId);
			rs = ps.executeQuery();
			list = new ArrayList<Long>();
			while(rs.next()) {
				list.add ( rs.getLong(1));
			}
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
		return list;
	}
		
}
