package com.github.eostermueller.pgbench.dataaccess_4;

import java.util.concurrent.atomic.AtomicLong;

import com.github.eostermueller.pgbench.BaseSqlTextMgr;
import com.github.eostermueller.pgbench.TableNames;

public class SqlTextMgr4 extends BaseSqlTextMgr {
	public Stats m_stats = new Stats();
	
	public String getHistoryByAccountSql() {
		this.m_stats.m_historyByAccountSql.incrementAndGet();
		return "SELECT tid, hid, bid, aid, delta, mtime, filler from " + m_tableNames.getHistoryTable() + " WHERE aid = ?";
	}
	public String getAccountPkInquirySql() {
		this.m_stats.m_accountPkInquirySql.incrementAndGet();
		return "SELECT aid, bid, abalance, filler from " + m_tableNames.getAccountTable() + " WHERE aid = ?";
	}
	
	public static class Stats {
		public AtomicLong m_accountPkInquirySql = new AtomicLong();
		public AtomicLong m_historyByAccountSql = new AtomicLong();
		public String getXmlStats() {
			StringBuilder sb = new StringBuilder();
			sb.append("<historyByAccountSql>" + m_historyByAccountSql  + "</historyPkInquirySql>");
			sb.append("<accountPkInqSql>" + m_accountPkInquirySql  + "</historyPkInquirySql>");
			return sb.toString();
		}
	
	}
	
}
