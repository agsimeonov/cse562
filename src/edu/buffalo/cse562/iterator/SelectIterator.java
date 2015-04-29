package edu.buffalo.cse562.iterator;

import java.sql.SQLException;

import net.sf.jsqlparser.expression.BooleanValue;
import net.sf.jsqlparser.expression.Expression;

import com.sleepycat.je.DatabaseEntry;

import edu.buffalo.cse562.evaluate.Evaluate;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

/**
 * Handles selection operation over rows given a where or having expression.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class SelectIterator implements RowIterator {
  private final RowIterator iterator;
  private final Expression  expression;
  private final Evaluate    evaluate;
  private Row               row;
  private boolean           isOpen = false;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param expressions - where or having expression
   * @param inSchema - the input schema
   */
  public SelectIterator(RowIterator iterator, Expression expressions, Schema inSchema) {
    this.iterator = iterator;
    this.expression = expressions;
    evaluate = new Evaluate(inSchema);
    open();
  }

  @Override
  public boolean hasNext() {
    if (row != null) return true;

    while (iterator.hasNext()) {
      Row next = iterator.next();
      evaluate.setRow(next);

      try {
        if (((BooleanValue) evaluate.eval(expression)).getValue()) {
          row = next;
          return true;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    close();
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row out = row;
    row = null;
    return out;
  }

  @Override
  public void close() {
    if (!isOpen) return;
    iterator.close();
    isOpen = false;
  }

  @Override
  public void open() {
    if (isOpen) return;
    iterator.open();
    isOpen = true;
  }
  
  @Override
  public void setKey(DatabaseEntry key) {
    iterator.setKey(key);
  }
}
