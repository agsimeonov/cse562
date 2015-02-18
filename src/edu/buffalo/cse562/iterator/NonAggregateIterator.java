package edu.buffalo.cse562.iterator;

import java.sql.SQLException;
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
    if (iterator.hasNext()) {
      return true;
    } else {
      close();
      return false;
    }
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    evaluate.setRow(iterator.next());
    Row row = new Row(schema);
    
    for (int i = 0; i < expressions.size(); i++) {
      Column column = schema.getColumns().get(i);
      try {
        row.setValue(column, evaluate.eval(expressions.get(i)));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return row;
  }
}
