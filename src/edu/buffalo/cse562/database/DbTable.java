package edu.buffalo.cse562.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sf.jsqlparser.statement.create.table.CreateTable;

public class DbTable {
  private File   data;
  private String name;

  //TODO Create Temporary Table for results
  public DbTable() {
    // JUST DO SOMETHING LIKE THIS PERHAPS BETTER TO CREATE AN ABSTRACT CLASS
    // WITH VIRTUAL TABLE
    // AND FileBackedTable as children
  }
  
  protected DbTable(CreateTable createTable) throws IOException {
    name = createTable.getTable().getName();
    Path path = Paths.get(Database.getDataDir(), name + ".dat");
    data = path.toFile();
    if (!data.exists()) {
      if (!data.createNewFile()) {
        throw new IOException("Could not create data file for table " + name);
      }
    }
  }
  
  public String toString() {
    return "THIS IS A TEST THAT DB TABLE " + name + " EXISTS!";
  }
}
