package edu.buffalo.cse562.iterator;

import java.util.List;

import edu.buffalo.cse562.table.Row;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class AggregateIterator extends ProjectIterator {
  private boolean ready = true;
  
  public AggregateIterator(RowIterator iterator, List<SelectExpressionItem> items) {
    super(iterator, items);
    open();
  }

  @Override
  public boolean hasNext() {
    if (iterator.hasNext() && ready) {
      ready = false;
      return true;
    }
    close();
    return false;
  }

  @Override
  public Row next() {
    return  null;
  }
  
  @Override
  public void close() {
    super.close();
    ready = false;
  }

  @Override
  public void open() {
    super.open();
    ready = true;
  }
}
