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
## Functional Dependencies and Relational Database Design
## Storage and File Systems
## Tree and Hash Indexes
## Query Processing
## Query Optimization
## Physical Database Design
## Transactions
## Concurrency Control Protocols
## Database Recovery 



