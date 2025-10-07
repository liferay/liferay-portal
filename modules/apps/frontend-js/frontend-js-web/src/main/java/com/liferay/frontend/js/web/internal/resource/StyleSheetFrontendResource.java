/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.Map;
import java.util.SortedMap;

/**
 * @author Iván Zaera Avellón
 */
public class StyleSheetFrontendResource implements FrontendResource {

	public StyleSheetFrontendResource(
		String eTag, boolean immutable, long maxAge, boolean sendNoCache,
		SortedMap<String, String> tokens, URL url) {

		_eTag = eTag;
		_immutable = immutable;
		_maxAge = maxAge;
		_sendNoCache = sendNoCache;
		_tokens = tokens;
		_url = url;
	}

	@Override
	public String getContentType() {
		return ContentTypes.TEXT_CSS;
	}

	@Override
	public String getETag() {
		if (_eTag == null) {
			return null;
		}

		if (_tokens == null) {
			return _eTag;
		}

		StringBuilder tokensSB = new StringBuilder();

		for (Map.Entry<String, String> entry : _tokens.entrySet()) {
			tokensSB.append(entry.getKey());
			tokensSB.append(StringPool.EQUAL);
			tokensSB.append(entry.getValue());
			tokensSB.append(StringPool.POUND);
		}

		int hash = 0;

		String tokensString = tokensSB.toString();

		byte[] data = tokensString.getBytes(StandardCharsets.UTF_8);

		for (byte b : data) {
			hash = (31 * hash) + b;
		}

		return _eTag + StringPool.DASH + StringUtil.toHexString(hash);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (_tokens == null) {
			return _url.openStream();
		}

		String content = StreamUtil.toString(_url.openStream());

		for (Map.Entry<String, String> entry : _tokens.entrySet()) {
			content = StringUtil.replace(
				content, StringPool.AT + entry.getKey() + StringPool.AT,
				entry.getValue());
		}

		return new ByteArrayInputStream(
			content.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public long getMaxAge() {
		return _maxAge;
	}

	@Override
	public boolean isImmutable() {
		return _immutable;
	}

	@Override
	public boolean isSendNoCache() {
		return _sendNoCache;
	}

	private final String _eTag;
	private final boolean _immutable;
	private final long _maxAge;
	private final boolean _sendNoCache;
	private final SortedMap<String, String> _tokens;
	private final URL _url;

}