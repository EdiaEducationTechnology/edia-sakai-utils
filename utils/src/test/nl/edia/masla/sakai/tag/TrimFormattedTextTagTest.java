package nl.edia.masla.sakai.tag;

import javax.servlet.jsp.JspException;

import junit.framework.TestCase;
import org.sakaiproject.util.api.FormattedText;
import org.sakaiproject.util.impl.FormattedTextImpl;

public class TrimFormattedTextTagTest extends TestCase {

	public void testGetTextFormatted() throws JspException {
		String myString = "formatted";

		TrimFormattedTextTag formattedTextTag = new TrimFormattedTextTag(){
			protected FormattedText getFormattedText() {
				return new FormattedTextImpl();
			}
		};

		String myTextFormatted = formattedTextTag.getTextFormatted(myString);
		assertEquals(myString, myTextFormatted);
	}

}
