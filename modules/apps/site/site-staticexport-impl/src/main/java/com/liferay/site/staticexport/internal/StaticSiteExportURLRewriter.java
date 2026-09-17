/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportURLRewriter {

	public StaticSiteExportURLRewriter(
		Map<String, String> pagePaths, String portalHost,
		Map<String, String> resourcePaths) {

		_pagePaths = pagePaths;
		_portalHost = portalHost;
		_resourcePaths = resourcePaths;
	}

	public void rewrite(StaticSiteExportDocument staticSiteExportDocument) {
		staticSiteExportDocument.rewrite(this::_getPath, this::_isPortalURL);
	}

	public String rewriteCSS(String css) {
		for (Map.Entry<String, String> entry : _resourcePaths.entrySet()) {
			String url = entry.getKey();
			String path = StringPool.SLASH + entry.getValue();

			if (!url.equals(path)) {
				css = StringUtil.replace(css, url, path);
			}
		}

		return css;
	}

	private String _getPath(String url) {
		if (Validator.isNull(url)) {
			return null;
		}

		url = _toPortalPath(StringUtil.trim(url));

		String path = _resourcePaths.get(url);

		if (path == null) {
			path = _pagePaths.get(url);
		}

		if (path == null) {
			return null;
		}

		return StringPool.SLASH + path;
	}

	private boolean _isPortalURL(String url) {
		return !StringUtil.equals(url, _toPortalPath(url));
	}

	private String _toPortalPath(String url) {
		int index = url.indexOf("://");

		if (index == -1) {
			return url;
		}

		int pathIndex = url.indexOf(CharPool.SLASH, index + 3);

		if (pathIndex == -1) {
			return url;
		}

		String host = url.substring(index + 3, pathIndex);

		int portIndex = host.indexOf(CharPool.COLON);

		if (portIndex != -1) {
			host = host.substring(0, portIndex);
		}

		if (StringUtil.equalsIgnoreCase(host, _portalHost)) {
			return url.substring(pathIndex);
		}

		return url;
	}

	private final Map<String, String> _pagePaths;
	private final String _portalHost;
	private final Map<String, String> _resourcePaths;

}