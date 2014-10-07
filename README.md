martinFowlerWasRight
====================

A performance comparison of 5 different SQL data access strategies (in Java).

[Martin Fowler](http://martinfowler.com/) was widely ignored when he gave [this advice](http://www.informit.com/articles/article.aspx?p=30661&seqNum=3) for better SQL performance:
```
"Try to pull back multiple rows at once. In particular, never do repeated queries on the same table to get multiple rows."::
```

Five Scenarios
--------------
This repo contains five different approaches to the same XML-over-HTTP web service:  a simple account and transaction inquiry to the [pgbench db](http://www.postgresql.org/docs/9.2/static/pgbench.html).  

The requirements:  
```
Given 1 to N account numbers, return transaction history and balance info for all accounts.  All 5 scenarios must use the exact same pojos, and same XML serialization code.
```

The smaller the scenario number, the fewer the SQL invocations to accomplish the exact same requirements.
1. A single SELECT to a JOIN of the PGBENCH_ACCOUNTS and PGBENCH_HISTORY tables.
2. Once SELECT to the PGBENCH_ACCOUNTS and an additional SELECT to the PGBENCH_HISTORY table.
3. Once SELECT to the PGBENCH_ACCOUNTS.  For each account, a separate SELECT to the PGBENCH_HISTORY.
4. For each account, one SELECT to the PGBENCH_ACCOUNTS.  For each account, a separate SELECT to the PGBENCH_HISTORY.
5. For each account, one SELECT to the PGBENCH_ACCOUNTS.  For each account, one SELECT to retrieve all PGBENCH_HISTORY unique id's.  One Select for each PGBENCH_HISTORY record.  Ouch.




