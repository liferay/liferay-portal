/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;

/**
 * @author Brian Myunghun Kim
 * @author Brian Wing Shun Chan
 */
public class LiferayServletRequest extends HttpServletRequestWrapper {

	public LiferayServletRequest(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);

		_httpServletRequest = httpServletRequest;
	}

	public void cleanUp() {
		if (_liferayInputStream != null) {
			_liferayInputStream.cleanUp();
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (_liferayInputStream == null) {
			_liferayInputStream = new LiferayInputStream(_httpServletRequest);
		}

		if (_finishedReadingOriginalStream) {

			// Return the cached input stream the second time the user requests
			// the input stream, otherwise, it will return an empty input stream
			// because it has already been parsed

			if (_cachedServletInputStream == null) {
				_cachedServletInputStream =
					_liferayInputStream.getCachedInputStream();
			}

			return _cachedServletInputStream;
		}

		return _liferayInputStream;
	}

	public void setFinishedReadingOriginalStream(
		boolean finishedReadingOriginalStream) {

		_finishedReadingOriginalStream = finishedReadingOriginalStream;
	}

	private ServletInputStream _cachedServletInputStream;
	private boolean _finishedReadingOriginalStream;
	private final HttpServletRequest _httpServletRequest;
	private LiferayInputStream _liferayInputStream;

}