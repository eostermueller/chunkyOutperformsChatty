martinFowlerWasRight
====================

A performance comparison of 5 different SQL data access strategies (in Java).  The results show that throughput goes up with fewer SQL invocations.

[Martin Fowler](http://martinfowler.com/) was widely ignored when he gave [this advice](http://www.informit.com/articles/article.aspx?p=30661&seqNum=3) for better SQL performance:

```
"Try to pull back multiple rows at once. In particular, 
never do repeated queries on the same table to get multiple rows."
```

Five Scenarios
--------------
This repo contains five different approaches to the same XML-over-HTTP web service:  a simple account and transaction inquiry to the [pgbench db](http://www.postgresql.org/docs/9.2/static/pgbench.html).  

The requirements:  

```
Given 1 to N account numbers, return transaction history and balance info for all accounts.  
All 5 scenarios must use the exact same pojos, and same XML serialization code.
```

The smaller the scenario number, the fewer the SQL invocations to accomplish the exact same requirements.



| Tables        | PGBENCH_ACCOUNTS           | PGBENCH_HISTORY  |
| ------------- |:--------------|:----- |
| Scenario_1    | 1 SELECT, OUTER JOIN to PGBENCH_HISTORY      |  |
| Scenario_2    | 1 SELECT      |   1 SELECT |
| Scenario_3    | 1 SELECT      | 1 SELECT per account |
| Scenario_4    | 1 SELECT per account  | 1 SELECT per account |
| Scenario_5    | 1 SELECT per account  | 1 SELECT PER account to retrieve unique IDs.  1 SELECT for each full history record. |
