package me.miltz.labelprinter;

import java.awt.Color;
import java.io.IOException;
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

  public PDDocument build(final List<Recipient> recipients)
      throws NullPointerException, IOException {
    int numPerPage, numPages;
    int numRecipients = recipients.size();
    Objects.requireNonNull(recipients);
    numPerPage = numCols * numRows;
    numPages = 1 + (numRecipients - 1) / numPerPage;
    var doc = new PDDocument();
    var rect = new PDRectangle();
    rect.setUpperRightX(pageWidth);
    rect.setUpperRightY(pageHeight);
    for (int i = 0; i < numPages; i++) {
      var page = new PDPage(rect);
      doc.addPage(page);
      try (var cs = new PDPageContentStream(doc, page)) {
        var indexOffset = i * numPerPage;
        for (int j = 0; j < numPerPage; j++) {
          if (j + indexOffset >= numRecipients) {
            break;
          }
          drawCell(recipients.get(indexOffset + j), j, cs);
        }
      }
    }
    return doc;
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
      int numCell,
      PDPageContentStream cs)
      throws IOException {
    if (recipient == null || cs == null) {
      return;
    }
    var x = xOffset + (numCell % numCols) * cellWidth;
    var y = yOffset - (numCell / numCols) * cellHeight;
    writeText(x, y, recipient.getName(), cs);
    var addr = recipient.getAddress();
    for (var line : addr.toLines()) {
      y -= lineHeight;
      writeText(x, y, line, cs);
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
