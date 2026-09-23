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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author Víctor Galán
 */
public class StaticSiteExportDocument {

	public StaticSiteExportDocument(String html, JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;

		_document = Jsoup.parse(html);
	}

	public String getHTML() {
		return _document.outerHtml();
	}

	public Set<String> getURLs() {
		Set<String> urls = new LinkedHashSet<>();

		for (String attributeName : _ATTRIBUTE_NAMES) {
			for (Element element :
					_document.select("[" + attributeName + "]")) {

				urls.add(element.attr(attributeName));
			}
		}

		for (Element element : _document.select("[srcset]")) {
			for (String candidate : _getSrcsetCandidates(element)) {
				urls.add(_getCandidateURL(candidate));
			}
		}

		for (Element element : _document.select("script[type=importmap]")) {
			JSONObject importsJSONObject = _getImportsJSONObject(element);

			if (importsJSONObject == null) {
				continue;
			}

			Iterator<String> iterator = importsJSONObject.keys();

			while (iterator.hasNext()) {
				String url = importsJSONObject.getString(iterator.next());

				if (!url.endsWith(StringPool.SLASH)) {
					urls.add(url);
				}
			}
		}

		return urls;
	}

	public void rewrite(Function<String, String> pathFunction) {
		for (String attributeName : _ATTRIBUTE_NAMES) {
			for (Element element :
					_document.select("[" + attributeName + "]")) {

				String url = element.attr(attributeName);

				String path = pathFunction.apply(url);

				if (path != null) {
					element.attr(attributeName, path);
				}
			}
		}

		for (Element element : _document.select("[srcset]")) {
			List<String> candidates = new ArrayList<>();

			for (String candidate : _getSrcsetCandidates(element)) {
				String url = _getCandidateURL(candidate);

				String path = pathFunction.apply(url);

				if (path != null) {
					candidate = StringUtil.replaceFirst(candidate, url, path);
				}

				candidates.add(candidate);
			}

			element.attr("srcset", StringUtil.merge(candidates, ", "));
		}

		for (Element element : _document.select("script[type=importmap]")) {
			JSONObject jsonObject = _getJSONObject(element);

			if (jsonObject == null) {
				continue;
			}

			JSONObject importsJSONObject = jsonObject.getJSONObject("imports");

			if (importsJSONObject == null) {
				continue;
			}

			Iterator<String> iterator = importsJSONObject.keys();

			while (iterator.hasNext()) {
				String specifier = iterator.next();

				String path = pathFunction.apply(
					importsJSONObject.getString(specifier));

				if (path != null) {
					importsJSONObject.put(specifier, path);
				}
			}

			element.text(jsonObject.toString());
		}
	}

	private String _getCandidateURL(String candidate) {
		String[] candidateParts = StringUtil.split(candidate, CharPool.SPACE);

		return candidateParts[0];
	}

	private JSONObject _getImportsJSONObject(Element element) {
		JSONObject jsonObject = _getJSONObject(element);

		if (jsonObject == null) {
			return null;
		}

		return jsonObject.getJSONObject("imports");
	}

	private JSONObject _getJSONObject(Element element) {
		try {
			return _jsonFactory.createJSONObject(element.data());
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug(jsonException);
			}

			return null;
		}
	}

	private List<String> _getSrcsetCandidates(Element element) {
		List<String> candidates = new ArrayList<>();

		for (String candidate :
				StringUtil.split(element.attr("srcset"), CharPool.COMMA)) {

			candidates.add(StringUtil.trim(candidate));
		}

		return candidates;
	}

	private static final String[] _ATTRIBUTE_NAMES = {
		"href", "poster", "src", "xlink:href"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		StaticSiteExportDocument.class);

	private final Document _document;
	private final JSONFactory _jsonFactory;

}