package edu.buffalo.cse562.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.sf.jsqlparser.statement.create.table.CreateTable;

public class DbTable {
  private final File   data;
  private final String name;

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
}
