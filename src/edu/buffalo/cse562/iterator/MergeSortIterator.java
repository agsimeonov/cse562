package edu.buffalo.cse562.iterator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

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
  private static final long                           THRESHOLD = 10 << 20;
  private final RowIterator                           iterator;
  private final List<OrderByElement>                  orderByElements;
  private TreeMap<Row, LinkedList<ObjectInputStream>> outputBuffer;

  public MergeSortIterator(RowIterator iterator, List<OrderByElement> orderByElements) {
    this.iterator = iterator;
    this.orderByElements = orderByElements;
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
    Row out = outputBuffer.firstKey();
    LinkedList<ObjectInputStream> list = outputBuffer.get(out);
    ObjectInputStream ois = list.pop();
    if (list.isEmpty()) outputBuffer.pollFirstEntry();
    
    try {
      Row increment = (Row) ois.readObject();
      if (increment != null) {
        list = outputBuffer.get(increment);
        if (list == null) {
          list = new LinkedList<ObjectInputStream>();
          outputBuffer.put(increment, list);
        }
        list.push(ois);
      } else {
        System.gc();
        ois.close();
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
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
    RowComparator comparator = new RowComparator(orderByElements);
    outputBuffer = new TreeMap<Row, LinkedList<ObjectInputStream>>(comparator);
    ArrayList<Row> buffer = new ArrayList<Row>();
    
    // Phase 1:
    while (iterator.hasNext()) {
      buffer.add(iterator.next());
      
      // When the memory is full flush the buffer onto disk
      Runtime runtime = Runtime.getRuntime();
      long memory = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
      if (memory < THRESHOLD || !iterator.hasNext()) {
        // Sort first
        buffer.sort(comparator);
        
        // Now flush buffer onto disk
        try {
          // Create file buffer
          File temporary = File.createTempFile("tmp", null, swapDirectory);
          temporary.deleteOnExit();
          
          // Flush all rows to disk
          ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(temporary));
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
        LinkedList<ObjectInputStream> list = outputBuffer.get(row);
        if (list == null) {
          list = new LinkedList<ObjectInputStream>();
          outputBuffer.put(row, list);
        }
        list.push(ois);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
