package edu.buffalo.cse562.parsetree;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import com.sleepycat.je.Database;
import com.sleepycat.je.SecondaryDatabase;

import edu.buffalo.cse562.berkeley.DatabaseManager;
import edu.buffalo.cse562.berkeley.cursor.PrimaryIterator;
import edu.buffalo.cse562.berkeley.cursor.SecondaryIterator;
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
  private Schema outSchema;
  private Column secondaryColumn = null;

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
    if (TableManager.getDbDir() != null) {
      ArrayList<String> types = TableManager.getTable(table.getWholeTableName()).getTypes();
      String name = table.getWholeTableName();
      
      if (secondaryColumn != null) {
        SecondaryDatabase secondary = DatabaseManager.getSecondary(name, secondaryColumn);
        return new SecondaryIterator(secondary, types);
      }
      
      Database database = DatabaseManager.getDatabase(name);
      return new PrimaryIterator(database, types);
    } else {
      return new TableIterator(table, outSchema);
    }
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
  
  /**
   * Acquires the table associated with this node.
   * 
   * @return the table associated with this node
   */
  public Table getTable() {
    return table;
  }
  
  /**
   * Used by the optimizer to set the optimal schema.
   * 
   * @param schema - the optimal schema
   */
  public void setOptimalSchema(Schema schema) {
    if (TableManager.getDbDir() == null) outSchema = schema;
  }
  
  /**
   * Sets the secondary key column.
   * 
   * @param the secondary key column
   */
  public void setSecondary(Column secondaryColumn) {
    this.secondaryColumn = secondaryColumn;
  }
}
