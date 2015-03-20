package edu.buffalo.cse562.iterator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.RowComparator;

/**
 * Two-Phase, Multiway Merge-Sort
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class MergeSortIterator implements RowIterator {
  private static final long          THRESHOLD = 10 << 20; // megabytes converted to bytes
  private final RowIterator          iterator;
  private final List<OrderByElement> orderByElements;
  private ArrayList<Row>             buffer;
  private static File                swapDirectory;

  public MergeSortIterator(RowIterator iterator, List<OrderByElement> orderByElements) {
    this.iterator = iterator;
    this.orderByElements = orderByElements;
    open();
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
  public void close() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void open() {
//    if (buffer != null) return;
    iterator.open();
    buffer = new ArrayList<Row>();
    while (iterator.hasNext()) {
      buffer.add(iterator.next());
      // TODO: Figure out a way to poll for free memory less
      if (Runtime.getRuntime().freeMemory() < THRESHOLD) {
        buffer.sort(new RowComparator(orderByElements));
        
      }
    }
  }

}
