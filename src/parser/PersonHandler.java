package parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class PersonHandler extends DefaultHandler {

	private Locator locator;
	private boolean insidePerson;
	private String k;

	private String value;
	private SimpleDateFormat formatMDate;
	
	protected String key;
	protected String recordTag;
	protected String mdate;
	protected List<String> personsPerPublication;

	public PersonHandler() {
		personsPerPublication = new ArrayList<String>(125);
		formatMDate =  new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
	}

	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}

	public Date getMDate() {
		try {
			return formatMDate.parse(mdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void startElement(String namespaceURI, String localName,
			String rawName, Attributes atts) throws SAXException {

		k = atts.getValue("key");
		mdate = atts.getValue("mdate");
		value = "";
		insidePerson = rawName.equals("author") || rawName.equals("editor");

		if (k != null) {
			key = k;
			recordTag = rawName;
		}
	}

	protected abstract void endPublication();
		
	public void endElement(String namespaceURI, String localName,
			String rawName) throws SAXException {
		if (rawName.equals("author") || rawName.equals("editor")) {
			personsPerPublication.add(value);
			return;
		}

		if (rawName.equals(recordTag)) {
			endPublication();
			personsPerPublication.clear();
		}
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (insidePerson)
			value += new String(ch, start, length);
	}

	private void Message(String mode, SAXParseException exception) {
		System.out.println(mode + " Line: " + exception.getLineNumber()
				+ " URI: " + exception.getSystemId() + "\n" + " Message: "
				+ exception.getMessage());
	}

	public void warning(SAXParseException exception) throws SAXException {

		Message("**Parsing Warning**\n", exception);
		throw new SAXException("Warning encountered");
	}

	public void error(SAXParseException exception) throws SAXException {

		Message("**Parsing Error**\n", exception);
		throw new SAXException("Error encountered");
	}

	public void fatalError(SAXParseException exception) throws SAXException {

		Message("**Parsing Fatal Error**\n", exception);
		throw new SAXException("Fatal Error encountered");
	}
}