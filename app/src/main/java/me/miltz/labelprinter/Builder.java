package me.miltz.labelprinter;

import java.io.IOException;
import java.util.Objects;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Builder {

  private final Config config;
  private final float xOffset;
  private final float yOffset;

  private int numPerPage;
  private int numPages;

  public Builder(Config config) {
    this.config = new Config(config);
    xOffset = config.getCellPadding()[3] + config.getPagePadding()[3];
    yOffset =
      config.getPageHeight() -
      config.getCellPadding()[0] -
      config.getPagePadding()[0];
  }

  public PDDocument build(final Recipient[] recipients)
    throws NullPointerException, IOException {
    Objects.requireNonNull(recipients);
    numPerPage = config.getNumCols() * config.getNumRows();
    numPages = 1 + (recipients.length - 1) / numPerPage;
    var doc = new PDDocument();
    var rect = new PDRectangle();
    rect.setUpperRightX(config.getPageWidth());
    rect.setUpperRightY(config.getPageHeight());
    for (int i = 0; i < numPages; i++) {
      var page = new PDPage(rect);
      doc.addPage(page);
      try (var cs = new PDPageContentStream(doc, page)) {
        var indexOffset = i * numPerPage;
        for (int j = 0; j < numPerPage; j++) {
          if (j + indexOffset >= recipients.length) {
            break;
          }
          drawCell(recipients[indexOffset + j], j, cs);
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
    var x = xOffset + (numCell % config.getNumCols()) * config.getCellWidth();
    var y = yOffset - (numCell / config.getNumCols()) * config.getCellHeight();
    writeText(x, y, recipient.getName(), cs);
    var addr = recipient.getAddress();
    y -= config.getLineHeight() * 2f;
    writeText(x, y, addr[0], cs);
    y -= config.getLineHeight();
    writeText(x, y, addr[1], cs);
  }

  private void writeText(float x, float y, String text, PDPageContentStream cs)
    throws IOException {
    cs.beginText();
    cs.setFont(PDType1Font.HELVETICA, config.getFontHeight());
    cs.setNonStrokingColor(config.getFontColor());
    cs.newLineAtOffset(x, y);
    cs.showText(text);
    cs.endText();
  }
}
