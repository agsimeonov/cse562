package edu.buffalo.cse562.iterator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.evaluate.Aggregate;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Iterates over rows in a given iterator and handles group by aggregate queries. 
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class GroupByIterator extends ProjectIterator {
  private HashMap<Row, ArrayList<ColumnAggregatePair>> buffer;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param inExpressions - contains projection expressions
   * @param outSchema - contains the outputSchema
   */
  public GroupByIterator(RowIterator iterator,
                           ArrayList<Expression> inExpressions,
                           Schema outSchema) {
    super(iterator, inExpressions, outSchema);
    open();
  }

  @Override
  public boolean hasNext() {
    if (buffer == null) return false;
    if (buffer.isEmpty()) {
      close();
      return false;
    } else {
      return true;
    }
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    
    Row row = buffer.keySet().iterator().next();
    ArrayList<ColumnAggregatePair> pairs = buffer.get(row);
    buffer.remove(row);
    
    for (ColumnAggregatePair pair : pairs)
      row.setValue(pair.column, pair.aggregate.currentResult());
    
    return row;
  }
  
  @Override
  public void close() {
    super.close();
    buffer = null;
  }
  
  /**
   * Initializes the group by iterator, by computing the desired rows and placing them in a buffer 
   * for future calls to {@link #next()} or {@link #hasNext()}.
   */
  @Override
  public void open() {
    if (buffer != null) return;
    super.open();
    buffer = new HashMap<Row, ArrayList<ColumnAggregatePair>>();
    
    while (this.iterator.hasNext()) {
      ArrayList<ColumnAggregatePair> pairs = new ArrayList<ColumnAggregatePair>();
      Row inRow = this.iterator.next();
      Row outRow = new Row(outSchema);
      evaluate.setRow(inRow);
      
      for (int i = 0; i < inExpressions.size(); i++) {
        Expression expression = inExpressions.get(i);
        Column column = outSchema.getColumns().get(i);
        
        if (inExpressions.get(i) instanceof Column) {
          try {
            outRow.setValue(column, evaluate.eval(inExpressions.get(i)));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        } else {
          Aggregate aggregate = Aggregate.getAggregate((Function) expression, evaluate);
          aggregate.yield(inRow);
          ColumnAggregatePair pair = new ColumnAggregatePair(column, aggregate);
          pairs.add(pair);
        }
      }
      
      if (buffer.containsKey(outRow)) {
        for (ColumnAggregatePair pair : buffer.get(outRow))
          pair.aggregate.yield(inRow);
      } else {
        buffer.put(outRow, pairs);
      }
    }
  }

  /**
   * Contains a column and an aggregate associated with it.
   * 
   * @author Alexander Simeonov
   * @author Sunny Mistry
   */
  private class ColumnAggregatePair {
    private final Column    column;
    private final Aggregate aggregate;

    /**
     * Initializes the pair.
     * 
     * @param column - given column
     * @param aggregate - aggregate associate with the given column
     */
    private ColumnAggregatePair(Column column, Aggregate aggregate) {
      this.column = column;
      this.aggregate = aggregate;
    }
  }
}
