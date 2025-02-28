package me.miltz.labelprinter;

import java.util.ArrayList;

public class Address {
  private final String street;
  private final String city;
  private final String area;

  public Address(final String[] raw_address) {
    this.street = raw_address[0];
    var city_parts = raw_address[1].split("OT");
    if (city_parts.length > 2) {
      throw new IllegalArgumentException("Invalid city address " + raw_address[2]);
    } else if (city_parts.length == 2) {
      this.city = city_parts[0].strip();
      this.area = city_parts[1].strip();
    } else {
      this.area = null;
      this.city = city_parts[0].strip();
    }
  }

  public String toString() {
    return "[" + street + " | " + city + " OT " + area + "]";
  }

  public ArrayList<String> toLines() {
    var lines = new ArrayList<String>();
    if (area != null) {
      lines.add("OT " + area);
    }
    lines.add(street);
    lines.add(city);
    return lines;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getArea() {
    return area;
  }
}
