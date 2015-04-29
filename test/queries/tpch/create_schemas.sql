CREATE TABLE LINEITEM (
  orderkey      INT REFERENCES ORDERS,
  partkey       INT REFERENCES PARTS,
  suppkey       INT REFERENCES SUPPLIERS,
  linenumber    INT,
  quantity      DECIMAL,
  extendedprice DECIMAL,
  discount      DECIMAL,
  tax           DECIMAL,
  returnflag    CHAR (1),
  linestatus    CHAR (1),
  shipdate      DATE,
  commitdate    DATE,
  receiptdate   DATE,
  shipinstruct  CHAR (25),
  shipmode      CHAR (10),
  comment       VARCHAR (44),
  PRIMARY KEY   (orderkey, linenumber)
);

CREATE TABLE ORDERS (
  orderkey      INT,
  custkey       INT REFERENCES CUSTOMER,
  orderstatus   CHAR (1),
  totalprice    DECIMAL,
  orderdate     DATE,
  orderpriority VARCHAR (15),
  clerk         VARCHAR (15),
  shippriority  INT,
  comment       VARCHAR (79),
  PRIMARY KEY   (orderkey)
);

CREATE TABLE CUSTOMER (
  custkey     INT,
  name        VARCHAR (25),
  address     VARCHAR (40),
  nationkey   INT REFERENCES NATION,
  phone       CHAR (15),
  acctbal     DECIMAL,
  mktsegment  CHAR (10),
  comment     VARCHAR (117),
  PRIMARY KEY (custkey)
);

CREATE TABLE NATION (
  nationkey   INT,
  name        CHAR (25),
  regionkey   INT REFERENCES REGION,
  comment     VARCHAR (152),
  PRIMARY KEY (nationkey)
);

CREATE TABLE SUPPLIER (
  suppkey     INT,
  name        CHAR (25),
  address     VARCHAR (40),
  nationkey   INT REFERENCES NATION,
  phone       CHAR (15),
  acctbal     DECIMAL,
  comment     VARCHAR (101),
  PRIMARY KEY (suppkey)
);

CREATE TABLE REGION (
  regionkey   INT,
  name        CHAR (25),
  comment     VARCHAR (152),
  PRIMARY KEY (regionkey)
);
