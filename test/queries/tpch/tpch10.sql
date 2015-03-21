CREATE TABLE LINEITEM (
        orderkey       INT,
        partkey        INT,
        suppkey        INT,
        linenumber     INT,
        quantity       DECIMAL,
        extendedprice  DECIMAL,
        discount       DECIMAL,
        tax            DECIMAL,
        returnflag     CHAR(1),
        linestatus     CHAR(1),
        shipdate       DATE,
        commitdate     DATE,
        receiptdate    DATE,
        shipinstruct   CHAR(25),
        shipmode       CHAR(10),
        comment        VARCHAR(44)
    );
CREATE TABLE ORDERS (
        orderkey       INT,
        custkey        INT,
        orderstatus    CHAR(1),
        totalprice     DECIMAL,
        orderdate      DATE,
        orderpriority  VARCHAR(15),
        clerk          VARCHAR(15),
        shippriority   INT,
        comment        VARCHAR(79)
    );
CREATE TABLE CUSTOMER (
        custkey      INT,
        name         VARCHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        CHAR(15),
        acctbal      DECIMAL,
        mktsegment   CHAR(10),
        comment      VARCHAR(117)
    );
CREATE TABLE NATION (
        nationkey    INT,
        name         CHAR(25),
        regionkey    INT,
        comment      VARCHAR(152)
    );

select
	customer.custkey,
	customer.name,
	sum(lineitem.extendedprice * (1 - lineitem.discount)) as revenue,
	customer.acctbal,
	nation.name,
	customer.address,
	customer.phone,
	customer.comment
from
	customer,
	orders,
	lineitem,
	nation
where
	customer.custkey = orders.custkey
	and lineitem.orderkey = orders.orderkey
	and orders.orderdate >= DATE('1995-03-15')
	and orders.orderdate < DATE('1996-03-15')
	and lineitem.returnflag = 'R'
	and customer.nationkey = nation.nationkey
group by
	customer.custkey,
	customer.name,
	customer.acctbal,
	customer.phone,
	nation.name,
	customer.address,
	customer.comment
order by
	revenue desc;
