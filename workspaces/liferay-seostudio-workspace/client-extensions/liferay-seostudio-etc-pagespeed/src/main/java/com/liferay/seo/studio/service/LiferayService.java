/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.constants.PageSpeedConstants;
import com.liferay.seo.studio.model.PageSpeedReport;
import com.liferay.seo.studio.model.PageSpeedScanResult;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Kiana Suetani
 */
@Component
public class LiferayService extends BaseService {

	public JSONArray getQueuedSEOStudioScansJSONArray() {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/c/seostudioscans"
		).queryParam(
			"filter", "state eq '" + PageSpeedConstants.STATE_QUEUED + "'"
		).queryParam(
			"nestedFields", "seoStudioScanRun,seoStudioDomain,seoStudioInstance"
		).queryParam(
			"nestedFieldsDepth", 3
		).queryParam(
			"pageSize", 20
		).build();

		String responseJSON = get(_getAuthorization(), uriComponents.toUri());

		if (Validator.isNull(responseJSON)) {
			return new JSONArray();
		}

		try {
			JSONArray itemsJSONArray = new JSONObject(
				responseJSON
			).optJSONArray(
				"items"
			);

			if (itemsJSONArray == null) {
				return new JSONArray();
			}

			return itemsJSONArray;
		}
		catch (JSONException jsonException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to parse the queued scans response", jsonException);
			}

			return new JSONArray();
		}
	}

	public List<String> getSitemapPageURLs(String hostname, int limit) {
		if (Validator.isNull(hostname)) {
			throw new IllegalArgumentException(
				"Unable to get a sitemap without a hostname");
		}

		if (limit <= 0) {
			throw new IllegalArgumentException(
				"Unable to get a sitemap without a positive limit");
		}

		String sitemapXML = _getSitemapXML(
			"https://" + hostname + "/sitemap.xml");

		if (Validator.isNull(sitemapXML)) {
			throw new IllegalStateException(
				"Unable to get a sitemap for " + hostname);
		}

		return _parseSitemapPageURLs(0, hostname, limit, sitemapXML);
	}

	public String patchSEOStudioScan(
		String errorMessage, long seoStudioScanId, String state) {

		JSONObject jsonObject = new JSONObject();

		if (Validator.isNotNull(errorMessage)) {
			jsonObject.put("errorMessage", errorMessage);
		}

		jsonObject.put("state", state);

		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/c/seostudioscans/" + seoStudioScanId
		).build();

		return patch(
			_getAuthorization(), jsonObject.toString(), uriComponents.toUri());
	}

	public String postSEOStudioPageSpeedResult(
		PageSpeedScanResult pageSpeedScanResult, long seoStudioScanId) {

		PageSpeedReport averagePageSpeedReport =
			pageSpeedScanResult.getAveragePageSpeedReport();

		JSONObject jsonObject = new JSONObject(
		).put(
			"accessibilityScore", averagePageSpeedReport.getAccessibility()
		).put(
			"bestPracticesScore", averagePageSpeedReport.getBestPractices()
		).put(
			"pagesErrored", pageSpeedScanResult.getPagesErrored()
		).put(
			"pagesScanned", pageSpeedScanResult.getPagesScanned()
		).put(
			"pagesTotal", pageSpeedScanResult.getPagesTotal()
		).put(
			"performanceScore", averagePageSpeedReport.getPerformance()
		).put(
			"r_seoStudioScanToSEOStudioPageSpeedResults_seoStudioScanId",
			seoStudioScanId
		).put(
			"seoScore", averagePageSpeedReport.getSEO()
		).put(
			"strategy", pageSpeedScanResult.getStrategy()
		);

		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/c/seostudiopagespeedresults"
		).build();

		return post(
			_getAuthorization(), jsonObject.toString(), uriComponents.toUri());
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-seostudio-etc-pagespeed-oahs");
	}

	private String _getSitemapXML(String url) {
		try {
			HttpResponse<String> httpResponse = _httpClient.send(
				HttpRequest.newBuilder(
				).uri(
					URI.create(url)
				).timeout(
					Duration.ofSeconds(10)
				).GET(
				).build(),
				HttpResponse.BodyHandlers.ofString());

			if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						StringBundler.concat(
							"Unable to get a sitemap ", url, ", HTTP ",
							httpResponse.statusCode()));
				}

				return null;
			}

			return httpResponse.body();
		}
		catch (IllegalArgumentException | IOException exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to get a sitemap " + url, exception);
			}

			return null;
		}
		catch (InterruptedException interruptedException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get a sitemap " + url, interruptedException);
			}

			Thread thread = Thread.currentThread();

			thread.interrupt();

			return null;
		}
	}

	private boolean _isSameHost(String hostname, String urlString) {
		try {
			URI uri = URI.create(urlString);

			String host = uri.getHost();

			if (Validator.isNull(host)) {
				return false;
			}

			String lowerCaseHost = StringUtil.toLowerCase(host);
			String lowerCaseHostname = StringUtil.toLowerCase(hostname);

			if (lowerCaseHost.equals(lowerCaseHostname) ||
				lowerCaseHost.endsWith("." + lowerCaseHostname)) {

				return true;
			}

			return false;
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse URL " + urlString,
					illegalArgumentException);
			}

			return false;
		}
	}

	private List<String> _parseSitemapPageURLs(
		int depth, String hostname, int limit, String sitemapXML) {

		if (limit <= 0) {
			return Collections.emptyList();
		}

		if (depth > 3) {
			if (_log.isDebugEnabled()) {
				_log.debug("Maximum sitemap recursion depth exceeded");
			}

			return Collections.emptyList();
		}

		List<String> urls = new ArrayList<>();

		JsonNode jsonNode = null;

		try {
			jsonNode = _xmlMapper.readTree(sitemapXML);
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse sitemap", ioException);
			}

			return urls;
		}

		if (jsonNode == null) {
			return urls;
		}

		boolean sitemapIndex = jsonNode.has("sitemap");

		JsonNode entriesJsonNode =
			sitemapIndex ? jsonNode.path("sitemap") : jsonNode.path("url");

		List<JsonNode> entryJsonNodes = new ArrayList<>();

		if (entriesJsonNode.isArray()) {
			entriesJsonNode.forEach(entryJsonNodes::add);
		}
		else if (!entriesJsonNode.isMissingNode()) {
			entryJsonNodes.add(entriesJsonNode);
		}

		for (JsonNode entryJsonNode : entryJsonNodes) {
			if (urls.size() >= limit) {
				break;
			}

			JsonNode locJsonNode = entryJsonNode.path("loc");

			String loc = locJsonNode.asText(null);

			if (Validator.isNull(loc)) {
				continue;
			}

			loc = loc.trim();

			if (Validator.isNull(loc)) {
				continue;
			}

			if (sitemapIndex) {
				if (!_isSameHost(hostname, loc)) {
					if (_log.isDebugEnabled()) {
						_log.debug("Ignoring cross host sitemap URL " + loc);
					}

					continue;
				}

				String childSitemapXML = _getSitemapXML(loc);

				if (Validator.isNotNull(childSitemapXML)) {
					urls.addAll(
						_parseSitemapPageURLs(
							depth + 1, hostname, limit - urls.size(),
							childSitemapXML));
				}
			}
			else {
				urls.add(loc);
			}
		}

		return urls;
	}

	private static final Log _log = LogFactory.getLog(LiferayService.class);

	private final HttpClient _httpClient = HttpClient.newBuilder(
	).connectTimeout(
		Duration.ofSeconds(5)
	).followRedirects(
		HttpClient.Redirect.NORMAL
	).build();

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	private final XmlMapper _xmlMapper = new XmlMapper();

}