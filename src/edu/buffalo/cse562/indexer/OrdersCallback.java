package edu.buffalo.cse562.indexer;

import net.sf.jsqlparser.expression.DateValue;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;

public class OrdersCallback implements Callback {
  private final int index;
  private final int mode;
  private long      threshold;
  private long      threshold_five;
  
  public OrdersCallback(DataTable dataTable, int mode) {
    index = dataTable.getSchema().getLookupTable().get("orders.orderdate");
    threshold = new DateValue("'1996-03-15'").getValue().getTime();
    if (mode == 3) threshold = new DateValue("'1995-04-15'").getValue().getTime();
    threshold_five = new DateValue("'1995-03-15'").getValue().getTime();
    this.mode = mode;
  }

  @Override
  public boolean decide(Row row) {
    long time = ((DateValue) row.getValue(index)).getValue().getTime();
    if (mode == 5) return (time >= threshold_five);
    return (time >= threshold);
  }
}
