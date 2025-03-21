/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class MetaInfoCacheServletResponse extends HttpServletResponseWrapper {

	@SuppressWarnings("deprecation")
	public static void finishResponse(
			MetaData metaInfoDataBag, HttpServletResponse httpServletResponse)
		throws IOException {

		if (httpServletResponse.isCommitted()) {
			return;
		}

		resetThrough(httpServletResponse);

		for (Map.Entry<String, Set<Header>> entry :
				metaInfoDataBag._headers.entrySet()) {

			String key = entry.getKey();

			boolean first = true;

			for (Header header : entry.getValue()) {
				if (first) {
					header.setToResponse(key, httpServletResponse);

					first = false;
				}
				else {
					header.addToResponse(key, httpServletResponse);
				}
			}
		}

		if (metaInfoDataBag._location != null) {
			httpServletResponse.sendRedirect(metaInfoDataBag._location);
		}
		else if (metaInfoDataBag._error) {
			httpServletResponse.sendError(
				metaInfoDataBag._status, metaInfoDataBag._errorMessage);
		}
		else {
			if (metaInfoDataBag._charsetName != null) {
				httpServletResponse.setCharacterEncoding(
					metaInfoDataBag._charsetName);
			}

			if (metaInfoDataBag._contentLength != -1) {
				httpServletResponse.setContentLengthLong(
					metaInfoDataBag._contentLength);
			}

			if (metaInfoDataBag._contentType != null) {
				httpServletResponse.setContentType(
					metaInfoDataBag._contentType);
			}

			if (metaInfoDataBag._locale != null) {
				httpServletResponse.setLocale(metaInfoDataBag._locale);
			}

			if (metaInfoDataBag._status != SC_OK) {
				httpServletResponse.setStatus(metaInfoDataBag._status);
			}
		}
	}

	public MetaInfoCacheServletResponse(
		HttpServletResponse httpServletResponse) {

		super(httpServletResponse);
	}

	@Override
	public void addCookie(Cookie cookie) {

		// The correct header name should be "Set-Cookie". Otherwise, the method
		// containsHeader will not able to detect cookies with the correct
		// header name.

		Set<Header> values = _metaData._headers.get(HttpHeaders.SET_COOKIE);

		if (values == null) {
			values = new HashSet<>();

			_metaData._headers.put(HttpHeaders.SET_COOKIE, values);
		}

		Header header = new Header(cookie);

		values.add(header);

		super.addCookie(cookie);
	}

	@Override
	public void addDateHeader(String name, long value) {
		Set<Header> values = _metaData._headers.get(name);

		if (values == null) {
			values = new HashSet<>();

			_metaData._headers.put(name, values);
		}

		Header header = new Header(value);

		values.add(header);

		super.addDateHeader(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		if (name.equals(HttpHeaders.CONTENT_TYPE)) {
			setContentType(value);

			return;
		}

		Set<Header> values = _metaData._headers.get(name);

		if (values == null) {
			values = new HashSet<>();

			_metaData._headers.put(name, values);
		}

		Header header = new Header(value);

		values.add(header);

		super.addHeader(name, value);
	}

	@Override
	public void addIntHeader(String name, int value) {
		Set<Header> values = _metaData._headers.get(name);

		if (values == null) {
			values = new HashSet<>();

			_metaData._headers.put(name, values);
		}

		Header header = new Header(value);

		values.add(header);

		super.addIntHeader(name, value);
	}

	@Override
	public boolean containsHeader(String name) {
		return _metaData._headers.containsKey(name);
	}

	public void finishResponse() throws IOException {
		finishResponse(_metaData, (HttpServletResponse)getResponse());

		_committed = true;
	}

	@Override
	public void flushBuffer() throws IOException {
		_committed = true;
	}

	@Override
	public int getBufferSize() {
		return _metaData._bufferSize;
	}

	@Override
	public String getCharacterEncoding() {

		// We are supposed to default to ISO-8859-1 based on the Servlet
		// specification. However, most application servers honors the system
		// property "file.encoding". Using the system default character gives us
		// better application server compatibility.

		if (_metaData._charsetName == null) {
			return StringPool.DEFAULT_CHARSET_NAME;
		}

		return _metaData._charsetName;
	}

	@Override
	public String getContentType() {
		String contentType = _metaData._contentType;

		if ((contentType != null) && (_metaData._charsetName != null)) {
			contentType = StringBundler.concat(
				contentType, "; charset=", _metaData._charsetName);
		}

		return contentType;
	}

	/**
	 * When the header for this given name is "Cookie", the return value cannot
	 * be used for the "Set-Cookie" header. The string representation for
	 * "Cookie" is application server specific. The only safe way to add the
	 * header is to call {@link HttpServletResponse#addCookie(Cookie)}.
	 */
	@Override
	public String getHeader(String name) {
		Set<Header> values = _metaData._headers.get(name);

		if (values == null) {
			return null;
		}

		Iterator<Header> iterator = values.iterator();

		Header header = iterator.next();

		return header.toString();
	}

	@Override
	public Collection<String> getHeaderNames() {
		return _metaData._headers.keySet();
	}

	public Map<String, Set<Header>> getHeaders() {
		return _metaData._headers;
	}

	/**
	 * When the header for this given name is "Cookie", the return value cannot
	 * be used for the "Set-Cookie" header. The string representation for
	 * "Cookie" is application server specific. The only safe way to add the
	 * header is to call {@link HttpServletResponse#addCookie(Cookie)}.
	 */
	@Override
	public Collection<String> getHeaders(String name) {
		Set<Header> values = _metaData._headers.get(name);

		if (values == null) {
			return Collections.emptyList();
		}

		List<String> stringValues = new ArrayList<>();

		for (Header header : values) {
			stringValues.add(header.toString());
		}

		return stringValues;
	}

	@Override
	public Locale getLocale() {
		return _metaData._locale;
	}

	public MetaData getMetaData() {
		return _metaData;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		calledGetOutputStream = true;

		return super.getOutputStream();
	}

	@Override
	public int getStatus() {
		return _metaData._status;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		calledGetWriter = true;

		return super.getWriter();
	}

	@Override
	public boolean isCommitted() {
		ServletResponse servletResponse = getResponse();

		if (_committed || servletResponse.isCommitted()) {
			return true;
		}

		return false;
	}

	@Override
	public void reset() {
		if (isCommitted()) {
			throw new IllegalStateException("Reset after commit");
		}

		// No need to reset _error, _errorMessage and _location, because setting
		// them requires commit.

		_metaData._charsetName = null;
		_metaData._contentLength = -1;
		_metaData._contentType = null;
		_metaData._headers.clear();
		_metaData._locale = null;
		_metaData._status = SC_OK;

		// calledGetOutputStream and calledGetWriter should be cleared by
		// resetBuffer() in subclass.

		resetBuffer();

		super.reset();
	}

	@Override
	public void resetBuffer() {
		if (isCommitted()) {
			throw new IllegalStateException("Reset buffer after commit");
		}

		resetBuffer(false);
	}

	@Override
	public void sendError(int status) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Send error after commit");
		}

		_metaData._error = true;
		_metaData._status = status;

		resetBuffer();

		_committed = true;

		super.sendError(status);
	}

	@Override
	public void sendError(int status, String errorMessage) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Send error after commit");
		}

		_metaData._error = true;
		_metaData._errorMessage = errorMessage;
		_metaData._status = status;

		resetBuffer();

		_committed = true;

		super.sendError(status, errorMessage);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		if (isCommitted()) {
			throw new IllegalStateException("Send redirect after commit");
		}

		resetBuffer(true);

		setStatus(SC_FOUND);

		_metaData._location = location;

		_committed = true;

		super.sendRedirect(location);
	}

	@Override
	public void setBufferSize(int bufferSize) {
		if (isCommitted()) {
			throw new IllegalStateException("Set buffer size after commit");
		}

		_metaData._bufferSize = bufferSize;

		super.setBufferSize(bufferSize);
	}

	@Override
	public void setCharacterEncoding(String charsetName) {
		if (isCommitted()) {
			return;
		}

		_setCharacterEncoding(charsetName);
	}

	@Override
	public void setContentLength(int contentLength) {
		if (isCommitted()) {
			return;
		}

		_metaData._contentLength = contentLength;

		super.setContentLength(contentLength);
	}

	@Override
	public void setContentLengthLong(long contentLength) {
		if (isCommitted()) {
			return;
		}

		_metaData._contentLength = contentLength;

		super.setContentLengthLong(contentLength);
	}

	@Override
	public void setContentType(String contentType) {
		if (isCommitted() || (contentType == null)) {
			return;
		}

		int index = contentType.indexOf(CharPool.SEMICOLON);

		if (index != -1) {
			String firstPart = contentType.substring(0, index);

			_metaData._contentType = firstPart.trim();

			index = contentType.indexOf("charset=");

			if (index != -1) {
				String charsetName = contentType.substring(index + 8);

				charsetName = charsetName.trim();

				_setCharacterEncoding(charsetName);
			}
			else {
				_metaData._charsetName = null;
			}
		}
		else {
			_metaData._contentType = contentType;

			_metaData._charsetName = null;
		}

		super.setContentType(contentType);
	}

	@Override
	public void setDateHeader(String name, long value) {
		Set<Header> values = new HashSet<>();

		_metaData._headers.put(name, values);

		Header header = new Header(value);

		values.add(header);

		super.setDateHeader(name, value);
	}

	@Override
	public void setHeader(String name, String value) {
		if (name.equals(HttpHeaders.CONTENT_TYPE)) {
			setContentType(value);

			return;
		}

		Set<Header> values = new HashSet<>();

		_metaData._headers.put(name, values);

		Header header = new Header(value);

		values.add(header);

		super.setHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		Set<Header> values = new HashSet<>();

		_metaData._headers.put(name, values);

		Header header = new Header(value);

		values.add(header);

		super.setIntHeader(name, value);
	}

	@Override
	public void setLocale(Locale locale) {
		if (isCommitted()) {
			return;
		}

		_metaData._locale = locale;

		super.setLocale(locale);
	}

	@Override
	public void setStatus(int status) {
		if (isCommitted()) {
			return;
		}

		_metaData._status = status;

		super.setStatus(status);
	}

	@SuppressWarnings("deprecation")
	public void setStatus(int status, String statusMessage) {
		setStatus(status);
	}

	@Override
	public String toString() {
		return StringBundler.concat(
			"{bufferSize=", _metaData._bufferSize, ", charsetName=",
			_metaData._charsetName, ", committed=", _committed,
			", contentLength=", _metaData._contentLength, ", contentType=",
			_metaData._contentType, ", error=", _metaData._error,
			", errorMessage=", _metaData._errorMessage, ", headers=",
			_metaData._headers, ", location=", _metaData._location, ", locale=",
			_metaData._locale, ", status=", _metaData._status, "}");
	}

	public static class MetaData implements Serializable {

		private int _bufferSize;
		private String _charsetName;
		private long _contentLength = -1;
		private String _contentType;
		private boolean _error;
		private String _errorMessage;
		private final Map<String, Set<Header>> _headers = new HashMap<>();
		private Locale _locale;
		private String _location;
		private int _status = SC_OK;

	}

	protected static void resetThrough(
		HttpServletResponse httpServletResponse) {

		if (httpServletResponse instanceof MetaInfoCacheServletResponse) {
			MetaInfoCacheServletResponse metaInfoCacheServletResponse =
				(MetaInfoCacheServletResponse)httpServletResponse;

			resetThrough(
				(HttpServletResponse)
					metaInfoCacheServletResponse.getResponse());
		}
		else {
			httpServletResponse.reset();
		}
	}

	/**
	 * Stub method for subclass to provide buffer resetting logic.
	 *
	 * @param nullOutReferences whether to reset flags. It is not directly used
	 *        by this class. Subclasses with an actual buffer may behave
	 *        differently depending on the value of this parameter.
	 */
	protected void resetBuffer(boolean nullOutReferences) {
		super.resetBuffer();
	}

	protected boolean calledGetOutputStream;
	protected boolean calledGetWriter;

	private void _setCharacterEncoding(String charsetName) {
		if (calledGetWriter || (charsetName == null)) {
			return;
		}

		_metaData._charsetName = charsetName;

		super.setCharacterEncoding(charsetName);
	}

	private boolean _committed;
	private final MetaData _metaData = new MetaData();

}