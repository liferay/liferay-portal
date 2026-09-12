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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceHarvester {

	public StaticSiteExportResourceHarvester(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	public Set<String> harvestCSS(String css, String cssURL) {
		Set<String> urls = new LinkedHashSet<>();

		Matcher matcher = _cssURLPattern.matcher(css);

		while (matcher.find()) {
			String url = matcher.group(1);

			if (url == null) {
				url = matcher.group(2);
			}

			url = _unquote(url);

			if (url.startsWith(StringPool.POUND)) {
				continue;
			}

			_addURL(urls, _resolve(url, cssURL));
		}

		return urls;
	}

	public Set<String> harvestHTML(String html) {
		Set<String> urls = new LinkedHashSet<>();

		Document document = Jsoup.parse(html);

		for (String attributeName : _ATTRIBUTE_NAMES) {
			for (Element element : document.select("[" + attributeName + "]")) {
				_addURL(element.attr(attributeName), urls);
			}
		}

		for (Element element : document.select("[srcset]")) {
			for (String candidate :
					StringUtil.split(element.attr("srcset"), CharPool.COMMA)) {

				String[] candidateParts = StringUtil.split(
					StringUtil.trim(candidate), CharPool.SPACE);

				_addURL(candidateParts[0], urls);
			}
		}

		for (Element element : document.select("script[type=importmap]")) {
			_addImportMapURLs(element.data(), urls);
		}

		return urls;
	}

	private void _addImportMapURLs(String importMap, Set<String> urls) {
		JSONObject importsJSONObject = null;

		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(importMap);

			importsJSONObject = jsonObject.getJSONObject("imports");
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return;
		}

		if (importsJSONObject == null) {
			return;
		}

		Iterator<String> iterator = importsJSONObject.keys();

		while (iterator.hasNext()) {
			String url = importsJSONObject.getString(iterator.next());

			if (!url.endsWith(StringPool.SLASH)) {
				_addURL(url, urls);
			}
		}
	}

	private void _addURL(String url, Set<String> urls) {
		if (Validator.isNull(url)) {
			return;
		}

		url = StringUtil.trim(url);

		int index = url.indexOf(CharPool.POUND);

		if (index != -1) {
			url = url.substring(0, index);
		}

		for (String prefix : _RESOURCE_PREFIXES) {
			if (url.startsWith(prefix)) {
				urls.add(url);

				return;
			}
		}
	}

	private String _resolve(String url, String baseURL) {
		if (Validator.isNull(url) || url.startsWith(StringPool.SLASH) ||
			url.startsWith("data:") || url.startsWith("http")) {

			return url;
		}

		int index = baseURL.lastIndexOf(CharPool.SLASH);

		if (index == -1) {
			return url;
		}

		String path = baseURL.substring(0, index + 1);

		while (url.startsWith("../")) {
			url = url.substring(3);

			path = path.substring(0, path.length() - 1);

			int lastIndex = path.lastIndexOf(CharPool.SLASH);

			if (lastIndex == -1) {
				break;
			}

			path = path.substring(0, lastIndex + 1);
		}

		if (url.startsWith("./")) {
			url = url.substring(2);
		}

		return path + url;
	}

	private String _unquote(String url) {
		if (Validator.isNull(url)) {
			return url;
		}

		url = StringUtil.unquote(StringUtil.trim(url));

		while (!url.isEmpty() &&
			   ((url.charAt(0) == CharPool.QUOTE) ||
				(url.charAt(0) == CharPool.APOSTROPHE))) {

			url = url.substring(1);
		}

		return url;
	}

	private static final String[] _ATTRIBUTE_NAMES = {
		"href", "poster", "src", "xlink:href"
	};

	private static final String[] _RESOURCE_PREFIXES = {
		"/combo", "/documents/", "/image/", "/o/", "/webserver/"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		StaticSiteExportResourceHarvester.class);

	private static final Pattern _cssURLPattern = Pattern.compile(
		"url\\(([^)]+)\\)|@import\\s+[\"']([^\"']+)[\"']");

	private final JSONFactory _jsonFactory;

}