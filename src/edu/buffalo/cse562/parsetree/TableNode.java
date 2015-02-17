package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;

/**
 * A table node representing a data table that needs to be read from.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class TableNode extends ParseTree {
  private DataTable dataTable;

  /**
   * Initializes the table node.
   * 
   * @param base - the parent node
   * @param dataTable - data table to read from
   */
  public TableNode(ParseTree base, DataTable dataTable) {
    super(base);
    this.dataTable = dataTable;
  }

  @Override
  public Iterator<Row> iterator() {
    return dataTable.iterator();
  }
}
