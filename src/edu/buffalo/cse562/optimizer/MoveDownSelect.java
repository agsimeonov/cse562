package edu.buffalo.cse562.optimizer;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.SelectNode;
import edu.buffalo.cse562.table.Schema;

/**
 * Used to perform the move down of selection optimization.
 * 
 * @author Alexander Simeonov
 */
public class MoveDownSelect {
  /**
   * Perform the move down of selection optimization on the entire tree.
   * 
   * @param root - root of the given tree
   */
  public static void moveDownAllSelectNodes(ParseTree root) {
    List<ParseTree> selectNodes = Optimizer.getAllTypeNodes(root, SelectNode.class);
    for (ParseTree selectNode : selectNodes)
      moveDown((SelectNode) selectNode);
  }
  
  /**
   * Perform the move down of selection optimization on a given selection node.
   * 
   * @param selectNode - the given selection node
   */
  public static void moveDown(SelectNode selectNode) {
    if (!(selectNode.getExpression() instanceof BinaryExpression)) return;
    List<Column> columns = getExpressionColumns((BinaryExpression) selectNode.getExpression());
    ParseTree lowestChild = getLowestChild(selectNode.getLeft(), columns);
    
    if (lowestChild == selectNode.getLeft()) return;
    Optimizer.popNode(selectNode);
    Optimizer.pushNode(selectNode, lowestChild.getBase(), lowestChild, null);
  }
  
  /**
   * Acquires the lowest possible child node where the selection code stay above.
   * 
   * @param childNode - a child node, the user should put the selection first child here
   * @param columns - expression columns within the selection
   * @return lowest possible child node where the selection code stay above
   */
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
  
  /**
   * Acquires a list of expression columns gathered from a binary expression.
   * 
   * @param in - the given binary expression
   * @return list of columns within that expression
   */
  public static List<Column> getExpressionColumns(BinaryExpression in) {
    List<Column> columns = new ArrayList<Column>();
    if (in.getLeftExpression() instanceof Column) columns.add((Column) in.getLeftExpression());
    if (in.getRightExpression() instanceof Column) columns.add((Column) in.getRightExpression());
    return columns;
  }
}
