package edu.buffalo.cse562.indexer;

import java.io.File;
import java.util.Set;

import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

public class LineitemCallback implements Callback {
  private final int lineitem_orderkey;
  private final int lineitem_shipdate;
  private final int lineitem_returnflag;
  private final int mode;
  private Set<Long> keySet;
  private long      threshold;

  public LineitemCallback(DataTable dataTable, int mode) {
    File ordersFile = new File(TableManager.getDbDir(), IndexManager.ORDERS + ".dat");
    keySet = IndexManager.getDatabase(IndexManager.ORDERS, "orders.orderkey", ordersFile).keySet();
    lineitem_orderkey = dataTable.getSchema().getLookupTable().get("lineitem.orderkey");
    lineitem_shipdate = dataTable.getSchema().getLookupTable().get("lineitem.shipdate");
    lineitem_returnflag = dataTable.getSchema().getLookupTable().get("lineitem.returnflag");
    if (mode == 3) threshold = new DateValue("'1995-02-15'").getValue().getTime();
    this.mode = mode;
  }
  
  @Override
  public boolean decide(Row row) {
    try {
      if (mode == 3) {
        long time = ((DateValue) row.getValue(lineitem_shipdate)).getValue().getTime();
        return (time <= threshold) ||!keySet.contains(row.getValue(lineitem_orderkey).toLong());
      }
      if (mode == 10) {
        String flag = row.getValue(lineitem_returnflag).toString();
        if (!flag.equals("'R'")) return true;
      }
      return !keySet.contains(row.getValue(lineitem_orderkey).toLong());
    } catch (InvalidLeaf e) {
      e.printStackTrace();
      return false;
    }
  }
}
