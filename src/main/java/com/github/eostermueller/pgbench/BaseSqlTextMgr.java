package com.github.eostermueller.pgbench;

public class BaseSqlTextMgr {
	protected TableNames m_tableNames = new TableNames();


	public String getBranchCount() {
		return "select count(*) from " + m_tableNames.getBranchTable();
	}
}
