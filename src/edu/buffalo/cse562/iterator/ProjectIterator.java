package edu.buffalo.cse562.iterator;

import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.evaluate.Evaluate;
import edu.buffalo.cse562.table.Schema;

/**
 * Abstract class which sets up common code for projection queries. 
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public abstract class ProjectIterator implements RowIterator {
  protected final RowIterator           iterator;
  protected final Schema                inSchema;
  protected final ArrayList<Expression> outExpressions;
  protected Evaluate                    evaluate;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param outExpressions - contains output expressions
   * @param inSchema - contains the input schema
   */
  public ProjectIterator(RowIterator iterator,
                         ArrayList<Expression> outExpressions,
                         Schema inSchema) {
    this.iterator = iterator;
    this.outExpressions = outExpressions;
    this.inSchema = inSchema;
    open();
  }

  @Override
  public void close() {
    iterator.close();
    evaluate = null;
  }
  
  @Override
  public void open() {
    if (evaluate != null) return;
    iterator.open();
    evaluate = new Evaluate(inSchema);
  }
}
