package edu.buffalo.cse562.iterator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.table.Row;

public class ProjectIterator implements SQLIterator {
  SQLIterator source;
  Column[] inputSchema;
  Expression[] outputExpression;
  
  public ProjectIterator(SQLIterator source) {
    
  }
  
  @Override
  public void close() {
    source.close();
  }

  @Override
  public boolean hasNext() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Row next() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void open() {
    source.open();
  }
}
