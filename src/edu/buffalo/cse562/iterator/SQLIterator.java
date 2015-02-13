package edu.buffalo.cse562.iterator;

import java.util.Iterator;

import net.sf.jsqlparser.expression.LeafValue;

public interface SQLIterator extends Iterator<LeafValue[]> {
  public void close();
  
  public void open();
}
