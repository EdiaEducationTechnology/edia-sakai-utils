package nl.edia.masla.sakai.tag;

import javax.servlet.jsp.JspException;

import junit.framework.TestCase;

import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.util.impl.FormattedTextImpl;

public class TrimFormattedTextTagTest extends TestCase {

	public void testGetTextFormatted() throws JspException {
		ComponentManager.testingMode = true;
		ComponentManager.loadComponent(org.sakaiproject.util.api.FormattedText.class,  new FormattedTextImpl());
		String myString = "formatted";
		String myTextFormatted = new TrimFormattedTextTag().getTextFormatted(myString);
		assertEquals(myString, myTextFormatted);
	}

}
