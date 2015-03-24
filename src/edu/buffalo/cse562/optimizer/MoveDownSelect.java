package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;
import edu.buffalo.cse562.table.Schema;

public class MoveDownSelect {
  public static void moveDownAllSelectNodes(ParseTree root) {
    List<ParseTree> selectNodes = Optimizer.getAllTypeNodes(root, SelectNode.class);
    for (ParseTree selectNode : selectNodes)
      moveDown((SelectNode) selectNode);
  }
  
  public static void moveDown(SelectNode selectNode) {
    if (!(selectNode.getExpression() instanceof BinaryExpression)) return;
    List<Column> columns = getExpressionColumns((BinaryExpression) selectNode.getExpression());
    ParseTree lowestChild = getLowestChild(selectNode.getLeft(), columns);
    
    if (lowestChild == selectNode.getLeft()) return;
    Optimizer.popNode(selectNode);
    Optimizer.pushNode(selectNode, true, lowestChild.getBase(), lowestChild);
  }
  
  public static ParseTree getLowestChild(ParseTree childNode, List<Column> columns) {
    ParseTree lowest = childNode;
    ParseTree[] children = {childNode.getLeft(), childNode.getRight()};
    
    for (ParseTree child : children) {
      if (child != null) {
        Schema schema = child.getSchema();
        boolean containsAll = true;
        
        for (Column column : columns)
          if (schema.getIndex(column) == null) containsAll = false;
        
        if (containsAll && child.getDepth() > lowest.getDepth()) {
          lowest = getLowestChild(child, columns);
        }
      }
    }
    
    return lowest;
  }
  
  public static List<Column> getExpressionColumns(BinaryExpression in) {
    List<Column> columns = new ArrayList<Column>();
    if (in.getLeftExpression() instanceof Column) columns.add((Column) in.getLeftExpression());
    if (in.getRightExpression() instanceof Column) columns.add((Column) in.getRightExpression());
    return columns;
  }
}
