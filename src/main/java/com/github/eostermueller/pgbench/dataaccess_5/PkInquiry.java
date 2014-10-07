package com.github.eostermueller.pgbench.dataaccess_5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchException;
import com.github.eostermueller.pgbench.PgBenchUtil;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;
import com.github.eostermueller.pgbench.model.Branch;
import com.github.eostermueller.pgbench.model.Transaction;

public class PkInquiry {
	public Account getAccount(long accountId) throws SQLException, PgBenchException {
		Account account = new Account();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( AccountMgr5.m_sqlTextMgr5.getAccountPkInquirySql() );
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
	public Branch getBranch(int branchId) throws SQLException, PgBenchException {
		Branch branch = new Branch();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( AccountMgr5.m_sqlTextMgr5.getBranchPkInquirySql() );
			ps.setInt(1, branchId);
			rs = ps.executeQuery();
			short count = 0;
			while(rs.next()) {
				if (++count >1)
					throw new PgBenchException("Expecting only a single account record, but found at least 2 for accountId [" + branchId + "]");
				branch.bid = rs.getInt(1);
				branch.bbalance = rs.getLong(2);
				branch.filler = rs.getString(3);
			}
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
		return branch;
	}
	public Transaction getTransaction(long transactionId) throws SQLException, PgBenchException {
		Transaction transaction = new Transaction();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = PgBench.SINGLETON.getConnection();
			ps = con.prepareStatement( AccountMgr5.m_sqlTextMgr5.getHistoryPkInquirySql() );
			ps.setLong(1, transactionId);
			rs = ps.executeQuery();
			short count = 0;
			while(rs.next()) {
				if (++count >1)
					throw new PgBenchException("Expecting only a single account record, but found at least 2 for accountId [" + transactionId + "]");
				//		return "SELECT tid, hid, bid, aid, delta, mtime, filler from " + m_tableNames.getHistoryTable() + " WHERE tid = ?";
				transaction.tellerId = rs.getInt(1);
				transaction.historyId = rs.getLong(2);
				transaction.branchId = rs.getInt(3);
				transaction.accountId = rs.getLong(4);
				transaction.delta = rs.getLong(5);
				transaction.mtime = PgBenchUtil.getDate(rs, 6);
				transaction.filler = rs.getString(7);
			}
		} finally {
			PgBenchUtil.closeQuietly(rs);
			PgBenchUtil.closeQuietly(ps);
			PgBenchUtil.closeQuietly(con);
		}
			return transaction;
		}
}
