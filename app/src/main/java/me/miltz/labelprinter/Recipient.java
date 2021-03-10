package me.miltz.labelprinter;

import java.lang.StringBuilder;

public class Recipient implements Comparable<Recipient> {

  private String name;
  private String[] address;
  private int id;

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

  public Recipient(String name, String[] address, int id) {
    this.name = name;
    this.address = address;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  @Override
  public int compareTo(Recipient other) {
    return this.name.compareTo(other.name);
  }
}
