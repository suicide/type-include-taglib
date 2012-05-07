/**
 * 
 */
package com.hastybox.typeincludetaglib.path;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Creates request dispatcher for includes and stores paths for faster access.
 * 
 * @author psy
 * 
 */
public class IncludeFactory {

	/**
	 * private constructor
	 */
	private IncludeFactory() {
		// no instances
	}

	/**
	 * base path for type templates to include
	 */
	public static final String BASEPATH = "/WEB-INF/typeTemplates/";

	/**
	 * store for path lookups. Is it cool to store is here?
	 */
	private static final Map<String, String> PATH_STORE;

	static {
		PATH_STORE = new ConcurrentHashMap<String, String>();
	}

	/**
	 * 
	 * searches template from given <code>object</code> and
	 * <code>template</code>
	 * 
	 * @param o
	 * @param template
	 * @param pageContext
	 * @return
	 */
	public static RequestDispatcher getInclude(Object o, String template,
			PageContext pageContext) throws JspException {

		StringBuilder lookupBuilder = new StringBuilder();
		lookupBuilder.append(o.getClass().getName());
		lookupBuilder.append("#");
		lookupBuilder.append(template);

		String lookupPath = lookupBuilder.toString();

		String lookup = PATH_STORE.get(lookupPath);

		RequestDispatcher requestDispatcher;

		if (lookup == null) {

			LookupResult result = findInclude(o.getClass(), template,
					pageContext);

			if (result.getRequestDispatcher() == null) {
				// needed as ConcurrentHashMap does not support null values

				throw new JspException(
						String.format(
								"No template for rendering %s with template %s was found",
								o.getClass().getName(), template));
			}

			requestDispatcher = result.getRequestDispatcher();

			// store in cache
			PATH_STORE.put(lookupPath, result.getPath());

		} else {
			// just get it again
			requestDispatcher = pageContext.getRequest().getRequestDispatcher(
					lookup);
		}

		return requestDispatcher;
	}

	/**
	 * Finds a template by first searching for the class itself, then the
	 * interfaces and then the superclass recursively
	 * 
	 * @param clazz
	 * @param template
	 * @param pageContext
	 * @return
	 * @throws JspException
	 */
	@SuppressWarnings("rawtypes")
	private static LookupResult findInclude(Class clazz, String template,
			PageContext pageContext) throws JspException {

		LookupResult result = getRequestDispatcher(clazz, template, pageContext);

		if (result.getRequestDispatcher() == null) {
			// check interfaces for existing template
			Class[] interfaces = clazz.getInterfaces();

			for (Class interfaze : interfaces) {
				result = getRequestDispatcher(interfaze, template, pageContext);

				if (result.getRequestDispatcher() != null) {
					break;
				}
			}

			// check superclass and its interfaces
			if (result.getRequestDispatcher() == null) {
				Class superclass = clazz.getSuperclass();

				// there is no superclass of Object
				if (superclass != null) {
					result = findInclude(clazz.getSuperclass(), template,
							pageContext);
				}
			}

		}

		return result;
	}

	/**
	 * Tries to find the request dispatcher from the given class and template
	 * 
	 * @param clazz
	 * @param template
	 * @param pageContext
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static LookupResult getRequestDispatcher(Class clazz,
			String template, PageContext pageContext) {
		String path = buildPath(clazz, template);

		RequestDispatcher requestDispatcher = getRequestDispatcher(path,
				pageContext);

		return new LookupResult(requestDispatcher, path);
	}

	/**
	 * Finds the RequestDispatcher from the given path. Also checks if a
	 * template exists in this path else returns null
	 * 
	 * @param path
	 * @param pageContext
	 * @return
	 */
	private static RequestDispatcher getRequestDispatcher(String path,
			PageContext pageContext) {

		InputStream stream = pageContext.getServletContext()
				.getResourceAsStream(path);
		
		// return null if jsp is not available
		if (stream == null) {
			return null;
		}

		return pageContext.getRequest().getRequestDispatcher(path);
	}

	/**
	 * Creates path String from given class and template
	 * 
	 * @param clazz
	 * @param template
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static String buildPath(Class clazz, String template) {
		// create path to jsp to be included
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(BASEPATH);

		// find package
		Package selfPackage = clazz.getPackage();
		if (selfPackage != null) {
			pathBuilder.append(selfPackage.getName());
		}
		// add class name
		pathBuilder.append("/");
		pathBuilder.append(clazz.getSimpleName());

		// add template if set
		if (template != null) {
			pathBuilder.append(".");
			pathBuilder.append(template);
		}
		pathBuilder.append(".jsp");

		return pathBuilder.toString();
	}
	
	/**
	 * clears include path cache
	 */
	public static void clearCache() {
		PATH_STORE.clear();
	}
	
	/**
	 * 
	 * exports Cache store
	 * 
	 * @return
	 */
	public static Map<String, String> getPathStore() {
		return PATH_STORE;
	}

	/**
	 * return container
	 * 
	 * @author psy
	 * 
	 */
	private static final class LookupResult {

		/**
		 * path to JSP include
		 */
		private final String path;

		/**
		 * resulting RequestDipatcher
		 */
		private final RequestDispatcher requestDispatcher;

		/**
		 * constructor
		 * 
		 * @param requestDispatcher
		 * @param path
		 */
		public LookupResult(RequestDispatcher requestDispatcher, String path) {

			this.requestDispatcher = requestDispatcher;
			this.path = path;

		}

		/**
		 * @return the path
		 */
		public String getPath() {
			return path;
		}

		/**
		 * @return the requestDispatcher
		 */
		public RequestDispatcher getRequestDispatcher() {
			return requestDispatcher;
		}

	}
}
