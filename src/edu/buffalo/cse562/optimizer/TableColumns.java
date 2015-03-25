package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.parsetree.JoinNode;
import edu.buffalo.cse562.parsetree.OrderByNode;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.ProjectNode;
import edu.buffalo.cse562.parsetree.SelectNode;
import edu.buffalo.cse562.parsetree.TableNode;
import edu.buffalo.cse562.table.TableManager;

public class TableColumns {
  public static void setAllSkipLists(ParseTree root) {
    List<ParseTree> tables = Optimizer.getAllTypeNodes(root, TableNode.class);
    for (ParseTree node : tables) {
      getSkipList(root, ((TableNode) node).getTable());
    }
  }
  
  public static Set<Integer> getSkipList(ParseTree root, Table table) {
    List<ParseTree> projections = Optimizer.getAllTypeNodes(root, ProjectNode.class);
    List<ParseTree> selections = Optimizer.getAllTypeNodes(root, SelectNode.class);
    List<ParseTree> orders = Optimizer.getAllTypeNodes(root, OrderByNode.class);
    List<ParseTree> joins = Optimizer.getAllTypeNodes(root, JoinNode.class);
    Set<Integer> indexes = new HashSet<Integer>();
    
    ArrayList<Column> columns = TableManager.getTable(table.toString()).getSchema().getColumns();
    
    for (ParseTree node : projections) {
      
//      ArrayList<Column> nodeColumns = node.getSchema().getColumns();
//      for (int i = 0; i < columns.size(); i++) {
//        Column column = columns.get(i);
//        String name = column.getWholeColumnName().toLowerCase();
//        for (Column nodeColumn : nodeColumns) {
//          String nodeColumnName = nodeColumn.getWholeColumnName().toLowerCase();
//          if (nodeColumnName.matches(".*[(].*[)]$")) {
//            nodeColumnName = nodeColumnName.replaceFirst(".*[(]", "");
//            nodeColumnName = nodeColumnName.replaceFirst("[)]", "");
//          }
////          if (name.matches(".*" + nodeColumnName + "$"))
//          if (name.contains(nodeColumnName)) indexes.add(i);
//        }
//      }
    }
    
    for (ParseTree node : selections) {
      SelectNode select = (SelectNode) node;
      System.out.println(select.getExpression());
    }
    
    System.out.println(columns);
    System.out.println(indexes);
    
    return indexes;
  }
}
