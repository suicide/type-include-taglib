/**
 * 
 */
package com.hastybox.typeincludetaglib.path;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates Template paths and stores them for faster access.
 * 
 * @author psy
 *
 */
public class TemplatePathFactory {
	
	/**
	 * private constructor
	 */
	private TemplatePathFactory() {
		// no instances
	}

	/**
	 * base path for type templates to include
	 */
	private static final String BASEPATH = "/WEB-INF/typeTemplates/";
	
	/**
	 * store for path lookups. Is it cool to store is here?
	 */
	private static final Map<String, String> PATH_STORE;
	
	static {
		PATH_STORE = new ConcurrentHashMap<String, String>();
	}
	
	/**
	 * 
	 * searches template path from given <code>object</code> and <code>template</code>
	 * 
	 * @param o
	 * @param template
	 * @return
	 */
	public static String getPath(Object o, String template) {
		
		StringBuilder lookupBuilder = new StringBuilder();
		lookupBuilder.append(o.getClass().getName());
		lookupBuilder.append("#");
		lookupBuilder.append(template);
		
		String lookupPath = lookupBuilder.toString();
		
		String lookup = PATH_STORE.get(lookupPath);
		
		if (lookup == null) {
			
			// create path to jsp to be included
			StringBuilder pathBuilder = new StringBuilder();
			pathBuilder.append(BASEPATH);

			// find package
			Package selfPackage = o.getClass().getPackage();
			if (selfPackage != null) {
				pathBuilder.append(selfPackage.getName());
			}
			// add class name
			pathBuilder.append("/");
			pathBuilder.append(o.getClass().getSimpleName());

			// add template if set
			if (template != null) {
				pathBuilder.append(".");
				pathBuilder.append(template);
			}
			pathBuilder.append(".jsp");
			
			//store in cache
			lookup = pathBuilder.toString();
			PATH_STORE.put(lookupPath, lookup);
			
		}
		
		return lookup;
	}

}
