/**
 * 
 */
package com.hastybox.typeincludetaglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;

/**
 * Parameter Tag. Add additional parameters to Include Tag
 * 
 * @author psy
 *
 */
public class ParamTag extends BodyTagSupport {
	
	/**
	 * serial id
	 */
	private static final long serialVersionUID = 4630475308417875671L;

	/**
	 * name
	 */
	private String name;
	
	/**
	 * value
	 */
	private Object value;

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	
	@Override
	public int doEndTag() throws JspException {
		// name is mandatory
		if (name == null) {
			throw new JspException("name parameter not provided");
		}
		
		// test if we are in an include tag
		Tag parentTag = findAncestorWithClass(this, IncludeTag.class);
		
		if (parentTag == null) {
			throw new JspException("Tag not inside of include tag");
		}
		
		IncludeTag includeTag = (IncludeTag) parentTag;
		
		// value can also be in the body
		if (value == null) {
			if (bodyContent == null || bodyContent.getString() == null) {
				value = "";
				
			} else {
				value = bodyContent.getString().trim();
			}
		}
		
		// add values to parent include tag
		includeTag.addParam(name, value);
		
		return EVAL_PAGE;
	}

}
