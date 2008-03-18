/*
 * Copyright Edia 2007 
 */
package nl.edia.propertyeditors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.edia.sakai.tool.util.SakaiUtils;

public class DateTypeEditor extends PropertyEditorSupport implements PropertyEditor {
	
	DateFormat dateFormat;
	
	public DateTypeEditor(DateFormat dateFormat) {
	    super();
	    this.dateFormat = dateFormat;
    }

	public DateTypeEditor(String dateFormat) {
	    super();
	    this.dateFormat = new SimpleDateFormat(dateFormat);
    }

	public DateTypeEditor() {
		this("yyyy-MM-dd hh:mm");
    }
	
	

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return "";
		}
		dateFormat.setTimeZone(SakaiUtils.getTimeZone());
		if (getValue() instanceof Date) {
			return dateFormat.format(getValue());
		}
		return super.getAsText();
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		dateFormat.setTimeZone(SakaiUtils.getTimeZone());
		try {
			super.setValue(dateFormat.parse(text));
		} catch (ParseException e) {
			super.setValue(null);
		}
	}
}
