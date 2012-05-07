/**
 * 
 */
package com.hastybox.typeincludetaglib.tag;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.TagSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author psy
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TagSupport.class})
public class ParamTagTest {
	
	private ParamTag tag;
	
	private IncludeTag includeTag;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		includeTag = mock(IncludeTag.class);
		
		mockStatic(TagSupport.class);
		
		tag = new ParamTag();
	}

	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.ParamTag#doEndTag()}.
	 */
	@Test
	public void testNormal() throws Exception {
		tag.setName("param");
		tag.setValue("value");
		
		when(TagSupport.findAncestorWithClass(tag, IncludeTag.class)).thenReturn(includeTag);
		
		tag.doEndTag();
	}
	
	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.ParamTag#doEndTag()}.
	 */
	@Test
	public void testBody() throws Exception {
		BodyContent body = mock(BodyContent.class);
		when(body.getString()).thenReturn("value");
		
		tag.setName("param");
		tag.setBodyContent(body);
		
		when(TagSupport.findAncestorWithClass(tag, IncludeTag.class)).thenReturn(includeTag);
		
		tag.doEndTag();
	}
	
	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.ParamTag#doEndTag()}.
	 */
	@Test
	public void testNoName() throws Exception {
		
		tag.setValue("value");
		
		when(TagSupport.findAncestorWithClass(tag, IncludeTag.class)).thenReturn(includeTag);
		
		try {
			tag.doEndTag();
			fail();
		} catch (Exception e) {
			// pass
		}
	}
	
	/**
	 * Test method for {@link com.hastybox.typeincludetaglib.tag.ParamTag#doEndTag()}.
	 */
	@Test
	public void testNotInIncludeTag() throws Exception {
		tag.setName("name");
		tag.setValue("value");
		
		try {
			tag.doEndTag();
			fail();
		} catch (Exception e) {
			// pass
		}
	}

}
