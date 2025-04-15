/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.portlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.portlet.ActionURL;
import jakarta.portlet.CacheControl;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortalContext;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderURL;
import jakarta.portlet.ResourceURL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

import org.springframework.util.CollectionUtils;

/**
 * @author Dante Wang
 */
public class MockMimeResponse
	extends MockPortletResponse implements MimeResponse {

	public MockMimeResponse() {
	}

	public MockMimeResponse(PortalContext portalContext) {
		super(portalContext);
	}

	public MockMimeResponse(
		PortalContext portalContext, PortletRequest portletRequest) {

		super(portalContext);

		_portletRequest = portletRequest;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends PortletURL & ActionURL> T createActionURL() {
		return (T)new MockPortletURL(
			getPortalContext(), MockPortletURL.URL_TYPE_ACTION);
	}

	@Override
	public ActionURL createActionURL(Copy copy) {
		return new MockActionURL(getPortalContext(), copy);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends PortletURL & RenderURL> T createRenderURL() {
		return (T)new MockPortletURL(
			getPortalContext(), MockPortletURL.URL_TYPE_RENDER);
	}

	@Override
	public RenderURL createRenderURL(Copy copy) {
		return new MockRenderURL(getPortalContext(), copy);
	}

	@Override
	public ResourceURL createResourceURL() {
		return new MockResourceURL(getPortalContext(), "resource");
	}

	@Override
	public void flushBuffer() {
		if (_printWriter != null) {
			_printWriter.flush();
		}

		try {
			_byteArrayOutputStream.flush();
		}
		catch (IOException ioException) {
			throw new IllegalStateException(
				"Unable to flush output stream: " + ioException.getMessage());
		}

		_committed = true;
	}

	@Override
	public int getBufferSize() {
		return _bufferSize;
	}

	@Override
	public CacheControl getCacheControl() {
		return _cacheControl;
	}

	@Override
	public String getCharacterEncoding() {
		return _characterEncoding;
	}

	public byte[] getContentAsByteArray() {
		flushBuffer();

		return _byteArrayOutputStream.toByteArray();
	}

	public String getContentAsString() throws UnsupportedEncodingException {
		flushBuffer();

		if (_characterEncoding == null) {
			return _byteArrayOutputStream.toString();
		}

		return _byteArrayOutputStream.toString(_characterEncoding);
	}

	@Override
	public String getContentType() {
		return _contentType;
	}

	public String getForwardedUrl() {
		return _forwardedUrl;
	}

	public String getIncludedUrl() {
		return _includedUrl;
	}

	@Override
	public Locale getLocale() {
		return _locale;
	}

	@Override
	public OutputStream getPortletOutputStream() throws IOException {
		return _byteArrayOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws UnsupportedEncodingException {
		if (_printWriter == null) {
			if (_characterEncoding == null) {
				_printWriter = new PrintWriter(
					new OutputStreamWriter(_byteArrayOutputStream));
			}
			else {
				_printWriter = new PrintWriter(
					new OutputStreamWriter(
						_byteArrayOutputStream, _characterEncoding));
			}
		}

		return _printWriter;
	}

	@Override
	public boolean isCommitted() {
		return _committed;
	}

	@Override
	public void reset() {
		resetBuffer();

		_characterEncoding = null;
		_contentType = null;
		_locale = null;
	}

	@Override
	public void resetBuffer() {
		if (_committed) {
			throw new IllegalStateException(
				"Unable to reset buffer because response is already committed");
		}

		_byteArrayOutputStream.reset();
	}

	@Override
	public void setBufferSize(int bufferSize) {
		_bufferSize = bufferSize;
	}

	public void setCharacterEncoding(String characterEncoding) {
		_characterEncoding = characterEncoding;
	}

	public void setCommitted(boolean committed) {
		_committed = committed;
	}

	@Override
	public void setContentType(String contentType) {
		if (_portletRequest != null) {
			Enumeration<String> supportedTypesEnumeration =
				_portletRequest.getResponseContentTypes();

			if (!CollectionUtils.contains(
					supportedTypesEnumeration, contentType)) {

				throw new IllegalArgumentException(
					StringBundler.concat(
						"Content type [", contentType,
						"] not in supported list: ",
						Collections.list(supportedTypesEnumeration)));
			}
		}

		_contentType = contentType;
	}

	public void setForwardedUrl(String forwardedUrl) {
		_forwardedUrl = forwardedUrl;
	}

	public void setIncludedUrl(String includedUrl) {
		_includedUrl = includedUrl;
	}

	public void setLocale(Locale locale) {
		_locale = locale;
	}

	private int _bufferSize = 4096;
	private final ByteArrayOutputStream _byteArrayOutputStream =
		new ByteArrayOutputStream();

	private final CacheControl _cacheControl = new CacheControl() {

		@Override
		public String getETag() {
			return _eTag;
		}

		@Override
		public int getExpirationTime() {
			return _expirationTime;
		}

		@Override
		public boolean isPublicScope() {
			return _publicScope;
		}

		@Override
		public void setETag(String eTag) {
			_eTag = eTag;
		}

		@Override
		public void setExpirationTime(int expirationTime) {
			_expirationTime = expirationTime;
		}

		@Override
		public void setPublicScope(boolean publicScope) {
			_publicScope = publicScope;
		}

		@Override
		public void setUseCachedContent(boolean useCachedContent) {
			_useCachedContent = useCachedContent;
		}

		@Override
		public boolean useCachedContent() {
			return _useCachedContent;
		}

		private String _eTag;
		private int _expirationTime;
		private boolean _publicScope;
		private boolean _useCachedContent;

	};

	private String _characterEncoding = "ISO-8859-1";
	private boolean _committed;
	private String _contentType;
	private String _forwardedUrl;
	private String _includedUrl;
	private Locale _locale = LocaleUtil.getDefault();
	private PortletRequest _portletRequest;
	private PrintWriter _printWriter;

}