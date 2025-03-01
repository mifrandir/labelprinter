package me.miltz.labelprinter;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Builder {

  private final float pageWidth;
  private final float pageHeight;
  private final float[] pagePadding;
  private final float cellWidth;
  private final float cellHeight;
  private final float[] cellPadding;
  private final float lineHeight;
  private final float fontHeight;
  private final Color fontColor;
  private final int numRows;
  private final int numCols;

  private final float xOffset;
  private final float yOffset;

  public Builder(Config config) {
    pageWidth = config.getPageWidth();
    pageHeight = config.getPageHeight();
    pagePadding = config.getPagePadding();
    cellWidth = config.getCellWidth();
    cellHeight = config.getCellHeight();
    cellPadding = config.getCellPadding();
    lineHeight = config.getLineHeight();
    fontHeight = config.getFontHeight();
    fontColor = config.getFontColor();
    numRows = config.getNumRows();
    numCols = config.getNumCols();

    xOffset = cellPadding[3] + pagePadding[3];
    yOffset = pageHeight - cellPadding[0] - pagePadding[0];
  }

  public PDPage buildPage(final PDDocument document, final PDRectangle rectangle, final List<Recipient> recipients)
      throws IOException {
    var page = new PDPage(rectangle);
    document.addPage(page);
    try (var cs = new PDPageContentStream(document, page)) {
      for (int i = 0; i < recipients.size(); i++) {
        drawCell(recipients.get(i), i, cs);
      }
    }
    return page;
  }

  public PDDocument build(final List<Recipient> recipients)
      throws NullPointerException, IOException {
    Objects.requireNonNull(recipients);
    var numRecipients = recipients.size();
    var numPerPage = numCols * numRows;
    var numPages = 1 + (numRecipients - 1) / numPerPage;
    var rectangle = new PDRectangle();
    rectangle.setUpperRightX(pageWidth);
    rectangle.setUpperRightY(pageHeight);
    var document = new PDDocument();
    for (int i = 0; i < numPages; i++) {
      var start = i * numPerPage;
      var end = Math.min(start + numPerPage, recipients.size());
      buildPage(document, rectangle, recipients.subList(start, end));
    }
    return document;
  }

  // Creates a document containing two pages. The first contains lines with
  // distance 100 in PDF units for calibrating the scaling and the second
  // contains lines that are 20mm apart to verify the current scaling.
  public PDDocument buildMarkers() throws NullPointerException, IOException {
    var doc = new PDDocument();
    var rect = new PDRectangle();
    rect.setUpperRightX(pageWidth);
    rect.setUpperRightY(pageHeight);
    var markerPage = new PDPage(rect);
    doc.addPage(markerPage);
    try (var cs = new PDPageContentStream(doc, markerPage)) {
      drawMarkers(cs);
    }
    var mmMarkerPage = new PDPage(rect);
    doc.addPage(mmMarkerPage);
    try (var cs = new PDPageContentStream(doc, mmMarkerPage)) {
      drawMmMarkers(cs);
    }
    return doc;
  }

  // Used to print lines that are exactly 100 PDF units apart to calibrate
  // the scaling for a specific printer.
  private void drawMarkers(PDPageContentStream cs) throws IOException {
    cs.setNonStrokingColor(fontColor);
    for (float y = 0; y < pageHeight; y += 100.0) {
      cs.moveTo(0, y);
      cs.lineTo(pageWidth, y);
    }
    for (float x = 0; x < pageWidth; x += 100.0) {
      cs.moveTo(x, 0);
      cs.lineTo(x, pageHeight);
    }
    cs.stroke();
  }

  // Used to check that the conversion to mm works on the specific printer.
  // Prints lines that should have a distance of exactly 20mm.
  private void drawMmMarkers(PDPageContentStream cs) throws IOException {
    cs.setNonStrokingColor(fontColor);
    for (float y = 0; y < pageHeight; y += Config.fromMm(20)) {
      cs.moveTo(0, y);
      cs.lineTo(pageWidth, y);
    }
    for (float x = 0; x < pageWidth; x += Config.fromMm(20)) {
      cs.moveTo(x, 0);
      cs.lineTo(x, pageHeight);
    }
    cs.stroke();
  }

  private void drawCell(
      Recipient recipient,
      int totalNumCells,
      PDPageContentStream page)
      throws IOException {
    if (recipient == null || page == null) {
      return;
    }
    var x = xOffset + (totalNumCells % numCols) * cellWidth;
    var y = yOffset - (totalNumCells / numCols) * cellHeight;
    writeText(x, y, recipient.getName(), page);
    var addr = recipient.getAddress();
    for (var line : addr.toLines()) {
      y -= lineHeight;
      writeText(x, y, line, page);
    }
  }

  private void writeText(float x, float y, String text, PDPageContentStream cs)
      throws IOException {
    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA, fontHeight);
    cs.setNonStrokingColor(fontColor);
    cs.newLineAtOffset(x, y);
    cs.showText(text);
    cs.endText();
  }
}
