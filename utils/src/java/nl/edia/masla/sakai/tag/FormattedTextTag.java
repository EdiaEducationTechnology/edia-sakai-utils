package nl.edia.masla.sakai.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.sakaiproject.util.FormattedText;

public class FormattedTextTag extends BodyTagSupport {
	
	private static final long serialVersionUID = 7516396297751202216L;

	public FormattedTextTag() {
		
	}
	
	@Override
	public int doStartTag() throws JspException {
	    return EVAL_BODY_BUFFERED;
	}
	
	@Override
	public int doAfterBody() throws JspException {
		String myString = this.bodyContent.getString();
		try {
			bodyContent.getEnclosingWriter().append(FormattedText.escapeHtmlFormattedText(myString));
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
