Martin Fowler Was Right
====================

This repo contains code and results from a performance comparison of 5 different SQL data access strategies (in Java).

[Martin Fowler](http://martinfowler.com/) was widely ignored back in 2003 when he gave [this advice](http://www.informit.com/articles/article.aspx?p=30661&seqNum=3) for better SQL performance:

```
"Try to pull back multiple rows at once. In particular, 
never do repeated queries on the same table to get multiple rows."
```

Was he really ignored?  Seems like it.  Reports of chatty db applications are everywhere ( [here](http://apmblog.compuware.com/2010/06/15/top-10-performance-problems-taken-from-zappos-monster-and-co/) [here](http://blogs.msdn.com/b/alikl/archive/2008/04/28/performance-sin-chatty-database-access-and-loops-plus-another-free-performance-tool.aspx)  ).  More than 10 years later, these results show that he was right.  Throughput goes up with fewer SQL invocations.  The rarely used techniques in scenarios 1 & 2 have 30% or more throughput than the other scenarios.  

Five Scenarios
--------------
This repo contains five different approaches to the same XML-over-HTTP web service:  a simple account and transaction inquiry to the [pgbench db](http://www.postgresql.org/docs/9.2/static/pgbench.html).  

The requirements:  

```
Given 1 to N account numbers, return transaction history and balance info for all accounts.  
All 5 scenarios must build the response data using the exact same pojos 
and the same XML serialization code.
```

The smaller the scenario number, the fewer the SQL invocations to accomplish the exact same requirements.



| Tables        | PGBENCH_ACCOUNTS           | PGBENCH_HISTORY  | Notes |
| ------------- |:--------------|:----- |:-----|
| Scenario_1    | 1 SELECT, OUTER JOIN to PGBENCH_HISTORY      |   | Rarely seen in the wild |
| Scenario_2    | 1 SELECT      |   1 SELECT | Rarely seen in the wild  |
| Scenario_3    | 1 SELECT      | 1 SELECT per account |  |
| Scenario_4    | 1 SELECT per account  | 1 SELECT per account |  |
| Scenario_5    | 1 SELECT per account  | 1 SELECT PER account to retrieve unique IDs.  1 SELECT for each full history record. |  |
