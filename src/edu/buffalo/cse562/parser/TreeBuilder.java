package edu.buffalo.cse562.parser;

import java.util.ArrayList;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.parsetree.CartesianNode;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.ProjectNode;
import edu.buffalo.cse562.parsetree.SelectionNode;
import edu.buffalo.cse562.parsetree.TableNode;
import edu.buffalo.cse562.parsetree.UnionNode;
import edu.buffalo.cse562.table.TableManager;

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
  
  @Override
  public void visit(PlainSelect plainSelect) {
    // Build a from items tree
    ArrayList<FromItem> fromItems = getFromItems(plainSelect);
    ParseTree fromItemsTree = getFromItemsTree(fromItems);
    
    // Build project tree
    @SuppressWarnings("unchecked")
    ParseTree projectTree = new ProjectNode(null, plainSelect.getSelectItems());
    projectTree.setLeft(fromItemsTree);
    
    // Build and insert a select tree for the where clause
    if (plainSelect.getWhere() != null) {
      ParseTree selectTree = new SelectionNode(projectTree, plainSelect.getWhere());
      selectTree.setLeft(projectTree.getLeft());
      projectTree.setLeft(selectTree);
    }
    
    root = projectTree;
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
      return new TableNode(null, TableManager.getTable(table.getName()));
    } else if (fromItem instanceof SubSelect) {
      SubSelect subSelect = (SubSelect) fromItem;
      TreeBuilder treeBuilder = new TreeBuilder(subSelect.getSelectBody());
      return treeBuilder.getRoot();
    } else {
      SubJoin subJoin = (SubJoin) fromItem;
      return getFromItemTree(subJoin.getJoin().getRightItem());
    }
  }
  
  public ParseTree getProjectTree() {
    return null;
  }
}
