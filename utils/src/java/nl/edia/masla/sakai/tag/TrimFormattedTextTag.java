package nl.edia.masla.sakai.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import org.sakaiproject.util.api.FormattedText;
import org.sakaiproject.component.cover.ComponentManager;

public class TrimFormattedTextTag extends BodyTagSupport {

	private static final Log LOG = LogFactory.getLog(TrimFormattedTextTag.class);

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
	 * This method calls {@link FormattedTextImpl#trimFormattedText(String, int, StringBuilder)}
	 * </p>
	 * @param text
	 * @return
	 * @throws JspException
	 */
	public String getTextFormatted(String text) throws JspException {

		StringBuilder stringBuilder = new StringBuilder();

		FormattedText formattedText = getFormattedText();

		boolean trimSucceeded = formattedText.trimFormattedText(text, maxNumOfChars, stringBuilder);
		if(!trimSucceeded) {
			LOG.warn("formattedText.trimFormattedText(text, maxNumOfChars, stringBuilder) failed");
		}

		return stringBuilder.toString();
    }

	protected FormattedText getFormattedText() {
		return (FormattedText) ComponentManager.getInstance().get(FormattedText.class);
	}

	@Override
	public int doEndTag() throws JspException {
	    return super.doEndTag();
	}
	
	public void doTag() throws JspException, IOException {
		String myString = this.bodyContent.getString();
		FormattedText formattedText = getFormattedText();
		this.bodyContent.append(formattedText.escapeHtmlFormattedText(myString));
	}
}
