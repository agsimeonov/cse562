package edu.buffalo.cse562.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class IndexManager {
  public static void preprocess() { 
    // customer
    Map<Long, List<Row>> customer = getDatabase("customer", "customer.custkey");
    writeOut(customer, "customer");
    
    // orders
    Map<Long, List<Row>> orders = process(customer, "orders", "orders.custkey");
    
    // lineitem
    orders = rekey(orders, "orders", "orders.orderkey");
    System.gc();
    process(orders, "lineitem", "lineitem.orderkey");
    orders = null;
    System.gc();
    
    // supplier
    customer = rekey(customer, "customer", "customer.nationkey");
    System.gc();
    process(customer, "supplier", "supplier.suppkey");
    customer = null;
    System.gc();
    
    // nation
    writeOut(getDatabase("nation", "nation.nationkey"), "nation");
    System.gc();
    
    // region
    writeOut(getDatabase("region", "region.regionkey"), "region");
    System.gc();
  }
  
  private static Map<Long, List<Row>> rekey(Map<Long, List<Row>> db, String name, String colName) {
    Map<Long, List<Row>> database = new HashMap<Long, List<Row>>();
    DataTable dataTable = TableManager.getTable(name);
    ArrayList<Column> columns = dataTable.getSchema().getColumns();
    int index;
    for (index = 0; index < columns.size(); index++) {
      String columnName = columns.get(index).getWholeColumnName().toLowerCase();
      if (columnName.equals(colName)) break;
    }
    
    for (List<Row> list : db.values()) {
      for (Row value : list) {
        Long key = null;
        
        try {
          key = value.getValue(index).toLong();
        } catch (InvalidLeaf e) {
          e.printStackTrace();
        }
        
        List<Row> values = database.containsKey(key) ? database.get(key) : new ArrayList<Row>();
        values.add(value);
        database.put(key, values);
      }
    }
    
    return database;
  }
  
  private static Map<Long, List<Row>> process(Map<Long, List<Row>> left, 
                                              String name, 
                                              String colName) {
    Map<Long, List<Row>> full = getDatabase(name, colName);
    Map<Long, List<Row>> optimal = getOptimal(left, full);
    full = null;
    System.gc();
    writeOut(optimal, name);
    return optimal;
  }
  
  private static void writeOut(Map<Long, List<Row>> db, String name) {
    File file = new File(TableManager.getDbDir(), name + ".dat");

    try {
      FileWriter fileWriter = new FileWriter(file);
      BufferedWriter writer = new BufferedWriter(fileWriter);
      for (List<Row> list : db.values()) {
        for (Row row : list) {
          writer.write(row.toString());
          writer.newLine();
        }
      }
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static Map<Long, List<Row>> getOptimal(Map<Long, List<Row>> left, 
                                                 Map<Long, List<Row>> right) {
    Map<Long, List<Row>> optimal = new HashMap<Long, List<Row>>();
    for (Long key : left.keySet()) {
      if (right.containsKey(key)) optimal.put(key, right.get(key));
    }
    return optimal;
  }
  
  private static Map<Long, List<Row>> getDatabase(String name, String colName) {
    DataTable dataTable = TableManager.getTable(name);
    TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());
    ArrayList<Column> columns = dataTable.getSchema().getColumns();
    Map<Long, List<Row>> database = new HashMap<Long, List<Row>>();
    int index;
    
    for (index = 0; index < columns.size(); index++) {
      String columnName = columns.get(index).getWholeColumnName().toLowerCase();
      if (columnName.equals(colName)) break;
    }
    
    while (iterator.hasNext()) {
      Row value = iterator.next();
      Long key = null;
      
      try {
        key = value.getValue(index).toLong();
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }
      
      List<Row> values = database.containsKey(key) ? database.get(key) : new ArrayList<Row>();
      values.add(value);
      database.put(key, values);
    }
    
    return database;
  }

  /** This class may never be instantiated. */
  private IndexManager() {}
}
