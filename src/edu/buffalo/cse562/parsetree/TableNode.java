package edu.buffalo.cse562.parsetree;

import java.util.Iterator;

import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.iterator.TableIterator;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

/**
 * A table node representing a data table that needs to be read from.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class TableNode extends ParseTree {
  private final Table  table;
  private final Schema outSchema;

  /**
   * Initializes the table node.
   * 
   * @param base - the parent node
   * @param table - table to read from
   */
  public TableNode(ParseTree base, Table table) {
    super(base);
    this.table = table;
    DataTable dataTable = TableManager.getTable(table.getName());
    outSchema = dataTable.getSchema();
    if (table.getAlias() != null) {
      for (int i = 0; i < outSchema.size(); i++)
        outSchema.getColumns().get(i).getTable().setName(table.getAlias());
    }
  }

  @Override
  public Iterator<Row> iterator() {
    return new TableIterator(table);
  }

  @Override
  public Schema getSchema() {
    return outSchema;
  }

  @Override
  public String nodeString() {
    String alias = table.getAlias() == null ? "" : " AS " + table.getAlias();
    return table.getWholeTableName().toUpperCase() + alias;
  }
}
