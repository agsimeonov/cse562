package edu.buffalo.cse562.iterator;

import net.sf.jsqlparser.expression.Expression;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;

public class ProjectIterator implements RowIterator {
  private RowIterator iterator;
  private Schema schema;
  Expression[] expressions;
  
  public ProjectIterator(RowIterator iterator, Schema schema, Expression[] expressions) {
    this.iterator = iterator;
    this.schema = schema;
    this.expressions = expressions;
  }
  
  @Override
  public void close() {
    iterator.close();
  }

  @Override
  public boolean hasNext() {
    return iterator.hasNext();
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row inRow = iterator.next();
    Row outRow = new Row(schema);
    
    return outRow;
  }

  @Override
  public void open() {
    iterator.open();
  }
}
