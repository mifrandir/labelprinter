package me.miltz.labelprinter;

import java.lang.StringBuilder;

public class Recipient {

  private String name;
  private String[] address;

  public String toString() {
    var builder = new StringBuilder();
    builder.append(name);
    builder.append("%n");
    for (var line : address) {
      builder.append(line);
      builder.append("%n");
    }
    return builder.toString();
  }

  public String[] getAddress() {
    return address.clone();
  }

  public String getName() {
    return name;
  }

  public Recipient(String name, String[] address) {
    this.name = name;
    this.address = address;
  }
}
