package edu.buffalo.cse562.parsetree;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;

import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.Eval;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

public class Evaluate extends Eval {
  HashMap<String, Iterator<Row>> iterators = new HashMap<String, Iterator<Row>>();

  @Override
  public LeafValue eval(Column column) throws SQLException {
    DataTable table = TableManager.getTable(column.getTable().getName());
    Schema schema = table.getSchema();
    
    if (iterators.get(column.getWholeColumnName()) == null) iterators.put(column.getWholeColumnName(), table.iterator());
    Iterator<Row> rows = iterators.get(column.getWholeColumnName());
    int index;
    for (index = 0; index < schema.numColumns(); index++) {
      if (schema.getColumns().get(index).getColumnName().equals(column.getColumnName())) {
        break;
      }
    }
    
    LeafValue next = rows.next().getValue(column);
    return next;
//    set = true;
//    return table.iterator().next()[index];
//    col.getTable().getName();
    // TODO Auto-generated method stub
//    return null;
  }
}
