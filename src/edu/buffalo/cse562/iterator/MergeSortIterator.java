package edu.buffalo.cse562.iterator;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import net.sf.jsqlparser.statement.select.OrderByElement;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.RowComparator;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

/**
 * Two-Phase, Multiway Merge-Sort. Handles ordering operations over rows in a child iterator.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class MergeSortIterator implements RowIterator {
  private static final long          THRESHOLD     = 35 << 20; // minimum available memory
  private static final int           STREAM_BUFFER = 5 << 20; // output stream buffer
  private final RowIterator          iterator;
  private final List<OrderByElement> orderByElements;
  private final Schema               inSchema;
  private PriorityQueue<Row>         outputBuffer;

  /**
   * Initializes the iterator.
   * 
   * @param iterator - child iterator
   * @param orderByElements - order conditions
   * @param inSchema - the  input schema
   */
  public MergeSortIterator(RowIterator iterator,
                           List<OrderByElement> orderByElements,
                           Schema inSchema) {
    this.iterator = iterator;
    this.orderByElements = orderByElements;
    this.inSchema = inSchema;
    open();
  }

  @Override
  public boolean hasNext() {
    if (outputBuffer == null) return false;
    if (outputBuffer.size() == 0) return false;
    return true;
  }

  @Override
  public Row next() {
    if (!this.hasNext()) return null;
    Row out = outputBuffer.remove();
    ObjectInputStream ois = out.getStream();
    out.setStream(null);
    
    try {
      Row increment = (Row) ois.readObject();
      if (increment != null) {
        increment.setStream(ois);
        outputBuffer.add(increment);
      } else {
        ois.close();
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    if (getAvailableMemory() < THRESHOLD) System.gc();
    
    return out;
  }

  @Override
  public void close() {
    if (outputBuffer == null) return;
    iterator.close();
    outputBuffer = null;
  }

  @Override
  public void open() {
    if (outputBuffer != null) return;
    iterator.open();
    ArrayList<ObjectInputStream> buffers = new ArrayList<ObjectInputStream>();
    File swapDirectory = new File(TableManager.getSwapDir());
    RowComparator comparator = new RowComparator(orderByElements, inSchema);
    outputBuffer = new PriorityQueue<Row>(comparator);
    ArrayList<Row> buffer = new ArrayList<Row>();
    
    // Phase 1:
    while (iterator.hasNext()) {
      buffer.add(iterator.next());
      
      // When the memory is full flush the buffer onto disk
      if (getAvailableMemory() < THRESHOLD || !iterator.hasNext()) {
        // Sort first
        buffer.sort(comparator);
        
        // Now flush buffer onto disk
        try {
          // Create file buffer
          File temporary = File.createTempFile("tmp", null, swapDirectory);
          temporary.deleteOnExit();
          
          // Flush all rows to disk
          FileOutputStream fos = new FileOutputStream(temporary);
          BufferedOutputStream bos = new BufferedOutputStream(fos, STREAM_BUFFER);
          ObjectOutputStream oos = new ObjectOutputStream(bos);
          for (Row row : buffer)
            oos.writeObject(row);
          oos.writeObject(null);
          oos.close();
          
          // Reset the memory buffer and invoke Java's garbage collector
          buffer.clear();
          System.gc();
          
          // Stash the file buffer
          buffers.add(new ObjectInputStream(new FileInputStream(temporary)));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    
    // Initialize Phase 2:
    for (ObjectInputStream ois : buffers) {
      try {
        Object object = ois.readObject();
        if (object == null) {
          ois.close();
          continue;
        }
        Row row = (Row) object;
        row.setStream(ois);
        outputBuffer.add(row);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Acquires the available memory.
   * 
   * @return - the available memory in bytes
   */
  private long getAvailableMemory() {
    Runtime runtime = Runtime.getRuntime();
    return runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
  }
}
