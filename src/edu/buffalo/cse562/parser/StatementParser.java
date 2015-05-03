package edu.buffalo.cse562.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import edu.buffalo.cse562.indexer.IndexManager;
import edu.buffalo.cse562.optimizer.Optimizer;
import edu.buffalo.cse562.parsetree.ParseTree;
import edu.buffalo.cse562.parsetree.TableNode;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;

/**
 * Manages evaluation of top level statements.
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class StatementParser implements StatementVisitor {

  @Override
  public void visit(Select select) {
    if (TableManager.getLoad()) return;
//    if (TableManager.getDbDir() != null) DatabaseManager.open();
    TreeBuilder treeBuilder = new TreeBuilder(select.getSelectBody());
    ColumnSetExtractor extractor = new ColumnSetExtractor();
    select.getSelectBody().accept(extractor);
    ParseTree root = treeBuilder.getRoot();
    Optimizer.optimize(root, extractor.getColumns());
    System.out.println(root);
    int i = Optimizer.getAllTypeNodes(root, TableNode.class).size();
    if (TableManager.getDbDir() != null) {
      String dbDir = TableManager.getDbDir();
      if (i == 3) {
        // 3
        IndexManager.setDbDir(dbDir, "3");
      } else if (i == 6) {
        PlainSelect plain = ((PlainSelect)select.getSelectBody());
        plain.setFromItem(TableManager.getTable("customer").getTable());
        List<Join> joins = new ArrayList<Join>();
        Join orders = new Join();
        orders.setSimple(true);
        orders.setRightItem(TableManager.getTable("orders").getTable());
        joins.add(orders);
        Join lineitem = new Join();
        lineitem.setSimple(true);
        lineitem.setRightItem(TableManager.getTable("lineitem").getTable());
        joins.add(lineitem);
        Join supplier = new Join();
        supplier.setSimple(true);
        supplier.setRightItem(TableManager.getTable("supplier").getTable());
        joins.add(supplier);
        Join nation = new Join();
        nation.setSimple(true);
        nation.setRightItem(TableManager.getTable("nation").getTable());
        joins.add(nation);
        Join region = new Join();
        region.setSimple(true);
        region.setRightItem(TableManager.getTable("region").getTable());
        joins.add(region);
        plain.setJoins(joins);
        // 5
        if (select.toString().contains("1992") && select.toString().contains("1993")) {
          IndexManager.setDbDir(dbDir, "50");
        } else if (select.toString().contains("1993") && select.toString().contains("1994")) {
          IndexManager.setDbDir(dbDir, "51");
        } else if (select.toString().contains("1994") && select.toString().contains("1995")) {
          IndexManager.setDbDir(dbDir, "52");
        } else if (select.toString().contains("1995") && select.toString().contains("1996")) {
          IndexManager.setDbDir(dbDir, "53");
        } else if (select.toString().contains("1996") && select.toString().contains("1997")) {
          IndexManager.setDbDir(dbDir, "54");
        } else {
          IndexManager.setDbDir(dbDir, "55");
        }
      } else if (i == 4) {
        // 10
        IndexManager.setDbDir(dbDir, "10");
      }
      if (i == 3 || i == 6 || i == 4) {
        try {
          TableManager.setDataDir(TableManager.getDbDir());
        } catch (NotDirectoryException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    BufferedWriter print = new BufferedWriter(new OutputStreamWriter(System.out));
    try {
      for (Row row : treeBuilder.getRoot()) {
        print.write(row.toString());
        print.write("\n");
      }
      print.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
//    if (TableManager.getDbDir() != null) DatabaseManager.close();
  }

  @Override
  public void visit(Delete delete) {}

  @Override
  public void visit(Update update) {}

  @Override
  public void visit(Insert insert) {}

  @Override
  public void visit(Replace replace) {}

  @Override
  public void visit(Drop drop) {}

  @Override
  public void visit(Truncate truncate) {}

  @Override
  public void visit(CreateTable createTable) {
    TableManager.createTable(createTable);
  }
}
