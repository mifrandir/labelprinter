package me.miltz.labelprinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.pdfbox.pdmodel.PDDocument;

public class App {

  static String read(final String inputPath) throws IOException {
    final File inputFile = new File(inputPath);
    // Reading the input
    final byte[] buffer = new byte[(int) inputFile.length()];
    try (var file = new FileInputStream(inputFile);) {
      int numRead = file.read(buffer);
      if (numRead <= 0) {
        throw new IOException();
      }
    }
    return new String(buffer, StandardCharsets.UTF_8);
  }

  static void write(final String outputPath, final PDDocument pdf) throws IOException {
    final File outputFile = new File(outputPath);
    pdf.save(outputFile);
    pdf.close();
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("ERROR: Missing required arguments.");
      System.err.println("Usage: label-printer <input.xml> <output.pdf>");
      return;
    }
    final var xml = read(args[0]);
    final var config = Config.defaultConfig();
    final var recipients = new Parser(config).parse(xml);
    final var pdf = new Builder(config).build(recipients);
    write(args[1], pdf);
  }
}
