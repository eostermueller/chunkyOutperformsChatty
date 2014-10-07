package com.github.eostermueller.pgbench.dataaccess_5;

import java.sql.SQLException;
import java.util.List;

import com.github.eostermueller.pgbench.PgBench;
import com.github.eostermueller.pgbench.PgBenchException;
import com.github.eostermueller.pgbench.model.Account;
import com.github.eostermueller.pgbench.model.Accounts;

public class AccountMgr5 {
	private PkInquiry m_pkInquiry;
	private ListInquiry m_listInquiry;
	public static SqlTextMgr5 m_sqlTextMgr5 = new SqlTextMgr5();
	public AccountMgr5() {
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
		List<Long> historyIds = m_listInquiry.getTransactions(val.accountId);
		for( long historyId : historyIds) {
			val.transactions.add(m_pkInquiry.getTransaction(historyId));
		}
	}

}
