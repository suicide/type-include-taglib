/**
 * 
 */
package com.hastybox.typeincludetaglib.tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.hastybox.typeincludetaglib.path.TemplatePathFactory;
import com.hastybox.typeincludetaglib.wrapper.AttributeWrappingHttpServletRequestWrapper;
import com.hastybox.typeincludetaglib.wrapper.OutputWrappingHttpServletResponseWrapper;

/**
 * <p>
 * The Include Tag class. It accepts an object that is given in the
 * <code>self</code> member and a template modifier stored in the
 * <code>template</code> member. The <code>self</code> parameter is manadatory
 * while the <code>template</code> parameter is optional.
 * </p>
 * 
 * <p>
 * In the current implementation all templates to use must be stored in
 * <code>/WEB-INF/typeTemplates/</code> followed by a folder with the package
 * name and finally the jsp file with the class name. For example:
 * </p>
 * 
 * <pre>
 * /WEB-INF/typeTemplates/java.lang/String.jsp
 * /WEB-INF/typeTemplates/com.hastybox.blubb/Article.teaser.jsp
 * </pre>
 * 
 * @author psy
 * 
 */
public class IncludeTag extends BodyTagSupport {

	/**
	 * serial id
	 */
	private static final long serialVersionUID = 1561430403921500250L;

	/**
	 * object to render
	 */
	private Object self;

	/**
	 * template to use for rendering
	 */
	private String template;

	/**
	 * parameter store
	 */
	private Map<String, Object> params;

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

		params = new HashMap<String, Object>();

		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doEndTag() throws JspException {

		try {
			String path = TemplatePathFactory.getPath(self, template);

			RequestDispatcher requestDispatcher = pageContext.getRequest()
					.getRequestDispatcher(path);

			if (requestDispatcher == null) {
				throw new JspException("Could not locate template");
			}

			ServletRequest req = new AttributeWrappingHttpServletRequestWrapper(
					(HttpServletRequest) pageContext.getRequest());

			OutputWrappingHttpServletResponseWrapper res = new OutputWrappingHttpServletResponseWrapper(
					(HttpServletResponse) pageContext.getResponse());

			// set attributes to "new" context
			req.setAttribute("self", self);

			// add other parameters from Parameter Tags
			for (Entry<String, Object> param : params.entrySet()) {
				req.setAttribute(param.getKey(), param.getValue());
			}

			// include template
			requestDispatcher.include(req, res);

			// write stuff to outStream instead of flushing pageContext.getOut()
			res.getWriter().flush();

			// write to response
			pageContext.getOut().write(res.getOut().toString());

		} catch (ServletException e) {
			throw new JspException(e);
		} catch (IOException e) {
			throw new JspException(e);
		}

		return EVAL_PAGE;
	}

	/**
	 * adds a parameter to the tag
	 * 
	 * @param name
	 * @param value
	 */
	public void addParam(String name, Object value) {
		params.put(name, value);
	}

	/**
	 * @param self
	 *            the self to set
	 */
	public void setSelf(Object self) {
		this.self = self;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

}
