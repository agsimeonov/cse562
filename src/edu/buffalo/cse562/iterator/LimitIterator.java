package edu.buffalo.cse562.iterator;

import net.sf.jsqlparser.statement.select.Limit;
import edu.buffalo.cse562.table.Row;

public class LimitIterator implements RowIterator {

  private RowIterator leftIterator; 
  private long rowCount; 
  private Row row; 
  private Limit limit;
  private int count; 
  public LimitIterator(RowIterator iterator, Limit limit) {
    this.leftIterator = iterator; 
    this.limit = limit; 
    rowCount = limit.getRowCount(); 
    count = 0;
  }

  @Override
  public boolean hasNext() {
    if (row != null) return true; 
    while (leftIterator.hasNext()) { 
      Row nextRow = leftIterator.next();
      if (count < rowCount) {
        count++;
        row = nextRow; 
        return true; 
      }
    }
    return false;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null; 
    Row outRow = row; 
    row = null;
    return outRow;
  }

  @Override
  public void close() {
    leftIterator.close();
  }

  @Override
  public void open() {
    leftIterator.open();
  }
}
