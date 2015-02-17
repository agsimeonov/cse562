package edu.buffalo.cse562;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.LinkedHashSet;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import edu.buffalo.cse562.table.Row;
import edu.buffalo.cse562.table.TableManager;
import edu.buffalo.cse562.visitor.StatementVisitorImpl;

/**
 * OperationEndgame
 * 
 * @author Alexander Simeonov
 * @author Sunny Mistry
 */
public class Main {

  /**
   * Processes the command line arguments.
   * 
   * @param args - command line arguments
   * @return a set of files to parse
   * @throws FileNotFoundException when a given file does not exist
   * @throws IOException when the given directory could not be created
   * @throws NotDirectoryException when the given path is not a valid directory
   */
  private static LinkedHashSet<File> cli(String[] args) throws FileNotFoundException,
                                                       IOException,
                                                       NotDirectoryException {
    LinkedHashSet<File> files = new LinkedHashSet<File>();

    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--data")) {
        TableManager.setDataDir(args[i + 1]);
        i++;
      } else {
        File file = new File(args[i]);
        if (file.exists()) files.add(file);
        else throw new FileNotFoundException(args[i] + " file does not exist!");
      }
    }

    return files;
  }

  /**
   * Program entry point!
   * 
   * @param args - command line arguments
   */
  public static void main(String[] args) {
    try {
      for (File file : cli(args)) {
        FileReader stream = new FileReader(file);
        CCJSqlParser parser = new CCJSqlParser(stream);
        for (Statement stmt = parser.Statement(); stmt != null; stmt = parser.Statement()) {
          stmt.accept(new StatementVisitorImpl());
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (NotDirectoryException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    
    // TESTING PORTION REMOVE LATER

    // EVALUATOR
//    Evaluate e = new Evaluate();
//    DataTable tbl = TableManager.getTable("R");
//    Addition add = new Addition(tbl.getSchema().getColumns().get(0), tbl.getSchema().getColumns().get(1));
//    for (Row row : tbl) {
//      e.setRow(row);
//      try {
//        System.out.println(e.eval(add).toLong());
//      } catch (InvalidLeaf e1) {
//        // TODO Auto-generated catch block
//        e1.printStackTrace();
//      } catch (SQLException e1) {
//        // TODO Auto-generated catch block
//        e1.printStackTrace();
//      }
//    }
    
    // MORE TESTING GENERAL TABLE ITERATOR
//    for(Object o : TableManager.getTable("R")) {
//      Row row = (Row) o;
//      System.out.println(row);
//    }
  }
}
