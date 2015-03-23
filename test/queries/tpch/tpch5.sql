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
CREATE TABLE SUPPLIER (
        suppkey      INT,
        name         CHAR(25),
        address      VARCHAR(40),
        nationkey    INT,
        phone        CHAR(15),
        acctbal      DECIMAL,
        comment      VARCHAR(101)
    );
CREATE TABLE NATION (
        nationkey    INT,
        name         CHAR(25),
        regionkey    INT,
        comment      VARCHAR(152)
    );
CREATE TABLE REGION (
        regionkey    INT,
        name         CHAR(25),
        comment      VARCHAR(152)
    );

select
    nation.name,
    sum(lineitem.extendedprice * (1 - lineitem.discount)) as revenue
from
    customer,
    orders,
    lineitem,
    supplier,
    nation,
    region
where
    customer.custkey = orders.custkey
    and lineitem.orderkey = orders.orderkey
    and lineitem.suppkey = supplier.suppkey
    and customer.nationkey = supplier.nationkey
    and supplier.nationkey = nation.nationkey
    and nation.regionkey = region.regionkey
    and region.name = 'AMERICA'
    and orders.orderdate >= DATE('1995-03-15')
    and orders.orderdate < DATE('1996-03-15')
group by
    nation.name
order by
    revenue desc;
