package edu.buffalo.cse562.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.RowComparator;

public class OrderByIterator implements RowIterator {
  private final RowIterator iterator;
  private final List<OrderByElement> orderByElements;
  private ArrayList<Row> buffer;
  private Iterator<Row> bufferIterator;
  
  public OrderByIterator(RowIterator iterator, List<OrderByElement> orderByElements) {
    this.iterator = iterator;
    this.orderByElements = orderByElements;
    open();
  }
  
  @Override
  public boolean hasNext() {
    if (buffer == null) return false;
    if (bufferIterator == null) bufferIterator = buffer.iterator();
    if (!bufferIterator.hasNext()) {
      close();
      return false;
    }
    return true;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    if (bufferIterator == null) bufferIterator = buffer.iterator();
    return bufferIterator.next();
  }

  @Override
  public void close() {
    if (buffer == null) return;
    iterator.close();
    buffer = null;
    bufferIterator = null;
  }

  @Override
  public void open() {
    if (buffer != null) return;
    iterator.open();
    buffer = new ArrayList<Row>();
    while (iterator.hasNext())
      buffer.add(iterator.next());
    buffer.sort(new RowComparator(orderByElements));
  }
}
