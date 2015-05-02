package edu.buffalo.cse562.indexer;

import net.sf.jsqlparser.expression.DateValue;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;

public class OrdersCallback implements Callback {
  private final long THRESHOLD = new DateValue("'1996-03-15'").getValue().getTime();
  private final int  index;
  
  public OrdersCallback(DataTable dataTable) {
    index = dataTable.getSchema().getLookupTable().get("orders.orderdate");
  }

  @Override
  public boolean decide(Row row) {
    long time = ((DateValue) row.getValue(index)).getValue().getTime();
    return (time >= THRESHOLD);
  }
}
