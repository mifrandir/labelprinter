package me.miltz.labelprinter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.*;
import java.util.logging.Level;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom2.*;
import org.jdom2.input.DOMBuilder;
import org.xml.sax.SAXException;

class Parser {

  private String[][] addressPattern;
  private String[] namePattern;
  private String idPattern;
  private String groupPattern;
  private Logger log;

  public Parser(Config config) {
    addressPattern = config.getAddressPattern();
    namePattern = config.getNamePattern();
    idPattern = config.getIdPattern();
    groupPattern = config.getGroupPattern();
    this.log = Logger.getLogger(Parser.class.getName());
  }

  private List<Recipient> filter(List<Recipient> raw) {
    var recipients = new ArrayList<Recipient>();
    var seen = new HashSet<Integer>();
    for (var rec : raw) {
      if (seen.contains(rec.getId())) {
        log.info("Dropped " + rec.getName());
      } else {
        seen.add(rec.getId());
        recipients.add(rec);
      }
    }
    Collections.sort(recipients);
    return recipients;
  }

  public List<Recipient> parse(String input) {
    var doc = readIntoDocument(input);
    if (doc == null) {
      log.log(Level.SEVERE, "Could not retrieve recipient list");
      return new ArrayList<>();
    }
    Element root = doc.getRootElement();
    var children = root.getChildren();
    var recipients = new ArrayList<Recipient>();
    String text;
    for (var xmlRecipient : children) {
      var nameJoiner = new StringJoiner(" ");
      for (var key : namePattern) {
        text = xmlRecipient.getChildText(key);
        if (text == null) {
          log.log(Level.WARNING, "Could not find name attribute %s", key);
          continue;
        }
        nameJoiner.add(text);
      }
      var name = nameJoiner.toString();
      var address = new String[addressPattern.length];
      for (int i = 0; i < address.length; i++) {
        var addressJoiner = new StringJoiner(" ");
        for (var key : addressPattern[i]) {
          addressJoiner.add(xmlRecipient.getChildText(key));
        }
        address[i] = addressJoiner.toString();
      }
      int id = Integer.parseInt(xmlRecipient.getChildText(idPattern));
      int group = Integer.parseInt(xmlRecipient.getChildText(groupPattern));
      recipients.add(new Recipient(name, address, id, group));
    }
    return filter(recipients);
  }

  private Document readIntoDocument(String input) {
    var stream = new ByteArrayInputStream(input.getBytes());
    Document doc;
    var factory = DocumentBuilderFactory.newInstance();
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
    try {
      var documentBuilder = factory.newDocumentBuilder();
      var w3cDoc = documentBuilder.parse(stream);
      doc = new DOMBuilder().build(w3cDoc);
    } catch (IOException | ParserConfigurationException | SAXException e) {
      log.log(Level.SEVERE, "Could not parse XML document");
      return null;
    }
    return doc;
  }
}
