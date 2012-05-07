/**
 * 
 */
package com.hastybox.typeincludetaglib.tag;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;


import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.hastybox.typeincludetaglib.model.ArticleImpl;
import com.hastybox.typeincludetaglib.path.IncludeFactory;

/**
 * @author psy
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({IncludeFactory.class})
public class IncludeTagTest {
	
	/**
	 * tag to test
	 */
	private IncludeTag tag;
	
	/**
	 * request
	 */
	private HttpServletRequest request;
	
	/**
	 * response
	 */
	private HttpServletResponse response;
	
	/**
	 * pageContext
	 */
	private PageContext pageContext;
	
	private RequestDispatcher requestDispatcher;
	
	private JspWriter jspWriter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		
		requestDispatcher = mock(RequestDispatcher.class);
		jspWriter = mock(JspWriter.class);
		
		pageContext = mock(PageContext.class);
		
		when(pageContext.getRequest()).thenReturn(request);
		when(pageContext.getResponse()).thenReturn(response);
		when(pageContext.getOut()).thenReturn(jspWriter);
		
 		mockStatic(IncludeFactory.class);
		
		tag = new IncludeTag();
		
		tag.setPageContext(pageContext);
		
		// start the tag
		tag.doStartTag();
		
	}

	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.IncludeTag#doEndTag()}.
	 */
	@Test
	public void testInclude() throws Exception {
		
		ArticleImpl object = new ArticleImpl();
		String template = null;
		
		tag.setSelf(object);
		tag.setTemplate(template);
		
		when(IncludeFactory.getInclude(object, template, pageContext)).thenReturn(requestDispatcher);
		
		tag.doEndTag();
		
	}
	
	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.IncludeTag#doEndTag()}.
	 */
	@Test
	public void testIncludeParam() throws Exception {
		
		ArticleImpl object = new ArticleImpl();
		String template = null;
		
		tag.setSelf(object);
		tag.setTemplate(template);
		
		tag.addParam("name", "value");
		
		when(IncludeFactory.getInclude(object, template, pageContext)).thenReturn(requestDispatcher);
		
		tag.doEndTag();
		
	}
	
	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.IncludeTag#doEndTag()}.
	 */
	@Test
	public void testIncludeFail() throws Exception {
		
		ArticleImpl object = new ArticleImpl();
		String template = null;
		
		tag.setSelf(object);
		tag.setTemplate(template);
		
		when(IncludeFactory.getInclude(object, template, pageContext)).thenReturn(null);
		
		try {
			tag.doEndTag();
			fail();
		} catch (Exception e) {
			// pass
		}
		
	}

}
