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
[
![](https://i.imgur.com/rfGTcHF.png)](http://naotu.baidu.com/file/125c3c198e6a6bba9da9617563ff3ea1?token=4a34f3bf6aea43db)

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

### Functional Dependencies (FD)

A functional dependency from X to Y : X → Y : attribute set X determines attribute set Y 

FD and Keys

<table>

<tr><th>Key 	Type</th><th>Function</th></tr>
<tr><td>Superkey</td><td>determines the entire tuple</td></tr>
<tr><td>candidate key(n)</td><td>A minimal set of attributes that determines the entire tuple </td></tr>
<tr><td>primary key</td><td>One of candidate key</td></tr>

</table>

a set of functional dependencies F can impley a set of other FD:
A→B, B→C ▶ A→C, A→(B,C)

The set of all FD logically implied by F is the **closure of F**: F<sup>+</sup>

####Armstrong’s Axioms
1. if Y is a subset of X, then X → Y (reflexivity)
2. if X → Y, then ZX → ZY (augmentation)
3. if X → Y and Y→ Z, then X → Z (transitivity)

####Addtional Rules:
1. If X → Y holds and X→Z holds, then X → YZ holds (union)
1. If X → YZ holds, then X→→ Y holds and X → Z holds (decomposition)
1. If X → Y holds and ZY→W holds, then ZX→W holds (pseudotransitivity)

#### Closure of Attribute Sets
X<sup>+</sup>: the set of attributes that are determined by X under F

#### Canonical Cover
Canonical Cover of F: F<sub>c</sub>, containing no redundancy
	
	repeat
		Use the union rule to replace any dependencies in F
			 X1 → Y1 and X1 → Y2 with X1 → Y1 Y2
		Find a functional dependency X → Y with an extraneous attribute either in X 	 or in Y 
		If an extraneous attribute is found, delete it from X → Y 
	until F does not change

![](https://i.imgur.com/EmaKJaA.png)



### Relational Database Design – 3NF

####Normalization: decomposing a relation schema R into fragments 
* Lossless decomposition
	* R → R1 and R2, the common attributes of R1 and R2 must be a super key for R1 or R2
* Dependency preservation
	* (∪ Fi)<sup>+</sup> = F<sup>+</sup> 
* Good form (little redundancy)
	* 1NF, 2NF, 3NF, BCNF

##### 2NF

	for each FD: X → {A} in F+:
		A ∈ X (the FD is trivial) OR
		X is not a proper subset of a candidate key for R, OR
		A is a prime attribute (an attribute that is part of a candidate key)

##### 3NF
	
	for each FD: X → {A} in F+:
		A ∈ X (the FD is trivial) OR
		X is a superkey for R, or
		A is a prime attribute for R
 * 3NF can still contain redundency, need to use null: [E→B, (B,C)→E]
##### 3NF Decomposition Algorithm
	Let R be the initial table with FDs F
	Compute the canonical cover Fc of F
	S=∅
	for each FD X→Y in the canonical cover Fc
		S=S∪(X,Y) 
	if no schema contains a candidate key for R
	 		Choose any candidate key K
		S=S ∪ K






### Relational Database Design – BCNF

	for each FD: X → {A} in F+,
		A ∈ X (trivial FD), or
		X is a superkey for R

* BCNF has no redundency but may not preserve all dependencies

#### Algorithhm:

	Let R be the initial schema with FDs F
	S={R}
	Until all relation schemas in S are in BCNF
	for each R in S 
		for each FD X → Y that violates BCNF for R
				S = (S – {R}) ∪ (R-Y) ∪ (X,Y)
	enduntil

	#########################
	When we find a schema R with BCNF violation X → Y we:
	1] Remove R from S
	2] Add a schema that has the same attributes as R except for Y
	3] Add a second schema that contains the attributes in X and Y




## Storage and File Systems
### Magnetic Disks

Store and retrieved data in units called blocks / pages
Slow

### File Organization

Database store data in files

Each file contains a sequence of records

- each file has only one type of record
- each file is for one relation only
- record are physically stored in block/page and one block contains records of same size

Each record is a sequence of fields 

Generally we can say
[![](https://i.imgur.com/BqdM7b6.png)](http://naotu.baidu.com/file/4b29878bf0c807d5af53452a7b815364?token=f31f7e70cb5c0a3b)

### How different kind of records are stored in file?

[![](https://i.imgur.com/q4zVw07.png)](http://naotu.baidu.com/file/6b0395013e2a33625c934a676bdf0a3e?token=b2bf4ab2933b2636)

## Tree and Hash Indexes

### Index

* Primary index (clustering index): data file is sorted on search key of index
* Secondary index (non-clustering index): when the file is not sorted on search key of index
* Sparse index: contains index records for some search-key values only (only for primary index since the data files are themselves sorted)
* Dense index: index that has an entry for every search key value

### B+ Tree Index	

#### Properties

* Balanced: All path from root to leaf are of the same length
* Each node has between ⌈n/2⌉ and n pointers
* Each Leaf node stores between ⌈(n-1)/2⌉ and n-1 data values
* n is called **fanout** and ⌈(n-1)/2⌉ is called the **order**
* if the root is not a leaf, it has at least 2 children, otherwise it has between 0 and n-1 data value

#### Leaf node:

[![https://i.imgur.com/1jYyCM1.png](https://i.imgur.com/1jYyCM1.png)](https://i.imgur.com/1jYyCM1.png)

#### Non-Leaf Node：

![](https://i.imgur.com/5sjT1kL.png)


#### Node relation:

![](https://i.imgur.com/YMll8fx.png)


#### Search for record with value k:

Start with the root node

1. If find an entry with key Kj = k, follow pointer Pj+1;
1. Otherwise, if k < Km–1, follow pointer Pj, where Kj is the smallest search-key value > k;
1. Otherwise, if k ≥ Km–1, follow Pm to the child node


If the current tree node is not a leaf node

1. repeat the above procedure on the node;
1. follow the corresponding pointer

Eventually reach a leaf node

1. If find an entry with key Ki = k, follow pointer Pi  to the desired record(s);
1. Else no record with key value k exists

#### Insertion:

Insert a data entry with search-key k

1. Search the tree using k
1. Find the correct leaf node L
1. Insert data entry into L
	1. If L has enough space, done!
	1. Else, split L (into L and a new node L2)
		1. Redistribute entries evenly, copy up middle key
		1. Update the parent of L by insert index entry pointing to L2

Updating can happen recursively

* To split index node, redistribute entries evenly, but push up middle key

Splits “grow” tree; root split increases height 

* Tree growth: gets wider or one level taller at top

[B+ Tree Animation](https://www.cs.usfca.edu/~galles/visualization/BPlusTree.html "B+ Tree Animation")

Deleion:

The idea is delete the entry and redistribute the node.
Delete the entry from the leaf, burrow from right sibling first, if right sibling has no enough entry to burrow then merge them. The redistribute the node

### Static Hashing

The idea of static hash is to use a hash function h, map each one of the records to one of M buckets. The buckets are a container of the hash index, pointing to the original data.

* is one bucket is already full (collision), we can add overflow buckets to it 
* a typical hash file is only 70%-80% full to avoid low efficiency caused by overflow buckets
* hashh index are always secondary index
* Number of M is fixed so it is not good to dynamic increasing data size

### Dynamic Hashing


The idea is when bucket become full, doubling the number of bucket and re-organize the file.

There are some smart way to do cheap re-organization like split the original bucket instead of adding new, for example, use dirrctory to of pointers to buckets.


#### Extensible hashing
[![](https://i.imgur.com/wi2OnHf.png)](https://i.imgur.com/wi2OnHf.png)
[![](https://i.imgur.com/WuhJnJL.png)](https://i.imgur.com/WuhJnJL.png)

depth mean the last n biary digit of the intergral it stores. when add 4, since 4 is 100 end with 00, we track the 00 pointer in the directory to the first bucket, but we find that it is already full, so we double the size of the directory and the 00 pointer now split into 2 pointers: 000 and 100 with depth 3, and re hash all the number in the origin 00 bucket to new bucket. We haven't change the depth of other bucket because they are not full so as you can see, 001 and 101 stil point to the same bucket.

## Query Processing
## Query Optimization 
## Physical Database Design
## Transactions
## Concurrency Control Protocols
## Database Recovery 

##Other learning Resource:

There is a Chinese passage [here](http://blog.jobbole.com/100349/) which is a very good summary for techniques in relational database. Hope you enjoy it.



