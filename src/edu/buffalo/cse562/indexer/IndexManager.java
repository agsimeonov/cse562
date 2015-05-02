package edu.buffalo.cse562.indexer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class IndexManager {
  private static final String CUSTOMER = "customer";
  public static final String  ORDERS   = "orders";
  private static final String LINEITEM = "lineitem";
  private static final String SUPPLIER = "supplier";
  private static final String NATION   = "nation";
  private static final String REGION   = "region";

  public static void preprocess() { 
    // customer
    copyPaste(CUSTOMER);
    
    // orders
    DataTable dataTable = TableManager.getTable(ORDERS);
    Callback callback = new OrdersCallback(dataTable);
    filter(ORDERS, callback);
    
    // lineitem
    dataTable = TableManager.getTable(LINEITEM);
    callback = new LineitemCallback(dataTable);
    filter(LINEITEM, callback);
    
    // supplier
    copyPaste(SUPPLIER);
    
    // nation
    copyPaste(NATION);
    
    // region
    copyPaste(REGION);
  }
  
  public static Map<Long, List<Row>> getDatabase(String name, String key, File file) {
    DataTable dataTable = TableManager.getTable(name);
    dataTable.setDataFile(file);
    TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());
    int index = dataTable.getSchema().getLookupTable().get(key);
    Map<Long, List<Row>> out = new HashMap<Long, List<Row>>();
    
    while (iterator.hasNext()) {
      Row next = iterator.next();
      
      try {
        long keyLong = next.getValue(index).toLong();
        List<Row> value = out.containsKey(keyLong) ? out.get(keyLong) : new ArrayList<Row>();
        value.add(next);
        out.put(keyLong, value);
      } catch (InvalidLeaf e) {
        e.printStackTrace();
      }
    }
    
    return out;
  }
  
  private static void filter(String name, Callback callback) {
    DataTable dataTable = TableManager.getTable(name);
    TableIterator iterator = new TableIterator(dataTable.getTable(), dataTable.getSchema());

    try {
      FileWriter fwriter = new FileWriter(getDestination(name));
      BufferedWriter writer = new BufferedWriter(fwriter);
      
      while (iterator.hasNext()) {
        Row next = iterator.next();
        if (callback.decide(next)) continue; 
        writer.write(next.toString());
        writer.newLine();
      }
      
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private static File getDestination(String name) {
    return new File(TableManager.getDbDir(), name.toLowerCase() + ".dat");
  }
  
  private static void copyPaste(String name) {
    File source = TableManager.getTable(name).getDataFile();
    File destination = getDestination(name);
    FileOutputStream fout;
    try {
      fout = new FileOutputStream(destination);
      BufferedOutputStream out = new BufferedOutputStream(fout);
      Files.copy(source.toPath(), out);
      out.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /** This class may never be instantiated. */
  private IndexManager() {}
}
