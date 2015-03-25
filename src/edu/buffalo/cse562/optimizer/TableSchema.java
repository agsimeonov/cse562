package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.TableNode;
import edu.buffalo.cse562.table.DataTable;
import edu.buffalo.cse562.table.Schema;
import edu.buffalo.cse562.table.TableManager;

/**
 * Optimizes the table schema, by determining and using the minimal amount of columns.
 * 
 * @author Alexander Simeonov
 */
public class TableSchema {
  /**
   * Optimizes the table schema for all table nodes in the given tree.
   * 
   * @param root - root of the given tree
   * @param columnSet - optimal set of columns
   */
  public static void setOptimalTableSchemas(ParseTree root, Set<String> columnSet) {
    List<ParseTree> tables = Optimizer.getAllTypeNodes(root, TableNode.class);
    for (ParseTree node : tables) {
      setOptimalSchema((TableNode) node, columnSet);
    }
  }
  
  /**
   * Optimizes the table schema for a given table node.
   * 
   * @param node - given table node
   * @param columnSet - optimal set of columns
   */
  public static void setOptimalSchema(TableNode node, Set<String> columnSet) {
    DataTable dataTable = TableManager.getTable(node.getTable().getWholeTableName().toLowerCase());
    Schema schema = dataTable.getSchema();
    ArrayList<Column> columns = schema.getColumns();
    HashMap<String, Integer> lookupTable = schema.getLookupTable();
    ArrayList<Column> out = new ArrayList<Column>();
    for (String column : columnSet) {
      if (lookupTable.containsKey(column)) {
        Column c = columns.get(lookupTable.get(column));
        if (!out.contains(c)) out.add(c);
      }
    }
    node.setOptimalSchema(new Schema(out));
  }
}
