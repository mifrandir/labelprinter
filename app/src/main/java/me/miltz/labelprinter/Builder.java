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

  private void drawCell(
    Recipient recipient,
    int numCell,
    PDPageContentStream cs
  )
    throws IOException {
    if (recipient == null || cs == null) {
      return;
    }
    var x = xOffset + (numCell % numCols) * cellWidth;
    var y = yOffset - (numCell / numCols) * cellHeight;
    writeText(x, y, recipient.getName(), cs);
    var addr = recipient.getAddress();
    y -= lineHeight * 2f;
    writeText(x, y, addr[0], cs);
    y -= lineHeight;
    writeText(x, y, addr[1], cs);
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
