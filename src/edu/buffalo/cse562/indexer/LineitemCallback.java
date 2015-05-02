package edu.buffalo.cse562.indexer;

import java.io.File;
import java.util.Set;

import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class LineitemCallback implements Callback {
  private final int index;
  private Set<Long> keySet;

  public LineitemCallback(DataTable dataTable) {
    File ordersFile = new File(TableManager.getDbDir(), IndexManager.ORDERS + ".dat");
    keySet = IndexManager.getDatabase(IndexManager.ORDERS, "orders.orderkey", ordersFile).keySet();
    index = dataTable.getSchema().getLookupTable().get("lineitem.orderkey");
  }
  
  @Override
  public boolean decide(Row row) {
    try {
      return !keySet.contains(row.getValue(index).toLong());
    } catch (InvalidLeaf e) {
      e.printStackTrace();
      return false;
    }
  }
}
