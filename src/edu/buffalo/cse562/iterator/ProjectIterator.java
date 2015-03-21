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
  protected final Schema                outSchema;
  protected final ArrayList<Expression> inExpressions;
  protected Evaluate                    evaluate;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param inExpressions - contains input expressions
   * @param outSchema - the output schema
   */
  public ProjectIterator(RowIterator iterator,
                         ArrayList<Expression> inExpressions,
                         Schema outSchema) {
    this.iterator = iterator;
    this.inExpressions = inExpressions;
    this.outSchema = outSchema;
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
    evaluate = new Evaluate();
  }
}
