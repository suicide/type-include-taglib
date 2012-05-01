/**
 * 
 */
package com.hastybox.typeincludetaglib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * @author psy
 * 
 */
public class IncludeTag extends TagSupport {

	private static final class OutputWrappingHttpServletResponseWrapper extends
			HttpServletResponseWrapper {
		private static final class StoringServletOutputStream extends
				ServletOutputStream {
			private ByteArrayOutputStream out = new ByteArrayOutputStream();

			@Override
			public void write(int b) throws IOException {
				out.write(b);
			}

			/**
			 * @return the out
			 */
			public ByteArrayOutputStream getOut() {
				return out;
			}
			
		}

		private ServletOutputStream out;
		private PrintWriter writer;

		private OutputWrappingHttpServletResponseWrapper(
				HttpServletResponse response) {
			super(response);
		}

		public void init() {
			out = new StoringServletOutputStream();
			
			writer = new PrintWriter(out);
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return out;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			return writer;
		}
		
		public ByteArrayOutputStream getOut() {
			return ((StoringServletOutputStream) out).getOut();
		}

	}

	/**
	 * Manages given attributes instead of attributes from original Request when available
	 * 
	 * @author psy
	 *
	 */
	static private final class AttributeWrappingHttpServletRequestWrapper extends
			HttpServletRequestWrapper {
		private final Map<String, Object> wrappingAttributes;

		private AttributeWrappingHttpServletRequestWrapper(
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

	/**
	 * serial id
	 */
	private static final long serialVersionUID = 1561430403921500250L;
	
	/**
	 * base path for type templates to include
	 */
	private static final String BASEPATH = "/WEB-INF/typeTemplates/";
	
	/**
	 * object to render
	 */
	private Object self;
	
	/**
	 * template to use for rendering
	 */
	private String template;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.jsp.tagext.TagSupport#setPageContext(javax.servlet.jsp.
	 * PageContext)
	 */
	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {

		try {
			ServletRequest req = new AttributeWrappingHttpServletRequestWrapper((HttpServletRequest) pageContext.getRequest());
			req.setAttribute("self", self);
			
			// create path to jsp to be included
			StringBuilder pathBuilder = new StringBuilder();
			pathBuilder.append(BASEPATH);
			
			// find package
			Package selfPackage = self.getClass().getPackage();
			if (selfPackage != null) {
				pathBuilder.append(selfPackage.getName());
			}
			// add class name
			pathBuilder.append("/");
			pathBuilder.append(self.getClass().getSimpleName());
			
			// add template if set
			if (template != null) {
				pathBuilder.append(".");
				pathBuilder.append(template);
			}
			pathBuilder.append(".jsp");
			
			OutputWrappingHttpServletResponseWrapper res = new OutputWrappingHttpServletResponseWrapper((HttpServletResponse) pageContext.getResponse());
			res.init();
			
			pageContext.getRequest().getRequestDispatcher(pathBuilder.toString())
					.include(req, res);
			
			// write stuff to outStream instead of flushing pageContext.getOut()
			res.getWriter().flush();
			
			pageContext.getOut().write(res.getOut().toString());
			//pageContext.getResponse().getWriter().write(res.getOut().toString());
			
		} catch (ServletException e) {
			throw new JspException(e);
		} catch (IOException e) {
			throw new JspException(e);
		}

		return SKIP_BODY;
	}
	
	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	/**
	 * @param self the self to set
	 */
	public void setSelf(Object self) {
		this.self = self;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

}
