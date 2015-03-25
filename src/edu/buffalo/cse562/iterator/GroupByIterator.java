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
  private HashMap<Row, ArrayList<IndexAggregatePair>> buffer;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param outExpressions - contains projection expressions
   * @param inSchema - contains the input schema
   */
  public GroupByIterator(RowIterator iterator,
                         ArrayList<Expression> outExpressions,
                         Schema inSchema) {
    super(iterator, outExpressions, inSchema);
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
    ArrayList<IndexAggregatePair> pairs = buffer.get(row);
    buffer.remove(row);
    
    for (IndexAggregatePair pair : pairs)
      row.setValue(pair.index, pair.aggregate.currentResult());
    
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
    buffer = new HashMap<Row, ArrayList<IndexAggregatePair>>();
    
    while (this.iterator.hasNext()) {
      Row inRow = this.iterator.next();
      Row outRow = new Row(outExpressions.size());
      ArrayList<Integer> indexes = new ArrayList<Integer>();
      evaluate.setRow(inRow);
      
      for (int i = 0; i < outExpressions.size(); i++) {
        if (outExpressions.get(i) instanceof Column) {
          try {
            outRow.setValue(i, evaluate.eval(outExpressions.get(i)));
          } catch (SQLException e) {
            e.printStackTrace();
          }
        } else {
          indexes.add(i);
        }
      }
      
      if (buffer.containsKey(outRow)) {
        for (IndexAggregatePair pair : buffer.get(outRow))
          pair.aggregate.yield(inRow);
        continue;
      }

      ArrayList<IndexAggregatePair> pairs = new ArrayList<IndexAggregatePair>();
      
      for (Integer i : indexes) {
        Aggregate aggregate = Aggregate.getAggregate((Function) outExpressions.get(i), evaluate);
        aggregate.yield(inRow);
        IndexAggregatePair pair = new IndexAggregatePair(i, aggregate);
        pairs.add(pair);
      }
      
      buffer.put(outRow, pairs);
    }
  }

  /**
   * Contains an index and an aggregate associated with it.
   * 
   * @author Alexander Simeonov
   * @author Sunny Mistry
   */
  private class IndexAggregatePair {
    private final int       index;
    private final Aggregate aggregate;

    /**
     * Initializes the pair.
     * 
     * @param index - given index
     * @param aggregate - aggregate associate with the given column
     */
    private IndexAggregatePair(int index, Aggregate aggregate) {
      this.index = index;
      this.aggregate = aggregate;
    }
  }
}
