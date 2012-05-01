package com.hastybox.typeincludetaglib.wrapper;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Manages given attributes instead of attributes from original Request when
 * available
 * 
 * @author psy
 * 
 */
public class AttributeWrappingHttpServletRequestWrapper
		extends HttpServletRequestWrapper {
	
	/**
	 * attributes container
	 */
	private final Map<String, Object> wrappingAttributes;

	/**
	 * constructor
	 * 
	 * @param request
	 */
	public AttributeWrappingHttpServletRequestWrapper(
			HttpServletRequest request) {
		super(request);

		// initialize
		wrappingAttributes = new HashMap<String, Object>();
	}

	@Override
	public void setAttribute(String name, Object o) {
		wrappingAttributes.put(name, o);
	}

	@Override
	public Object getAttribute(String name) {
		// look for attribute in local container
		Object o = wrappingAttributes.get(name);
		if (o != null) {
			return o;
		}
		// return attribute from original request
		return super.getAttribute(name);
	}
}