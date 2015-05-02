package edu.buffalo.cse562.indexer;

import edu.buffalo.cse562.table.Row;

public interface Callback {
  /**
   * Decide whether to filter or not.
   * 
   * @param row - row to decide on
   * @return filters on true, otherwise doesn't
   */
  public boolean decide(Row row);
}
