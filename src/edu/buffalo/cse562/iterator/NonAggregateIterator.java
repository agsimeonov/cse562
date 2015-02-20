package edu.buffalo.cse562.iterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import edu.buffalo.cse562.table.Row;

/**
 * Iterates over rows in a given iterator and handles non-aggregate queries. 
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class NonAggregateIterator extends ProjectIterator {
  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param items - contains columns and their aliases
   */
  public NonAggregateIterator(RowIterator iterator, List<SelectExpressionItem> items) {
    super(iterator, items);
  }

  @Override
  public boolean hasNext() {
    if (iterator.hasNext()) return true;
    close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row next = iterator.next();
    evaluate.setRow(next);
    Row row = new Row(outSchema);
    
    for (int i = 0; i < inExpressions.size(); i++) {
      Column column = outSchema.getColumns().get(i);
      
      // Handle wildcards
      if (column.getColumnName() == null) {
        ArrayList<Column> tableColumns = next.getSchema().getTableColumns(column.getTable());

        for (Column tableColumn : tableColumns) {
          inExpressions.add(i, tableColumn);
          outSchema.getColumns().add(i, tableColumn);
        }
        
        inExpressions.remove(i + tableColumns.size());
        outSchema.getColumns().remove(i + tableColumns.size());
        
        column = outSchema.getColumns().get(i);
      }
      
      try {
        row.setValue(column, evaluate.eval(inExpressions.get(i)));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return row;
  }
}
