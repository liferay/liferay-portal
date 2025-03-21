/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author László Csontos
 * @author Shuyang Zhou
 * @author Tomas Polesovsky
 */
public class SanitizedServletResponse extends HttpServletResponseWrapper {

	public static HttpServletResponse getSanitizedServletResponse(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		setXContentOptions(httpServletRequest, httpServletResponse);
		setXFrameOptions(httpServletRequest, httpServletResponse);

		return httpServletResponse;
	}

	@Override
	public void addHeader(String name, String value) {
		super.addHeader(
			HttpComponentsUtil.sanitizeHeader(name),
			HttpComponentsUtil.sanitizeHeader(value));
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		super.sendRedirect(HttpComponentsUtil.sanitizeHeader(location));
	}

	@Override
	public void setCharacterEncoding(String charset) {
		super.setCharacterEncoding(HttpComponentsUtil.sanitizeHeader(charset));
	}

	@Override
	public void setContentType(String contentType) {
		super.setContentType(HttpComponentsUtil.sanitizeHeader(contentType));
	}

	@Override
	public void setHeader(String name, String value) {
		super.setHeader(
			HttpComponentsUtil.sanitizeHeader(name),
			HttpComponentsUtil.sanitizeHeader(value));
	}

	protected static void setXContentOptions(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!_X_CONTENT_TYPE_OPTIONS) {
			return;
		}

		if (_X_CONTENT_TYPE_OPTIONS_URLS_EXCLUDES.length > 0) {
			String requestURI = httpServletRequest.getRequestURI();

			for (String url : _X_CONTENT_TYPE_OPTIONS_URLS_EXCLUDES) {
				if (requestURI.startsWith(url)) {
					return;
				}
			}
		}

		httpServletResponse.setHeader(
			HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff");
	}

	protected static void setXFrameOptions(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (!_X_FRAME_OPTIONS) {
			return;
		}

		String requestURI = httpServletRequest.getRequestURI();

		for (KeyValuePair xFrameOptionKVP : _xFrameOptionKVPs) {
			String url = xFrameOptionKVP.getKey();

			if (requestURI.startsWith(url)) {
				String value = xFrameOptionKVP.getValue();

				if (value != null) {
					httpServletResponse.setHeader(
						HttpHeaders.X_FRAME_OPTIONS,
						xFrameOptionKVP.getValue());
				}

				return;
			}
		}

		httpServletResponse.setHeader(HttpHeaders.X_FRAME_OPTIONS, "DENY");
	}

	private SanitizedServletResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}

	private static final boolean _X_CONTENT_TYPE_OPTIONS =
		GetterUtil.getBoolean(
			SystemProperties.get("http.header.secure.x.content.type.options"),
			true);

	private static final String[] _X_CONTENT_TYPE_OPTIONS_URLS_EXCLUDES =
		SystemProperties.getArray(
			"http.header.secure.x.content.type.options.urls.excludes");

	private static final boolean _X_FRAME_OPTIONS;

	private static final KeyValuePair[] _xFrameOptionKVPs;

	static {
		String httpHeaderSecureXFrameOptionsKey =
			"http.header.secure.x.frame.options";

		Map<String, String> properties = SystemProperties.getProperties(
			httpHeaderSecureXFrameOptionsKey.concat(StringPool.PERIOD), true);

		List<KeyValuePair> xFrameOptionKVPs = new ArrayList<>(
			properties.size());

		List<String> propertyNames = new ArrayList<>(properties.keySet());

		propertyNames.sort(
			Comparator.comparingInt(GetterUtil::getIntegerStrict));

		for (String propertyName : propertyNames) {
			String propertyValue = properties.get(propertyName);

			String[] propertyValueParts = StringUtil.split(
				propertyValue, CharPool.PIPE);

			if (propertyValueParts.length > 2) {
				continue;
			}

			String url = StringUtil.trim(propertyValueParts[0]);

			if (Validator.isNull(url)) {
				continue;
			}

			if (propertyValueParts.length == 1) {
				xFrameOptionKVPs.add(new KeyValuePair(url, null));

				continue;
			}

			String value = StringUtil.trim(propertyValueParts[1]);

			if (Validator.isNull(value)) {
				value = null;
			}

			xFrameOptionKVPs.add(new KeyValuePair(url, value));
		}

		_xFrameOptionKVPs = xFrameOptionKVPs.toArray(new KeyValuePair[0]);

		if (_xFrameOptionKVPs.length == 0) {
			_X_FRAME_OPTIONS = false;
		}
		else {
			_X_FRAME_OPTIONS = GetterUtil.getBoolean(
				SystemProperties.get(httpHeaderSecureXFrameOptionsKey), true);
		}
	}

}