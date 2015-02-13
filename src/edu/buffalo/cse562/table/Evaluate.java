package edu.buffalo.cse562.table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.Eval;

public class Evaluate extends Eval {
  HashMap<String, Iterator<LeafValue[]>> iterators = new HashMap<String, Iterator<LeafValue[]>>();

  @Override
  public LeafValue eval(Column column) throws SQLException {
    DataTable table = TableManager.getTable(column.getTable().getName());
    Column[] schema = table.getSchema();
    
    if (iterators.get(column.getWholeColumnName()) == null) iterators.put(column.getWholeColumnName(), table.iterator());
    Iterator<LeafValue[]> rows = iterators.get(column.getWholeColumnName());
    int index;
    for (index = 0; index < schema.length; index++) {
      if (schema[index].getColumnName().equals(column.getColumnName())) {
        break;
      }
    }
    
    LeafValue next = rows.next()[index];
    return next;
//    set = true;
//    return table.iterator().next()[index];
//    col.getTable().getName();
    // TODO Auto-generated method stub
//    return null;
  }
}
