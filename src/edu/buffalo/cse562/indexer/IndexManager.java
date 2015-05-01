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
    Map<Long, List<Row>> customer = getDatabase("customer");
    writeOut(customer, "customer");
    
    // orders
    Map<Long, List<Row>> orders = process(customer, "orders");
    customer = null;
    System.gc();
    
    // lineitem
    Map<Long, List<Row>> lineitem = process(orders, "lineitem");
    orders = null;
    System.gc();
    
    // supplier
    process(lineitem, "supplier");
    
    // nation
    Map<Long, List<Row>> nation = process(lineitem, "nation");
    lineitem = null;
    System.gc();
    
    // region
    process(nation, "region");
    nation = null;
    System.gc();
  }
  
  private static Map<Long, List<Row>> process(Map<Long, List<Row>> left, String name) {
    Map<Long, List<Row>> full = getDatabase(name);
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
  
  private static Map<Long, List<Row>> getDatabase(String name) {
    DataTable dataTable = TableManager.getTable(name);
    TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());
    ArrayList<Column> columns = dataTable.getSchema().getColumns();
    Map<Long, List<Row>> database = new HashMap<Long, List<Row>>();
    int index;
    
    for (index = 0; index < columns.size(); index++) {
      String columnName = columns.get(index).getWholeColumnName().toLowerCase();
      boolean found = false;
      switch (name.toLowerCase()) {
        case "lineitem":
          if (columnName.equals("lineitem.orderkey")) found = true;
          break;
        case "orders":
          if (columnName.equals("orders.custkey")) found = true;
          break;
        case "supplier":
          if (columnName.equals("supplier.nationkey")) found = true;
          break;
        case "nation":
          if (columnName.equals("nation.nationkey")) found = true;
          break;
        case "region":
          if (columnName.equals("region.regionkey")) found = true;
          break;
        case "customer":
          if (columnName.equals("customer.custkey")) found = true;
      }
      if (found) break;
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
