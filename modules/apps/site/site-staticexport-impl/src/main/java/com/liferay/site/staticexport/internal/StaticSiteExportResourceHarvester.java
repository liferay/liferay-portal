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

	private static final String[] _ATTRIBUTE_NAMES = {
		"href", "poster", "src", "xlink:href"
	};

	private static final String[] _RESOURCE_PREFIXES = {
		"/combo", "/documents/", "/image/", "/o/", "/webserver/"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		StaticSiteExportResourceHarvester.class);

	private final JSONFactory _jsonFactory;

}