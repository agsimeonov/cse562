package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.table.Row;

public interface Callback {
  public boolean decide(Row row);
}
