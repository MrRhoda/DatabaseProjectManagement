create database if not exists poiseprojects_db;


create table ERFNumbers
(Address varchar(100),
ERFNumber varchar(10),
primary key(Address));

create table Architects
(Name varchar(50),
Tel varchar(10),
Email varchar(50),
Address varchar(100),
primary key(Name));

create table Customers
(Name varchar(50),
Tel varchar(10),
Email varchar(50),
Address varchar(100),
primary key(Name));

create table Contractors
(Name varchar(50),
Tel varchar(10),
Email varchar(50),
Address varchar(100),
primary key(Name));

create table Projects
(Number varchar(15) UNIQUE,
Name varchar(50),
BuildingType varchar(50),
Address varchar(100),
TotalFee float,
PaidToDate float,
DeadLine Date,
CompletionDate Date,
ContractorName varchar(50) UNIQUE,
ArchitectName varchar(50) UNIQUE,
CustomerName varchar(50) UNIQUE,
Finalised varchar(5),
primary key (Number),
foreign key (Address) references ERFNumbers(Address),
foreign key (ContractorName) references Contractors(Name),
foreign key (ArchitectName) references Architects(Name),
foreign key (CustomerName) references Customers(Name));

insert into ERFNumbers values('100 Whale Street', '12345');
insert into ERFNumbers values('274 Mckena ave', '67890');
insert into ERFNumbers values('01 Duberry Lane', '83104');
select * from ERFNumbers;

insert into Contractors values('Jim', '0771472784', 'jim@contract.co.za', '71 Monday Lane');
insert into Contractors values('Ammaar', '0718294274', 'ammaar@wecontract.co.za', '7 Geek street');
insert into Contractors values('Royce', '082742596', 'royce@hello.co.za', '5 Bakka street');
select * from Contractors;

insert into Architects values('Fuad', '0745518344', 'fuad@bizz.co.za', '100 Joanne Road');
insert into Architects values('Karlie', '0623524991', 'karlie@arc.co.za', '656 Main road');
insert into Architects values('Yahya', '0828546983', 'hotmail.com', '42 Long Ave');
select * from Architects;

insert into Customers values('Tabu', '0716421275', 'tabu@gmail.com', '47 Trent lane');
insert into Customers values('Matthew', '0745231788', 'matt@gmail.com', '18 Loop Street');
insert into Customers values('Yusuf', '0915441336', 'yusuf@gmail.com', '14 Mankind Lane');
select * from Customers;

insert into projects values(
'1001',
'House Tabu',
'House',
'100 Whale Street',
3500000,
1750000,
'2024-08-21',
NULL,
'Jim',
'Fuad',
'Tabu',
'false');

insert into projects values(
'1002',
'Apartment Matthew',
'Apartment',
'274 Mckena ave',
1500000,
350000,
'2023-08-21',
NULL,
'Ammaar',
'Karlie',
'Matthew',
'false');

insert into projects values(
'1003',
'House Yusuf',
'House',
'01 Duberry Lane',
2700000,
2500000,
'2023-04-29',
NULL,
'Royce',
'Yahya',
'Yusuf',
'false');