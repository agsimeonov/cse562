package edu.buffalo.cse562.parser;

import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.parsetree.CartesianNode;
import edu.buffalo.cse562.parsetree.DistinctNode;
import edu.buffalo.cse562.parsetree.JoinNode;
import edu.buffalo.cse562.parsetree.LimitNode;
import edu.buffalo.cse562.parsetree.OrderByNode;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.ProjectNode;
import edu.buffalo.cse562.parsetree.SelectionNode;
import edu.buffalo.cse562.parsetree.TableNode;
import edu.buffalo.cse562.parsetree.UnionNode;

/**
 * Parses select queries and build a parse tree.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class TreeBuilder implements SelectVisitor {
  private ParseTree root;
  
  /**
   * Builds a parse tree based on a given select statement.
   * 
   * @param plainSelect - select statement used to build the tree
   */
  public TreeBuilder(SelectBody selectBody) {
    selectBody.accept(this);
  }
  
  /**
   * Builds a select statement parse tree and sets it as the current root.
   * 
   * @param plainSelect - select statement to process
   */
  @Override
  public void visit(PlainSelect plainSelect) {
    ParseTree current;
    
    // Build a from items tree
    ArrayList<FromItem> fromItems = getFromItems(plainSelect);
    current = getFromItemsTree(fromItems);
    
    // Build inner join tree if necessary
    @SuppressWarnings("unchecked")
    Expression onExpression = getOnExpression(plainSelect.getJoins());
    if (onExpression != null) {
      ParseTree joinTree = new JoinNode(current, onExpression);
      current.setBase(joinTree);
      current = joinTree;
    }
    
    // Build project tree
    @SuppressWarnings("unchecked")
    ParseTree projectTree = new ProjectNode(null, plainSelect.getSelectItems());
    projectTree.setLeft(current);
    current.setBase(projectTree);
    current = projectTree;
    
    // Build and insert a select tree for the where clause if necessary
    if (plainSelect.getWhere() != null) {
      ParseTree selectTree = new SelectionNode(projectTree, plainSelect.getWhere());
      selectTree.setLeft(projectTree.getLeft());
      projectTree.setLeft(selectTree);
    }
    
    // Build a selection tree for the having clause if necessary
    if (plainSelect.getHaving() != null) {
      ParseTree selectTree = new SelectionNode(null, plainSelect.getHaving());
      selectTree.setLeft(current);
      current.setBase(selectTree);
      current = selectTree;
    }
    
    // Handle distinct select option
    if (plainSelect.getDistinct() != null) {
      ParseTree distinctTree = new DistinctNode(null);
      distinctTree.setLeft(current);
      current.setBase(distinctTree);
      current = distinctTree;
    }
    
    // Handle order by select option
    if (plainSelect.getOrderByElements() != null) {
      @SuppressWarnings("unchecked")
      List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
      ParseTree orderByTree = new OrderByNode(null, orderByElements);
      orderByTree.setLeft(current.getLeft());
      current.setBase(orderByTree);
      current = orderByTree;
    }
    
    // Handle limit select option
    if (plainSelect.getLimit() != null) {
      ParseTree limitTree = new LimitNode(null, plainSelect.getLimit());
      limitTree.setLeft(current);
      current.setBase(limitTree);
      current = limitTree; 
    }
    
    root = current;
  }

  /**
   * Builds a union parse tree and sets it as the current root.
   * 
   * @param union - union to process
   */
  @Override
  public void visit(Union union) {
    // Collect all the roots
    ArrayList<ParseTree> roots = new ArrayList<ParseTree>();
    
    for (Object o : union.getPlainSelects()) {
      PlainSelect select = (PlainSelect) o;
      TreeBuilder treeBuilder = new TreeBuilder(select);
      roots.add(treeBuilder.getRoot());
    }
    
    // Build the union tree from the collected roots
    root = new UnionNode(null);
    ParseTree current = root;
    
    for (int i = 0; i < roots.size(); i++) {
      if (i + 2 == roots.size()) {
        current.setLeft(roots.get(i));
        current.setRight(roots.get(i + 1));
        break;
      } else {
        current.setLeft(roots.get(i));
        ParseTree right = new UnionNode(current);
        current.setRight(right);
        current = right;
      }
    }
  }
  
  /**
   * Acquires the built parse tree.
   * 
   * @return the root of the parse tree
   */
  public ParseTree getRoot() {
    return root;
  }
  
  /**
   * Acquires the join on expression statement.
   * 
   * @param joins - list of joins
   * @return join on expression statement, null if none found
   */
  public Expression getOnExpression(List<Join> joins) {
    if (joins == null) return null;
    for (Join join : joins)
      if (join.getOnExpression() != null) return join.getOnExpression();
    return null;
  }
  
  /**
   * Acquires the From Items in a select statement.
   * 
   * @param plainSelect - the select statement from which to acquire from items
   * @return a list of from items
   */
  public ArrayList<FromItem> getFromItems(PlainSelect plainSelect) {
    ArrayList<FromItem> fromItems = new ArrayList<FromItem>();
    fromItems.add(plainSelect.getFromItem());

    if (plainSelect.getJoins() != null) {
      for (Object o : plainSelect.getJoins()) {
        Join join = (Join) o;
        fromItems.add(join.getRightItem());
      }
    }
    
    return fromItems;
  }
  
  /**
   * Builds the from items tree, given an ordered list of from items.
   * 
   * @param fromItems - an ordered list of from items
   * @return parse tree for the given list of from items
   */
  public ParseTree getFromItemsTree(ArrayList<FromItem> fromItems) {
    ParseTree fromItemsRoot = null;
    ParseTree current = null;

    for (int i = 0; i < fromItems.size(); i++) {
      if (current == null) {
        if (i + 1 == fromItems.size()) {
          current = getFromItemTree(fromItems.get(i));
        } else {
          current = new CartesianNode(null);
          current.setLeft(getFromItemTree(fromItems.get(i)));
        }
        fromItemsRoot = current;
      } else {
        if (i + 1 == fromItems.size()) {
          current.setRight(getFromItemTree(fromItems.get(i)));
        } else {
          ParseTree next = new CartesianNode(current);
          current.setRight(next);
          current = next;
          current.setLeft(getFromItemTree(fromItems.get(i)));
        }
      }
    }
    
    return fromItemsRoot;
  }
  
  /**
   * Acquires the parse tree for a single from item, which can be a table, sub-select, or sub-join.
   * 
   * @param fromItem - a table, sub-select, or sub-join
   * @return parse tree for the given item
   */
  public ParseTree getFromItemTree(FromItem fromItem) {
    if (fromItem instanceof Table) {
      Table table = (Table) fromItem;
      return new TableNode(null, table);
    } else if (fromItem instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) fromItem;
      TreeBuilder treeBuilder = new TreeBuilder(subSelect.getSelectBody());
      return treeBuilder.getRoot();
    } else {
      SubJoin subJoin = (SubJoin) fromItem;
      return getFromItemTree(subJoin.getJoin().getRightItem());
    }
  }
}
