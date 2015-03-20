package edu.buffalo.cse562.iterator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.RowComparator;
import edu.buffalo.cse562.table.TableManager;

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
  private ArrayList<File>            buffers;
  private File                       swapDirectory;

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
    if (buffers != null) return;
    iterator.open();
    swapDirectory = new File(TableManager.getSwapDir());
    buffers = new ArrayList<File>();
    buffer = new ArrayList<Row>();
    
    // Phase 1:
    while (iterator.hasNext()) {
      buffer.add(iterator.next());
      
      // When the memory is full flush the buffer onto disk
      if (Runtime.getRuntime().freeMemory() < THRESHOLD || !iterator.hasNext()) {
        // Sort first
        buffer.sort(new RowComparator(orderByElements));
        
        // Now flush buffer onto disk
        try {
          File temporary = File.createTempFile("tmp", null, swapDirectory);
//          temporary.deleteOnExit();
          buffers.add(temporary);
          
          ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temporary));
          for (Row row : buffer)
            oos.writeObject(row);
          oos.writeObject(null);
          oos.close();
          
          buffer = new ArrayList<Row>();
          System.gc();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}
