package nl.edia.masla.sakai.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.sakaiproject.util.FormattedText;

public class TrimFormattedTextTag extends BodyTagSupport {
	
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
			StringBuffer myBuffer = new StringBuffer();
			FormattedText.trimFormattedText(myString, maxNumOfChars, myBuffer);
			bodyContent.getEnclosingWriter().append(myBuffer.toString());
			bodyContent.clearBuffer();
		} catch (IOException e) {
			throw new JspException(e);
		}
	    return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
	    // TODO Auto-generated method stub
	    return super.doEndTag();
	}
	
	  public void doTag() throws JspException, IOException {
		  String myString = this.bodyContent.getString();
		  this.bodyContent.append(FormattedText.escapeHtmlFormattedText(myString));
	  }
}
