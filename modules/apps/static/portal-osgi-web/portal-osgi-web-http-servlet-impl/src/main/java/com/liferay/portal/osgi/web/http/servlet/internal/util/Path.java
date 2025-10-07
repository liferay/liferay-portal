/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.http.servlet.internal.util;

import com.liferay.petra.string.CharPool;

/**
 * @author Dante Wang
 */
public class Path {

	public Path(String path) {
		int index = path.indexOf(CharPool.QUESTION);

		if (index == -1) {
			_queryString = null;

			_requestURI = path;
		}
		else {
			_queryString = path.substring(index + 1);

			_requestURI = path.substring(0, index);
		}

		index = _requestURI.lastIndexOf(CharPool.PERIOD);

		if ((index != -1) &&
			(index >= _requestURI.lastIndexOf(CharPool.SLASH))) {

			_extension = _requestURI.substring(index + 1);
		}
		else {
			_extension = null;
		}
	}

	public String getExtension() {
		return _extension;
	}

	public String getQueryString() {
		return _queryString;
	}

	public String getRequestURI() {
		return _requestURI;
	}

	private final String _extension;
	private final String _queryString;
	private final String _requestURI;

}