/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Map;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportURLRewriter {

	public StaticSiteExportURLRewriter(
		Map<String, String> pagePaths, Map<String, String> resourcePaths) {

		_pagePaths = pagePaths;
		_resourcePaths = resourcePaths;
	}

	public void rewrite(StaticSiteExportDocument staticSiteExportDocument) {
		staticSiteExportDocument.rewrite(this::_getPath);
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

		url = StringUtil.trim(url);

		String path = _resourcePaths.get(url);

		if (path == null) {
			path = _pagePaths.get(url);
		}

		if (path == null) {
			return null;
		}

		return StringPool.SLASH + path;
	}

	private final Map<String, String> _pagePaths;
	private final Map<String, String> _resourcePaths;

}