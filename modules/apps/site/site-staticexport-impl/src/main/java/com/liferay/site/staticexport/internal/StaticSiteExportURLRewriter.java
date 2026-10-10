/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportURLRewriter {

	public StaticSiteExportURLRewriter(
		Map<Locale, Map<String, String>> pagePathsMap,
		Set<String> portalHostNames, Map<String, String> resourcePaths) {

		_pagePathsMap = pagePathsMap;
		_portalHostNames = portalHostNames;
		_resourcePaths = resourcePaths;
	}

	public void rewrite(
		Locale locale, StaticSiteExportDocument staticSiteExportDocument) {

		Map<String, String> pagePaths = _pagePathsMap.getOrDefault(
			locale, Collections.emptyMap());

		staticSiteExportDocument.rewrite(url -> _getPath(pagePaths, url));
	}

	private String _getPath(Map<String, String> pagePaths, String url) {
		if (Validator.isNull(url)) {
			return null;
		}

		StaticSiteExportURL staticSiteExportURL = new StaticSiteExportURL(
			_portalHostNames, url);

		String path = _resourcePaths.get(staticSiteExportURL.getURL());

		if (path == null) {
			path = pagePaths.get(staticSiteExportURL.getURL());
		}

		if (path == null) {
			return null;
		}

		return StringPool.SLASH + path + staticSiteExportURL.getURIFragment();
	}

	private final Map<Locale, Map<String, String>> _pagePathsMap;
	private final Set<String> _portalHostNames;
	private final Map<String, String> _resourcePaths;

}