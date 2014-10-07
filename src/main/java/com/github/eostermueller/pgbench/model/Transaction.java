package com.github.eostermueller.pgbench.model;

import java.util.Date;

import com.github.eostermueller.pgbench.PgBenchUtil;

public class Transaction {
//tid  | bid  | aid | delta |           mtime            | filler
	public long historyId = PgBenchUtil.UN_INIT;
	public int tellerId = PgBenchUtil.UN_INIT;
	public int branchId = PgBenchUtil.UN_INIT;
	public long accountId  = PgBenchUtil.UN_INIT;
	public long delta  = PgBenchUtil.UN_INIT;
	public Date mtime = null;
	public String filler = null;
	
}
