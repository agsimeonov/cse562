package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.Row;

/**
 * A table node representing a data table that needs to be read from.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class TableNode extends ParseTree {
  private final Table table;

  /**
   * Initializes the table node.
   * 
   * @param base - the parent node
   * @param table - table to read from
   */
  public TableNode(ParseTree base, Table table) {
    super(base);
    this.table = table;
  }

  @Override
  public Iterator<Row> iterator() {
    return new TableIterator(table);
  }
}
