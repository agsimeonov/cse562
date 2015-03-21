package edu.buffalo.cse562.iterator;

import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

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
   * @param outExpressions - contains projection expressions
   * @param outSchema - contains the input schema
   */
  public NonAggregateIterator(RowIterator iterator,
                              ArrayList<Expression> outExpressions,
                              Schema inSchema) {
    super(iterator, outExpressions, inSchema);
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
    Row row = new Row(outExpressions.size());
    
    for (int i = 0; i < outExpressions.size(); i++) {
      try {
        row.setValue(i, evaluate.eval(outExpressions.get(i)));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return row;
  }
}
