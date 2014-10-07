Chunky Outperforms Chatty
====================

This repo contains Java code and results from a performance comparison of 5 different SQL data access strategies, ranging from very chatty to very chunky.  Chatty = many SQL invocations that touch relative few records.  Chunky = fewer SQL invocations that touch more records).  The two chunkiest scenarios (1 & 2) outperform the rest, with 30% or more tps.

[Martin Fowler](http://martinfowler.com/) was widely ignored back in 2003 when he gave [this advice](http://www.informit.com/articles/article.aspx?p=30661&seqNum=3) for better SQL performance:

```
"Try to pull back multiple rows at once. In particular, 
never do repeated queries on the same table to get multiple rows."
```

Was he really ignored?  Seems like it.  Chatty db applications that perform poorly are everywhere ( [here](http://apmblog.compuware.com/2010/06/15/top-10-performance-problems-taken-from-zappos-monster-and-co/) [here](http://blogs.msdn.com/b/alikl/archive/2008/04/28/performance-sin-chatty-database-access-and-loops-plus-another-free-performance-tool.aspx) [here](http://dotnet.dzone.com/news/select-n1-problem-%E2%80%93-how)  ).  More than 10 years later, these results show that Martin Fowler was right.  Throughput goes up with fewer SQL invocations.  

Five Scenarios
--------------
This repo contains five different SQL approaches to the same XML-over-HTTP web service:  a simple account and transaction inquiry to the [pgbench db](http://www.postgresql.org/docs/9.2/static/pgbench.html).  

The following graph shows throughput in red (tps / higher is better) and the scenario number in blue.
See how the blue line rises like steps?  The test ran each scenario for 1 minute, then moved on to the next larger scenario number:  1,2,3,4,5 and then it repeated 8 more times.
![Inquiry for 5 accountIds](https://github.com/eostermueller/chunkyOutperformsChatty/blob/master/results/fiveAccounts/tps-by-scenario.png)

The requirements:  

```
Given 1 to N account numbers, return transaction history and balance info for all accounts.  
All 5 scenarios must build the response data using the exact same pojos 
and the same XML serialization code.
```

The smaller the scenario number, the fewer the SQL invocations to accomplish the exact same requirements.


| Tables        | PGBENCH_ACCOUNTS           | PGBENCH_HISTORY  | Notes |
| ------------- |:--------------|:----- |:-----|
| [Scenario_1](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_1)    | 1 SELECT, OUTER JOIN to PGBENCH_HISTORY      |   | The Chunkiest of the 5.  Rarely seen in the wild |
| [Scenario_2](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_2)    | 1 SELECT      |   1 SELECT | Rarely seen in the wild  |
| [Scenario_3](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_3)    | 1 SELECT      | 1 SELECT per account |  |
| [Scenario_4](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_4)    | 1 SELECT per account  | 1 SELECT per account |  |
| [Scenario_5](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_5)    | 1 SELECT per account  | 1 SELECT PER account to retrieve unique IDs.  1 SELECT for each full history record. | The  Chattiest of the 5. |
