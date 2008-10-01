package nl.edia.masla.sakai.tag;

import javax.servlet.jsp.JspException;

import junit.framework.TestCase;

public class TrimFormattedTextTagTest extends TestCase {

	public void testGetTextFormatted() throws JspException {
		new TrimFormattedTextTag().getTextFormatted("formatted");
	}

}
