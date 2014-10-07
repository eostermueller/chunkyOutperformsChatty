package com.github.eostermueller.pgbench;

public class TableNames {
	public String getAccountTable() {
		return getTable("pgbench_accounts");
	}
	public String getBranchTable() {
		return getTable("pgbench_branches");
	}
	public String getHistoryTable() {
		return getTable("pgbench_history");
	}
	public String getTellerTable() {
		return getTable("pgbench_tellers");
	}
	private String m_schema = null;
	public void setSchema(String val) {
		m_schema = val;
	}

	private String getTable(String tableName) {
		if (getSchema()==null)
			return tableName;
		else
			return getSchema() + "." + tableName;
	}
	private String getSchema() {
		return m_schema;
	}
}
