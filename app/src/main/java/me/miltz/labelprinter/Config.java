package me.miltz.labelprinter;

import java.awt.Color;

public class Config {

  // Standard scaling from mm to PDF units
  private static float defaultScaling = 72.0f / 25.4f;

  // Page layout and style
  private float pageWidth;
  private float pageHeight;
  private float[] pagePadding;
  private float cellWidth;
  private float cellHeight;
  private float[] cellPadding;
  private float lineHeight;
  private float fontHeight;
  private Color fontColor;
  private int numRows;
  private int numCols;

  // Input parsing
  private String[] namePattern;
  private String[][] addressPattern;
  private String idPattern;
  private String groupPattern;

  public Config(Config other) {
    pageWidth = other.pageWidth;
    pageHeight = other.pageHeight;
    pagePadding = other.pagePadding.clone();
    cellWidth = other.cellWidth;
    cellHeight = other.cellHeight;
    cellPadding = other.cellPadding.clone();
    lineHeight = other.lineHeight;
    fontHeight = other.fontHeight;
    fontColor = other.fontColor;
    numRows = other.numRows;
    numCols = other.numCols;
    namePattern = other.namePattern.clone();
    addressPattern = new String[other.addressPattern.length][];
    for (int i = 0; i < addressPattern.length; i++) {
      addressPattern[i] = other.addressPattern[i].clone();
    }
    idPattern = other.idPattern;
    groupPattern = other.groupPattern;
  }

  public String getIdPattern() {
    return idPattern;
  }

  public void setIdPattern(String idPattern) {
    this.idPattern = idPattern;
  }

  public String getGroupPattern() {
    return groupPattern;
  }

  public void setGroupPattern(String groupPattern) {
    this.groupPattern = groupPattern;
  }

  public static Config defaultConfig() {
    var config = new Config();
    // Keeping actual A4 page size
    config.setPageWidth(210f * defaultScaling);
    config.setPageHeight(297f * defaultScaling);
    config.setCellWidth(fromMm(70));
    config.setCellHeight(fromMm(36));
    config.setCellPadding(
      new float[] { fromMm(8), fromMm(8), fromMm(8), fromMm(8) }
    );
    config.setFontHeight(fromMm(4));
    config.setLineHeight(fromMm(5));
    config.setFontColor(Color.BLACK);
    config.setNamePattern(new String[] { "Vorname", "Nachname" });
    config.setAddressPattern(
      new String[][] { { "Anschrift" }, { "PLZ", "Wohnort" } }
    );
    config.setIdPattern("Parzelle");
    config.setGroupPattern("Komplex");
    config.numCols = 3;
    config.numRows = 8;
    config.setPagePadding(
      new float[] {
        (config.pageHeight - config.numRows * config.cellHeight) / 2, // top
        (config.pageWidth - config.numCols * config.cellWidth) / 2, // right
        (config.pageHeight - config.numRows * config.cellHeight) / 2, // bottom
        (config.pageWidth - config.numCols * config.cellWidth) / 2, // left
      }
    );
    return config;
  }

  public static float fromMm(float x) {
    // Custom scaling for specific printer (some settings are wrong, I know...)
    return 100f / 34.4f * x;
  }

  public String[][] getAddressPattern() {
    return addressPattern;
  }

  public void setAddressPattern(String[][] addressPattern) {
    this.addressPattern = addressPattern;
  }

  public String[] getNamePattern() {
    return namePattern;
  }

  public void setNamePattern(String[] namePattern) {
    this.namePattern = namePattern;
  }

  public int getNumCols() {
    return numCols;
  }

  public void setNumCols(int numCols) {
    this.numCols = numCols;
  }

  public int getNumRows() {
    return numRows;
  }

  public void setNumRows(int numRows) {
    this.numRows = numRows;
  }

  public Color getFontColor() {
    return fontColor;
  }

  public void setFontColor(Color fontColor) {
    this.fontColor = fontColor;
  }

  public float getFontHeight() {
    return fontHeight;
  }

  public void setFontHeight(float fontHeight) {
    this.fontHeight = fontHeight;
  }

  public float getLineHeight() {
    return lineHeight;
  }

  public void setLineHeight(float lineHeight) {
    this.lineHeight = lineHeight;
  }

  public float[] getCellPadding() {
    return cellPadding;
  }

  public void setCellPadding(float[] cellPadding) {
    this.cellPadding = cellPadding;
  }

  public float getCellHeight() {
    return cellHeight;
  }

  public void setCellHeight(float cellHeight) {
    this.cellHeight = cellHeight;
  }

  public float getCellWidth() {
    return cellWidth;
  }

  public void setCellWidth(float cellWidth) {
    this.cellWidth = cellWidth;
  }

  public float getPageHeight() {
    return pageHeight;
  }

  public void setPageHeight(float pageHeight) {
    this.pageHeight = pageHeight;
  }

  public float getPageWidth() {
    return pageWidth;
  }

  public void setPageWidth(float pageWidth) {
    this.pageWidth = pageWidth;
  }

  public float[] getPagePadding() {
    return pagePadding;
  }

  public void setPagePadding(float[] pagePadding) {
    this.pagePadding = pagePadding;
  }

  private Config() {}
}
