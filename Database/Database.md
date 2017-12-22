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

###Aggregate Functions:
	
	avg(balance) % average value of that column
	min
	max
	sum
	count(balance) % number of row in that column

###Group By

	select branch-name, count([distinct] account-number)
	from account
	group by branch-name

![](https://i.imgur.com/I3i55mM.png)

* Attributes in select clause outside of aggregate functions must appear in group by list.
* You seperate the table according to the attributes in group by into serveral groups, then for each group, you apply the aggregate functions. 
* having can be used to filter the groups:

		having avg(balance)>700
	* having clause should be after where clause

### Derived Relations

In order to do some complex query, you may need a temp relation to help you

	select branch-name
	from(
		select branch-name, avg(balance)
		from account
		group by branch-name
		) as result(branch-name,avg-balance)
		
	where avg-balance = 
		(select max(avg-balance)
		from result)




Writing SQL for complex query is not an easy task. Need more practice.

## DDL: Data Definition Language



<table>
<caption>Domain Types in SQL</caption>
<tr><th>Type</th><th>Meaning</th></tr>
<tr><td>char(n)</td><td>fixed length string</td></tr>
<tr><td>varchar(n)</td><td>variable length string, maximum n</td></tr>
<tr><td>int</td><td>integer</td></tr>
<tr><td>smallint</td><td>defined by system</td></tr>
<tr><td>Numeric(p,d)</td><td>fixed point number, p digits precision with d digits on the right of decimal point</td></tr>
<tr><td>real,double</td><td>defined by system</td></tr>
<tr><td>float(n)</td><td>n digits precision float</td></tr>
<tr><td>date</td><td>year-month-date</td></tr>
<tr><td>time</td><td>hour-min-sec</td></tr>
</table>

### Create Table Example
	
	CREATE TABLE Students
	( 	sid: CHAR(20),
		name: CHAR(20),
		login: CHAR(10),
		age: INT,
		gpa: REAL
	)


### How to define integrity constraints

![](https://i.imgur.com/rfGTcHF.png)

	CREATE TABLE  Department(
	   did  INTEGER,
	   dname  CHAR(20),
	   budget  REAL,
	   HKID  CHAR(11) NOT NULL,

	   PRIMARY KEY  (did),
	   UNIQUE(dname,budget) % candidate key
	   FOREIGN KEY  (HKID) REFERENCES Employees,ON DELETE NO ACTION/CASCADE,      
	   check(dname in ("CS", "MATH"))
	   check(budget>20)
	   )

	* No action: won't delete when delete
	* CASCADE: if the foreign key is deleted from the foreign table, also delete the record in this table


### Destroying and Altering Relations
	
	DROP TABLE Students

	ALTER TABLE Students
		ADD COLUMN firstYear: integer

### Record Deletion/ Insertion / update
	
	delete from account
	where branch-name = "Perryridge"

	insert into account(col1,col2) values(value1,value2)
	
	insert into account select v1,v2 from loan where ...

	update account
	set balance = balance * 1.06
	where balance>1000


	update account
	set balance=case
					when balance<=1000
					then balance * 1.05
					else balance*1.06
				end



	
### Assetions and Trigger are very powerful but seldom used and they are quite complex

Please refer to [this](https://www.rose-hulman.edu/class/csse/csse333/Slides/Triggers.pdf)


## Functional Dependencies and Relational Database Design

### Functional Dependencies

### Relational Database Design – 3NF

### Relational Database Design – BCNF

## Storage and File Systems
## Tree and Hash Indexes
## Query Processing
## Query Optimization
## Physical Database Design
## Transactions
## Concurrency Control Protocols
## Database Recovery 

##Other learning Resource:

There is a Chinese passage [here](http://blog.jobbole.com/100349/) which is a very good summary for techniques in relational database. Hope you enjoy it.



