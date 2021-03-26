package me.miltz.labelprinter;

import java.lang.StringBuilder;

public class Recipient implements Comparable<Recipient> {

  private String name;
  private String[] address;
  private int id;
  private int group;

  public String toString() {
    var builder = new StringBuilder();
    builder.append(name);
    builder.append(" ");
    for (var line : address) {
      builder.append(line);
      builder.append(" ");
    }
    builder.append(id);
    builder.append(" ");
    builder.append(group);
    return builder.toString();
  }

  public String[] getAddress() {
    return address.clone();
  }

  public String getName() {
    return name;
  }

  public Recipient(String name, String[] address, int id, int group) {
    this.name = name;
    this.address = address;
    this.id = id;
    this.group = group;
  }

  public int getId() {
    return id;
  }

  @Override
  public int compareTo(Recipient other) {
    if (this.group != other.group) {
      return Integer.compare(this.group, other.group);
    }
    if (this.id != other.id) {
      return Integer.compare(this.id, other.id);
    }
    return this.name.compareTo(other.name);
  }
}
