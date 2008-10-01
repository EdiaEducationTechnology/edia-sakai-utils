package nl.edia.masla.sakai.tag;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.sakaiproject.util.FormattedText;

public class TrimFormattedTextTag extends BodyTagSupport {
	
	/**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5005393748678249301L;

	public TrimFormattedTextTag() {
		
	}
	
	protected int maxNumOfChars = 100;
	
	public int getMaxNumOfChars() {
    	return maxNumOfChars;
    }

	public void setMaxNumOfChars(int maxNumOfChars) {
    	this.maxNumOfChars = maxNumOfChars;
    }

	@Override
	public int doStartTag() throws JspException {
	    return EVAL_BODY_BUFFERED;
	}
	
	@Override
	public int doAfterBody() throws JspException {
		String myString = this.bodyContent.getString();
		try {
			String myFormattedText = getTextFormatted(myString);
			
			if (myFormattedText != null) {
				bodyContent.getEnclosingWriter().append(myFormattedText);
			}
			bodyContent.clearBuffer();
		} catch (IOException e) {
			throw new JspException(e);
		}
	    return EVAL_BODY_INCLUDE;
	}
	
	/**
	 * <p>
	 * This method calls {@link FormattedText#trimFormattedText(String, int, StringBuffer)} or 
	 * FormattedText#trimFormattedText(String, int, StringBuilder)}
	 * depending on which is available. 
	 * </p>
	 * <p>
	 * Apparently the method footprint has changed in 2.5.x by someone who does not really care about other people's code.
	 * </p> 
	 * @param text
	 * @return
	 * @throws JspException
	 */
	public String getTextFormatted(String text) throws JspException {
	    // The method trimFormattedText changed signature in 2.5.x
	    // Try to find the method as string builder...
	    String myFormattedText = trimFormattedTextUsingStringBuilder(text);
	    if (myFormattedText == null) {
		    // Try to find the method as string buffer...
	    	myFormattedText = trimFormattedTextUsingStringBuffer(text);
	    }
	    return myFormattedText;
    }

	protected String trimFormattedTextUsingStringBuilder(String text) throws JspException {
	    return callTrimFormattedTextUsing(text, new StringBuilder());
    }

	protected String trimFormattedTextUsingStringBuffer(String text) throws JspException {
	    return callTrimFormattedTextUsing(text, new StringBuffer());
    }
	
	/**
	 * Ugly, ugly, ugly. Get the method by builder class and than call it.
	 * @param myString
	 * @param myBuilder
	 * @return
	 * @throws JspException
	 */
	protected String callTrimFormattedTextUsing(String myString, Object myBuilder) throws JspException {
	    try {
		    Method myMethod = FormattedText.class.getMethod("trimFormattedText", String.class, int.class, myBuilder.getClass());
		    // Method is static, obj can be null
		    myMethod.invoke(null, myString, maxNumOfChars, myBuilder);
		    return myBuilder.toString();
	    } catch (NoSuchMethodException e) {
	    	// Expected, return null to flag the unavailability of the method.
	    	return null;
	    } catch (IllegalArgumentException e) {
	    	// Unexpected
        	throw new JspException(e);
        } catch (IllegalAccessException e) {
	    	// Unexpected
        	throw new JspException(e);
        } catch (InvocationTargetException e) {
	    	// Unexpected
        	throw new JspException(e);
        }
    }
	
	@Override
	public int doEndTag() throws JspException {
	    return super.doEndTag();
	}
	
	public void doTag() throws JspException, IOException {
		String myString = this.bodyContent.getString();
		this.bodyContent.append(FormattedText.escapeHtmlFormattedText(myString));
	}
}
