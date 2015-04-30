package edu.buffalo.cse562.indexer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class IndexManager {
  public static void preprocess() {
    for (String name : TableManager.getAllTableNames()) {
      System.out.println(name);
      System.gc();
      DataTable dataTable = TableManager.getTable(name);
      TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());
      ArrayList<Column> columns = dataTable.getSchema().getColumns();
      HashMap<Long, List<Row>> database = new HashMap<Long, List<Row>>();
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

      try {
        File databaseFile = new File(TableManager.getDbDir(), name.toLowerCase());
        FileOutputStream fout = new FileOutputStream(databaseFile);
        ObjectOutputStream out = new ObjectOutputStream(fout);
        out.writeUnshared(database);
        out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /** This class may never be instantiated. */
  private IndexManager() {}
}
