package edu.buffalo.cse562.iterator;

import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LeafValue;
import edu.buffalo.cse562.evaluate.Aggregate;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Iterates over rows in a given iterator and handles aggregate queries.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class AggregateIterator extends ProjectIterator {
  private ArrayList<Aggregate> aggregators;
  private boolean              ready;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param outExpressions - contains projection expressions
   * @param inSchema - contains the input schema
   */
  public AggregateIterator(RowIterator iterator,
                           ArrayList<Expression> outExpressions,
                           Schema inSchema) {
    super(iterator, outExpressions, inSchema);
    open();
  }

  @Override
  public boolean hasNext() {
    if (iterator.hasNext() && ready) return true;
    close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    LeafValue[] results = new LeafValue[outExpressions.size()];
    
    for(Expression e : outExpressions) {
      Function function = (Function) e;
      aggregators.add(Aggregate.getAggregate(function, evaluate));
    }
    
    while (iterator.hasNext()) {
      Row next = iterator.next();
      for (int i = 0; i < outExpressions.size(); i++) {
        results[i] = aggregators.get(i).yield(next);
      }
    }
    
    Row out = new Row(results.length);
    
    for (int i = 0; i < results.length; i++)
      out.setValue(i, results[i]);
    
    ready = false;
    return out;
  }
  
  @Override
  public void close() {
    super.close();
    aggregators = null;
    ready = false;
  }

  @Override
  public void open() {
    if (!ready) {
      super.open();
      aggregators = new ArrayList<Aggregate>();
      ready = true;
    }
  }
}
