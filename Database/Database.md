# Database System Outline

## DBMS Overview

###Terminology
**Database Management System (DBMS)** is a software package that manages databases.

**Database** = interrelated data  + database applications

**Some database**

Oracle’s Oracle

IBM’s DB2

Microsoft’s Access, SQL Server

MySQL, PostgreSQL, MangoDB, …

###Data Independence: the separation of applications from data
####Solution: levels of abstraction

**Physical Level**: how record is stored on disks, file formats? Locations?

**Logical Level**: how data are structured in db, relationships among the data

**View Level**: Define a subset of database, hide some unrelative data, add some information derived from original record. Application access the database through view level. 

Each level is defined by a **schema**

**Database instance**: actual content of the db at a particular point in time

####Data Independence:  change in one level would not affect nearby level

Physical Data independence: Physical || Logical

Logical Data independence: Logical || View

![](https://i.imgur.com/HlsYJXJ.png)

## Entity Relationship (ER) Model

[![](https://i.imgur.com/S57FvXV.png)](http://naotu.baidu.com/file/3a27ac4e01e12769373c629144d22bb5?token=388d9290d2869238)







## Relational Model, Algebra
### Relational Model
[![](https://i.imgur.com/w1MC76o.png)](http://naotu.baidu.com/file/72ac0b2eb7d919fd2532e771088c6aa0?token=16f00c838c888e51)


### Relational Algebra
[![](https://i.imgur.com/RcLQhca.png)](http://naotu.baidu.com/file/d80d0785dee5f62e1801e589c9b5a62f?token=27faaf467d6b5506)

## SQL

Examples:

basic selection, projection

    select [distinct/all] branch-name,loan-number,amount*100
	from loan
	where branch-name="Perryridge" and amount>1200 and number between 10 and 20

join operation, rename operation and ordering

	select *
	from borrower as B,loan
	where B.loan-number = loan.loan-number and loan.text like "%Main%"
	order by loan.customer-name desc, amount asc

	% matchs any substring, _ match single character
	asc is default ordering type

Set operations

	(select customer-name from depositor)
	union/intersect/except [all]
	(select customer-name from borrower)

SQL Nested subqueries
	
	select * from loan
	where amount > select avg(amount) from loan

	select distinct customer-name
	from borrower
	where customer-name [not] in (select customer-name from depositor)

Some and all clause

	select branch-name
	from branch
	where assets > some/all
					(select assets from branch where branch-city = "Brooklyn")

Test for Empty Relations

	select customer-name from depositor as D where [not] exists(
	select * from borrower as B where D.customer-name=B.customer-name)

Thee above operation can be understood as an expensive query: first select all customer-name from depositor, then for each result, put it into the where cluster of the second query. Only when the second query returns a non-empty result, we keep that customer-name in the final result.

Test for Duplicate Tuples
	
	select T.customer-name
	from depositor as T
	where [not] unique(
		select R.customer-name
		from account,depositor as R
		where T.customer-name = R.customer-name and
		R.account-number = account.account-number and
		account.branch-name="Perryridge")

Division in SQL

Find all customers with an account at all branches located in Brooklyn.

	select distinct S.customer-name
	from depositor as S
	where not exist (
		(select branch-name
		from branch 
		where branch-city=“Brooklyn”)
	    except
		(select R.branch-name
		from depositor as T, account as R
		where T.account-number = R.account-number and S.customer-name = T.customer-name))
![](https://i.imgur.com/gEpyOXa.png)
Writing SQL for complex query is not an easy task. Need more practice.





## Functional Dependencies and Relational Database Design
## Storage and File Systems
## Tree and Hash Indexes
## Query Processing
## Query Optimization
## Physical Database Design
## Transactions
## Concurrency Control Protocols
## Database Recovery 



