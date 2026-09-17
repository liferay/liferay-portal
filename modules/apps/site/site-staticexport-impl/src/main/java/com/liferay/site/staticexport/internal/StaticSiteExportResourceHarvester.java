/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.staticexport.internal;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportResourceHarvester {

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

			_addURL(_resolve(cssURL, url), urls);
		}

		return urls;
	}

	public Set<String> harvestDocument(
		StaticSiteExportDocument staticSiteExportDocument) {

		Set<String> urls = new LinkedHashSet<>();

		for (String url : staticSiteExportDocument.getURLs()) {
			_addURL(url, urls);
		}

		return urls;
	}

	public Set<String> harvestJS(
		Map<String, String> importMapPrefixes, String js, String jsURL) {

		Set<String> urls = new LinkedHashSet<>();

		Matcher matcher = _jsModulePathPattern.matcher(js);

		while (matcher.find()) {
			_addURL(StringPool.SLASH + matcher.group(1), urls);
		}

		String strippedJS = _blockCommentPattern.matcher(
			js
		).replaceAll(
			StringPool.BLANK
		);

		matcher = _jsRelativeModulePathPattern.matcher(strippedJS);

		while (matcher.find()) {
			_addURL(_resolve(jsURL, matcher.group(1)), urls);
		}

		matcher = _jsRelativeStylesheetPathPattern.matcher(strippedJS);

		while (matcher.find()) {
			_addURL(_resolve(jsURL, matcher.group(1)), urls);
		}

		for (Map.Entry<String, String> entry : importMapPrefixes.entrySet()) {
			Pattern pattern = Pattern.compile(
				"[\"'`]" + Pattern.quote(entry.getKey()) + "([-/.\\w]+)[\"'`]");

			matcher = pattern.matcher(js);

			while (matcher.find()) {
				_addURL(entry.getValue() + matcher.group(1), urls);
			}
		}

		return urls;
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

	private String _resolve(String baseURL, String url) {
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

	private static final String _RESOURCE_EXTENSIONS =
		"css|gif|ico|jpeg|jpg|js|json|png|svg|webp|woff|woff2";

	private static final String[] _RESOURCE_PREFIXES = {
		"/combo", "/documents/", "/image/", "/o/", "/webserver/"
	};

	private static final Pattern _blockCommentPattern = Pattern.compile(
		"/\\*.*?\\*/", Pattern.DOTALL);
	private static final Pattern _cssURLPattern = Pattern.compile(
		"url\\(([^)]+)\\)|@import\\s+[\"']([^\"']+)[\"']");
	private static final Pattern _jsModulePathPattern = Pattern.compile(
		"[\"'`](?:\\$\\{[^}]*\\})?/?(o/[-@$/.\\w()]+\\.(?:" +
			_RESOURCE_EXTENSIONS + "))[\"'`]");
	private static final Pattern _jsRelativeModulePathPattern = Pattern.compile(
		"(?:from|import)\\s*\\(?\\s*[\"'`](\\.{1,2}/[-@$/.\\w()]+" +
			"\\.(?:css|js))[\"'`]");
	private static final Pattern _jsRelativeStylesheetPathPattern =
		Pattern.compile("[\"'](\\.{1,2}/[-@$/.\\w()]+\\.css)[\"']");

}