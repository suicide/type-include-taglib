package com.hastybox.typeincludetaglib.wrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * ServletResponseWrapper that fetches the output in order to include it in the
 * original response later on.
 * 
 * @author psy
 * 
 */
public class OutputWrappingHttpServletResponseWrapper extends
		HttpServletResponseWrapper {

	/**
	 * ServletOutputStream that stores the output in a ByteArrayOutputStream for
	 * further usage.
	 * 
	 * @author psy
	 * 
	 */
	private static final class StoringServletOutputStream extends
			ServletOutputStream {

		/**
		 * simple ByteArrayOutputStream to fetch output
		 */
		private ByteArrayOutputStream out;

		/**
		 * constructor
		 */
		public StoringServletOutputStream() {
			out = new ByteArrayOutputStream();
		}

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

	/**
	 * Dummy ServletOutputStream
	 */
	private ServletOutputStream out;

	/**
	 * Writer wrapped arout OutputStream
	 */
	private PrintWriter writer;

	/**
	 * 
	 * constructor
	 * 
	 * @param response
	 */
	public OutputWrappingHttpServletResponseWrapper(HttpServletResponse response) {
		super(response);
		// initialization
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
		return ((OutputWrappingHttpServletResponseWrapper.StoringServletOutputStream) out)
				.getOut();
	}

}