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

/**
 * Evaluates any expression in the context of a tuple.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Evaluate extends Eval {
  private HashMap<String, Iterator<Row>> tableIterators = new HashMap<String, Iterator<Row>>();
  
  /**
   * Generator Function.  Takes a given column and produces the next value for that column.
   * 
   * @param column - a given column
   * @return the next value for a given column, null if there are no more values
   */
  @Override
  public LeafValue eval(Column column) throws SQLException {
    DataTable table = TableManager.getTable(column.getTable().getName());
    Schema schema = table.getSchema();
    String columnName = column.getWholeColumnName().toLowerCase();
    
    if (tableIterators.get(columnName) == null) tableIterators.put(columnName, table.iterator());
    Iterator<Row> rows = tableIterators.get(columnName);
    
    for (int i = 0; i < schema.size(); i++) {
      String schemaColumnName = schema.getColumns().get(i).getWholeColumnName().toLowerCase();
      if (schemaColumnName.equals(columnName)) break;
    }
    
    if (!rows.hasNext()) return null;
    LeafValue next = rows.next().getValue(column);
    return next;
  }
}
