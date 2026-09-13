/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportURLRewriter {

	public StaticSiteExportURLRewriter(
		JSONFactory jsonFactory, Map<String, String> pagePaths,
		String portalHost, Map<String, String> resourcePaths) {

		_jsonFactory = jsonFactory;
		_pagePaths = pagePaths;
		_portalHost = portalHost;
		_resourcePaths = resourcePaths;
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

	public String rewriteHTML(String html) {
		Document document = Jsoup.parse(html);

		for (String attributeName : _ATTRIBUTE_NAMES) {
			for (Element element : document.select("[" + attributeName + "]")) {
				String url = element.attr(attributeName);

				String path = _getPath(url);

				if (path != null) {
					element.attr(attributeName, path);
				}
				else if (_isAlternateLink(element) && _isPortalURL(url)) {
					element.remove();
				}
			}
		}

		for (Element element : document.select("[srcset]")) {
			List<String> candidates = new ArrayList<>();

			for (String candidate :
					StringUtil.split(element.attr("srcset"), CharPool.COMMA)) {

				String[] candidateParts = StringUtil.split(
					StringUtil.trim(candidate), CharPool.SPACE);

				String path = _getPath(candidateParts[0]);

				if (path != null) {
					candidateParts[0] = path;
				}

				candidates.add(
					StringUtil.merge(candidateParts, StringPool.SPACE));
			}

			element.attr("srcset", StringUtil.merge(candidates, ", "));
		}

		for (Element element : document.select("script[type=importmap]")) {
			_rewriteImportMap(element);
		}

		return document.outerHtml();
	}

	private String _getPath(String url) {
		if (Validator.isNull(url)) {
			return null;
		}

		url = _toPortalPath(StringUtil.trim(url));

		String path = _resourcePaths.get(url);

		if (path == null) {
			path = _resourcePaths.get(
				StringUtil.replace(url, "&amp;", StringPool.AMPERSAND));
		}

		if (path == null) {
			path = _pagePaths.get(url);
		}

		if (path == null) {
			return null;
		}

		return StringPool.SLASH + path;
	}

	private boolean _isAlternateLink(Element element) {
		if (StringUtil.equals(element.tagName(), "link") &&
			StringUtil.equals(element.attr("rel"), "alternate")) {

			return true;
		}

		return false;
	}

	private boolean _isPortalURL(String url) {
		return !StringUtil.equals(url, _toPortalPath(url));
	}

	private void _rewriteImportMap(Element element) {
		JSONObject jsonObject = null;

		try {
			jsonObject = _jsonFactory.createJSONObject(element.data());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return;
		}

		JSONObject importsJSONObject = jsonObject.getJSONObject("imports");

		if (importsJSONObject == null) {
			return;
		}

		Iterator<String> iterator = importsJSONObject.keys();

		while (iterator.hasNext()) {
			String specifier = iterator.next();

			String path = _getPath(importsJSONObject.getString(specifier));

			if (path != null) {
				importsJSONObject.put(specifier, path);
			}
		}

		element.text(jsonObject.toString());
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

	private static final String[] _ATTRIBUTE_NAMES = {
		"href", "poster", "src", "xlink:href"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		StaticSiteExportURLRewriter.class);

	private final JSONFactory _jsonFactory;
	private final Map<String, String> _pagePaths;
	private final String _portalHost;
	private final Map<String, String> _resourcePaths;

}