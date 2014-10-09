Chunky Outperforms Chatty
====================

This repo contains Java code and results from a performance comparison of 5 different SQL data access strategies, ranging from very chatty to very chunky.  

* Chatty = many SQL invocations that touch relative few records.  
* Chunky = fewer SQL invocations that touch more records.  

The two chunkiest scenarios (1 & 2) outperform the rest, with roughly 30% or more tps.

[Martin Fowler](http://martinfowler.com/) was widely ignored back in 2003 when he gave [this advice](http://www.informit.com/articles/article.aspx?p=30661&seqNum=3) for better SQL performance:

```
"Try to pull back multiple rows at once. In particular, 
never do repeated queries on the same table to get multiple rows."
```

That's the chunky approach.   Was he really ignored?  Seems like it.  Chatty db applications that perform poorly are everywhere ( [here](http://apmblog.compuware.com/2010/06/15/top-10-performance-problems-taken-from-zappos-monster-and-co/) [here](http://blogs.msdn.com/b/alikl/archive/2008/04/28/performance-sin-chatty-database-access-and-loops-plus-another-free-performance-tool.aspx) [here](http://dotnet.dzone.com/news/select-n1-problem-%E2%80%93-how)  ).  More than 10 years later, the results in the comparison below show that Martin Fowler was right.  Throughput goes up with fewer SQL invocations.  

Five Scenarios
--------------
In this repo, you'll find five different implementations to the same XML-over-HTTP web service:  a simple account and transaction inquiry to the [pgbench db](http://www.postgresql.org/docs/9.2/static/pgbench.html).  

The following graph shows throughput in red (tps / higher is better) and the scenario number in blue.
See how the blue line rises like steps?  The test ran each scenario for 1 minute, then moved on to the next larger scenario number:  1,2,3,4,5 and then it repeated 8 more times.  Each request inquired upon 5 differnt accounts.

![Inquiry for 5 accountIds](https://github.com/eostermueller/chunkyOutperformsChatty/blob/master/results/fiveAccounts/tps-by-scenario.png)

The requirements for the web service:  

```
Given 1 to N account numbers, return transaction history and balance info for all accounts.  
All 5 scenarios must build the response data using the exact same pojos 
and the same XML serialization code.
```

Scenario 1 has the fewest SQL invocations, 5 has the most (5 is a little extreme, actually), and 2, 3 and 4 line up inbetween (yes, in order).



| Tables        | PGBENCH_ACCOUNTS           | PGBENCH_HISTORY  | Notes |
| ------------- |:--------------|:----- |:-----|
| [Scenario_1](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_1)    | 1 SELECT, OUTER JOIN to PGBENCH_HISTORY      |   | The Chunkiest of the 5.  Rarely seen in the wild |
| [Scenario_2](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_2)    | 1 SELECT      |   1 SELECT | Rarely seen in the wild  |
| [Scenario_3](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_3)    | 1 SELECT      | 1 SELECT per account |  |
| [Scenario_4](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_4)    | 1 SELECT per account  | 1 SELECT per account |  |
| [Scenario_5](https://github.com/eostermueller/chunkyOutperformsChatty/tree/master/src/main/java/com/github/eostermueller/pgbench/dataaccess_5)    | 1 SELECT per account  | 1 SELECT PER account to retrieve unique IDs.  1 SELECT for each full history record. | The  Chattiest of the 5. |

## Instructions

1. Install PostGreSQL.  I used 9.2
2. Load pgbench sample data as detailed below.
3. Add a seuquence / primary key to the pgbench_history table.
4. [Download the war](https://github.com/eostermueller/chunkyOutperformsChatty/archive/master.zip) file and unzipt it to a blank folder.
5. Make sure the JDBC connection info is right in this file: ```./src/main/webapp/META-INF/context.xml```
5. Build it with  ```mvn clean package```
5. Deploy target/sqlPerfAntiPatterns.war to Tomcat 7+
6. Run the service using this URL:
```
localhost:8082/sqlPerfAntiPatterns/sqlPerfServlet?pgbenchScenarioNum=2&pgbenchAccountIds=34591,9483121,78941,111294,9122
```
The following populates the pgbench_accounts table, but not the pgbench_history table:
```
export DB_NAME=db_pgbench
export SCALE_FACTOR=100
export HOSTNAME=localhost
export PORT=5432
export USER=postgres

pgbench -i -s $SCALE_FACTOR -h $HOSTNAME -p $PORT -U $USER $DB_NAME
```

```
-- add primary key to history table
db_pgbench=# ALTER TABLE pgbench_history ADD COLUMN hid SERIAL PRIMARY KEY;
NOTICE:  ALTER TABLE will create implicit sequence "pgbench_history_hid_seq" for serial column "pgbench_history.hid"
NOTICE:  ALTER TABLE / ADD PRIMARY KEY will create implicit index "pgbench_history_pkey" for table "pgbench_history"
ALTER TABLE


--added index for searching history for account id's.
CREATE INDEX idx_aid on pgbench_history (aid);
```
