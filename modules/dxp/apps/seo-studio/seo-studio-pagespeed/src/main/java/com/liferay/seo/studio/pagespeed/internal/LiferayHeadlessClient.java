/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.net.HttpURLConnection;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * @author Kiana Suetani
 */
public class LiferayHeadlessClient {

	public LiferayHeadlessClient(String authToken, String portalURL) {
		_authToken = authToken;
		_portalURL = portalURL;
	}

	public List<String> getPageURLs(int maxPages) throws Exception {
		if (maxPages <= 0) {
			return new ArrayList<>();
		}

		String protocol = "https://";

		if (_portalURL.startsWith("http://")) {
			protocol = "http://";
		}

		LinkedHashSet<String> urlSet = new LinkedHashSet<>();

		for (String virtualHost : _getVirtualHosts()) {
			_addSitemapURLs(
				maxPages, protocol + virtualHost + "/sitemap.xml", urlSet);

			if (urlSet.size() >= maxPages) {
				break;
			}
		}

		List<String> urls = new ArrayList<>(urlSet);

		if (urls.size() > maxPages) {
			return urls.subList(0, maxPages);
		}

		return urls;
	}

	private void _addSitemapURLs(
			int maxPages, String sitemapURL, LinkedHashSet<String> urlSet)
		throws Exception {

		String sitemapXML = _makeRequest(sitemapURL);

		if (Validator.isNotNull(sitemapXML)) {
			urlSet.addAll(_parseSitemapURLs(0, maxPages, sitemapXML));
		}
	}

	private List<String> _getChildSitemapURLs(Element rootElement) {
		List<String> childSitemapURLs = new ArrayList<>();

		for (Element sitemapElement : rootElement.elements("sitemap")) {
			Element locElement = sitemapElement.element("loc");

			if (locElement != null) {
				childSitemapURLs.add(locElement.getText());
			}
		}

		return childSitemapURLs;
	}

	private List<String> _getVirtualHosts() throws Exception {
		String responseJSON = _makeRequest(
			_portalURL + "/o/headless-portal-instances/v1.0/portal-instances");

		if (Validator.isNull(responseJSON)) {
			return new ArrayList<>();
		}

		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			responseJSON);

		JSONArray itemsJSONArray = responseJSONObject.getJSONArray("items");

		if ((itemsJSONArray == null) || (itemsJSONArray.length() == 0)) {
			return new ArrayList<>();
		}

		List<String> virtualHosts = new ArrayList<>();

		for (int i = 0; i < itemsJSONArray.length(); i++) {
			String virtualHost = itemsJSONArray.getJSONObject(
				i
			).getString(
				"virtualHost"
			);

			if (Validator.isNotNull(virtualHost)) {
				virtualHosts.add(virtualHost);
			}
		}

		return virtualHosts;
	}

	private String _makeRequest(String url) {
		try {
			Http.Options options = new Http.Options();

			if (Validator.isNotNull(_authToken)) {
				options.addHeader("Authorization", "Bearer " + _authToken);
			}

			options.setLocation(url);

			options.setTimeout(30000);

			String response = HttpUtil.URLtoString(options);

			Http.Response httpResponse = options.getResponse();

			if (httpResponse.getResponseCode() != HttpURLConnection.HTTP_OK) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Response code ", httpResponse.getResponseCode(),
							" for ", url));
				}

				return null;
			}

			return response;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to make request to " + url, exception);
			}

			return null;
		}
	}

	private List<String> _parseSitemapURLs(int depth, int maxPages, String xml)
		throws Exception {

		if (depth > _MAX_SITEMAP_DEPTH) {
			if (_log.isDebugEnabled()) {
				_log.debug("Maximum sitemap recursion depth exceeded");
			}

			return new ArrayList<>();
		}

		Document document = SAXReaderUtil.read(xml);

		Element rootElement = document.getRootElement();

		List<String> urls = new ArrayList<>();

		if (Objects.equals(rootElement.getName(), "sitemapindex")) {
			for (String childSitemapURL : _getChildSitemapURLs(rootElement)) {
				String childSitemapXML = _makeRequest(childSitemapURL);

				if (Validator.isNotNull(childSitemapXML)) {
					urls.addAll(
						_parseSitemapURLs(
							depth + 1, maxPages, childSitemapXML));
				}

				if (urls.size() >= maxPages) {
					break;
				}
			}
		}
		else {
			for (Element urlElement : rootElement.elements("url")) {
				Element locElement = urlElement.element("loc");

				if (locElement == null) {
					continue;
				}

				String url = locElement.getText();

				if (Validator.isNotNull(url)) {
					urls.add(url);

					if (urls.size() >= maxPages) {
						break;
					}
				}
			}
		}

		return urls;
	}

	private static final int _MAX_SITEMAP_DEPTH = 3;

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayHeadlessClient.class);

	private final String _authToken;
	private final String _portalURL;

}