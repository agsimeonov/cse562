package edu.buffalo.cse562.indexer;

import java.sql.Date;
import java.util.Calendar;

import net.sf.jsqlparser.expression.DateValue;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;

public class OrdersCallback implements Callback {
  private final int index;
  private final int mode;
  private long      threshold;
  private long      upper;
  private long      lower;
  
  public OrdersCallback(DataTable dataTable, int mode) {
    index = dataTable.getSchema().getLookupTable().get("orders.orderdate");
    threshold = new DateValue("'1996-03-15'").getValue().getTime();
    this.mode = mode;
  }
  
  @SuppressWarnings("deprecation")
  public OrdersCallback(DataTable dataTable, int mode, int add) {
    index = dataTable.getSchema().getLookupTable().get("orders.orderdate");
    Date low = new DateValue("'1992-01-01'").getValue();
    Date high = new DateValue("'1993-01-01'").getValue();
    low.setYear(low.getYear() + (Calendar.YEAR * add));
    high.setYear(high.getYear() + (Calendar.YEAR * add));
    lower = low.getTime();
    upper = high.getTime();
    this.mode = mode;
  }

  @Override
  public boolean decide(Row row) {
    long time = ((DateValue) row.getValue(index)).getValue().getTime();
    if (mode == 5) return (time >= upper) || (time < lower);
    return (time >= threshold);
  }
}
