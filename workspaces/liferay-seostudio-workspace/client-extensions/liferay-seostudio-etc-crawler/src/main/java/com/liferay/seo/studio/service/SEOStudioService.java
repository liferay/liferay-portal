/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.model.CrawlHit;

import java.net.URI;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Brooke Dalton
 */
@Component
public class SEOStudioService extends BaseService {

	public JSONObject fetchSEOStudioDomainJSONObject(long seoStudioDomainId) {
		String responseJSON = _getSEOStudioDomain(seoStudioDomainId);

		if (Validator.isNull(responseJSON)) {
			return null;
		}

		return new JSONObject(responseJSON);
	}

	public String getActiveSEOStudioScans() {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/scans"
		).queryParam(
			"filter", "state in ('queued','running')"
		).queryParam(
			"pageSize", 100
		).build();

		return get(_getAuthorization(), uriComponents.toUri());
	}

	public List<CrawlHit> getCrawlHits(long seoStudioDomainId) {
		List<CrawlHit> crawlHits = new ArrayList<>();

		String lastURL = null;

		while (true) {
			JSONArray itemsJSONArray = new JSONObject(
				_getCrawlHits(lastURL, 2000, seoStudioDomainId)
			).optJSONArray(
				"items"
			);

			if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
				break;
			}

			String previousLastURL = lastURL;

			for (Object object : itemsJSONArray) {
				CrawlHit crawlHit = new CrawlHit((JSONObject)object);

				crawlHits.add(crawlHit);

				lastURL = crawlHit.getURL();
			}

			if (Objects.equals(previousLastURL, lastURL)) {
				break;
			}
		}

		return crawlHits;
	}

	public long getSEOStudioDomainId(JSONObject seoStudioScanJSONObject) {
		long seoStudioScanRunId = seoStudioScanJSONObject.getLong(
			"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId");

		JSONObject seoStudioScanRunJSONObject = new JSONObject(
			_getSEOStudioScanRun(seoStudioScanRunId));

		return seoStudioScanRunJSONObject.getLong(
			"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId");
	}

	public String patchSEOStudioScan(
		JSONObject jsonObject, long seoStudioScanId) {

		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/scans/" + seoStudioScanId
		).build();

		return patch(
			_getAuthorization(), jsonObject.toString(), uriComponents.toUri());
	}

	public String patchSEOStudioScan(
		String errorMessage, long seoStudioScanId, String state) {

		JSONObject jsonObject = new JSONObject();

		if (Validator.isNotNull(errorMessage)) {
			jsonObject.put("errorMessage", errorMessage);
		}

		jsonObject.put("state", state);

		return patchSEOStudioScan(jsonObject, seoStudioScanId);
	}

	public void postSEOStudioScanInsightsBatch(
			long accountEntryId, JSONObject insightJSONObject,
			long seoStudioScanId)
		throws Exception {

		JSONArray pageURLsJSONArray = insightJSONObject.optJSONArray(
			"pageURLs");

		if ((pageURLsJSONArray == null) || pageURLsJSONArray.isEmpty()) {
			return;
		}

		List<String> pageURLs = TransformUtil.transform(
			pageURLsJSONArray.toList(), object -> (String)object);

		_postSEOStudioScanInsightsBatch(
			accountEntryId, insightJSONObject, pageURLs,
			_resolveSEOStudioPageIdsMap(
				accountEntryId, pageURLs, seoStudioScanId),
			seoStudioScanId);
	}

	public URI toCrawlURI(String hostname) {
		if (Validator.isNull(hostname)) {
			throw new IllegalArgumentException("Hostname is required");
		}

		String url = StringUtil.toLowerCase(hostname.trim());

		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "https://" + url;
		}

		URI uri = URI.create(url);

		if (Validator.isNull(uri.getHost())) {
			throw new IllegalArgumentException(
				"URL \"" + url + "\" has no host component");
		}

		return uri;
	}

	public String toDomainURL(URI uri) {
		String host = StringUtil.toLowerCase(uri.getHost());
		String scheme = StringUtil.toLowerCase(uri.getScheme());

		if (uri.getPort() == -1) {
			return scheme + "://" + host;
		}

		return StringBundler.concat(scheme, "://", host, ":", uri.getPort());
	}

	public String toIndexName(long seoStudioDomainId) {
		return "seo_studio_" + seoStudioDomainId;
	}

	private String _getAuthorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-seostudio-etc-crawler-oahs");
	}

	private String _getCrawlHits(
		String lastURL, int pageSize, long seoStudioDomainId) {

		UriComponentsBuilder uriComponentsBuilder =
			UriComponentsBuilder.fromPath(
				"/o/seo-studio/v1.0/seo-studio-domains/" + seoStudioDomainId +
					"/crawl-hits"
			).queryParam(
				"pageSize", pageSize
			);

		if (Validator.isNotNull(lastURL)) {
			uriComponentsBuilder.queryParam("lastURL", lastURL);
		}

		UriComponents uriComponents = uriComponentsBuilder.build();

		uriComponents = uriComponents.encode();

		return get(_getAuthorization(), uriComponents.toUri());
	}

	private String _getSEOStudioDomain(long seoStudioDomainId) {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/domains/" + seoStudioDomainId
		).build();

		return get(_getAuthorization(), uriComponents.toUri());
	}

	private Map<String, Long> _getSEOStudioPageIdsMap(long seoStudioScanId) {
		Map<String, Long> seoStudioPageIdsMap = new HashMap<>();

		int page = 1;

		while (true) {
			JSONArray itemsJSONArray = new JSONObject(
				_getSEOStudioPages(page, 2000, seoStudioScanId)
			).optJSONArray(
				"items"
			);

			if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
				break;
			}

			for (Object object : itemsJSONArray) {
				JSONObject seoStudioPageJSONObject = (JSONObject)object;

				seoStudioPageIdsMap.put(
					seoStudioPageJSONObject.getString("pageURL"),
					seoStudioPageJSONObject.getLong("id"));
			}

			page++;
		}

		return seoStudioPageIdsMap;
	}

	private String _getSEOStudioPages(
		int page, int pageSize, long seoStudioScanId) {

		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/pages"
		).queryParam(
			"filter",
			"r_seoStudioScanToSEOStudioPages_seoStudioScanId eq '" +
				seoStudioScanId + "'"
		).queryParam(
			"page", page
		).queryParam(
			"pageSize", pageSize
		).build();

		return get(_getAuthorization(), uriComponents.toUri());
	}

	private String _getSEOStudioScanRun(long seoStudioScanRunId) {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/scan-runs/" + seoStudioScanRunId
		).build();

		return get(_getAuthorization(), uriComponents.toUri());
	}

	private String _postSEOStudioInsightType(JSONObject jsonObject) {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/insight-types"
		).build();

		return post(
			_getAuthorization(), jsonObject.toString(), uriComponents.toUri());
	}

	private String _postSEOStudioPagesBatch(JSONArray jsonArray) {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/pages/batch"
		).build();

		return post(
			_getAuthorization(), jsonArray.toString(), uriComponents.toUri());
	}

	private void _postSEOStudioPagesBatch(
		long accountEntryId, List<String> pageURLs, long seoStudioScanId) {

		for (int i = 0; i < pageURLs.size(); i += _BATCH_SIZE) {
			JSONArray seoStudioPagesJSONArray = new JSONArray();

			List<String> batchPageURLs = ListUtil.subList(
				pageURLs, i, i + _BATCH_SIZE);

			for (String batchPageURL : batchPageURLs) {
				seoStudioPagesJSONArray.put(
					_toSEOStudioPageJSONObject(
						accountEntryId, batchPageURL, seoStudioScanId));
			}

			_postSEOStudioPagesBatch(seoStudioPagesJSONArray);
		}
	}

	private String _postSEOStudioScanInsightsBatch(JSONArray jsonArray) {
		UriComponents uriComponents = UriComponentsBuilder.fromPath(
			"/o/seo-studio/scan-insights/batch"
		).build();

		return post(
			_getAuthorization(), jsonArray.toString(), uriComponents.toUri());
	}

	private void _postSEOStudioScanInsightsBatch(
		long accountEntryId, JSONObject insightJSONObject,
		List<String> pageURLs, Map<String, Long> seoStudioPageIdsMap,
		long seoStudioScanId) {

		JSONObject seoStudioInsightTypeJSONObject = new JSONObject(
			_postSEOStudioInsightType(
				_toSEOStudioInsightTypeJSONObject(
					accountEntryId, insightJSONObject, seoStudioScanId)));

		long seoStudioInsightTypeId = seoStudioInsightTypeJSONObject.getLong(
			"id");

		String detectedDateString = Instant.now(
		).truncatedTo(
			ChronoUnit.SECONDS
		).toString();

		for (int i = 0; i < pageURLs.size(); i += _BATCH_SIZE) {
			JSONArray seoStudioScanInsightsJSONArray = new JSONArray();

			List<String> batchPageURLs = ListUtil.subList(
				pageURLs, i, i + _BATCH_SIZE);

			for (String batchPageURL : batchPageURLs) {
				Long seoStudioPageId = seoStudioPageIdsMap.get(batchPageURL);

				if (seoStudioPageId == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to get a page for URL " + batchPageURL);
					}

					continue;
				}

				seoStudioScanInsightsJSONArray.put(
					_toSEOStudioScanInsightJSONObject(
						accountEntryId,
						insightJSONObject.getString("classification"),
						detectedDateString, seoStudioInsightTypeId,
						seoStudioPageId, seoStudioScanId));
			}

			if (seoStudioScanInsightsJSONArray.isEmpty()) {
				continue;
			}

			_postSEOStudioScanInsightsBatch(seoStudioScanInsightsJSONArray);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Posted ", pageURLs.size(), " ",
					insightJSONObject.getString("name"),
					" SEO Studio scan insights for SEO Studio insight type ID ",
					seoStudioInsightTypeId));
		}
	}

	private Map<String, Long> _resolveSEOStudioPageIdsMap(
			long accountEntryId, List<String> pageURLs, long seoStudioScanId)
		throws Exception {

		Map<String, Long> seoStudioPageIdsMap = _getSEOStudioPageIdsMap(
			seoStudioScanId);

		List<String> missingPageURLs = ListUtil.filter(
			pageURLs, pageURL -> !seoStudioPageIdsMap.containsKey(pageURL));

		if (ListUtil.isEmpty(missingPageURLs)) {
			return seoStudioPageIdsMap;
		}

		_postSEOStudioPagesBatch(
			accountEntryId, missingPageURLs, seoStudioScanId);

		long deadline = System.currentTimeMillis() + 60000;

		while (true) {
			Set<String> existingPageURLs = seoStudioPageIdsMap.keySet();

			if (existingPageURLs.containsAll(pageURLs)) {
				break;
			}

			if (System.currentTimeMillis() > deadline) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Timed out waiting for pages to be readable for SEO " +
							"Studio scan ID " + seoStudioScanId);
				}

				break;
			}

			Thread.sleep(1000);

			seoStudioPageIdsMap.putAll(
				_getSEOStudioPageIdsMap(seoStudioScanId));
		}

		return seoStudioPageIdsMap;
	}

	private JSONObject _toSEOStudioInsightTypeJSONObject(
		long accountEntryId, JSONObject insightJSONObject,
		long seoStudioScanId) {

		return new JSONObject(
		).put(
			"category", insightJSONObject.getString("category")
		).put(
			"description", insightJSONObject.getString("description")
		).put(
			"externalReferenceCode",
			insightJSONObject.getString("name") + "_" + seoStudioScanId
		).put(
			"fixHint", insightJSONObject.getString("fixHint")
		).put(
			"name", insightJSONObject.getString("name")
		).put(
			"r_accountToSEOStudioInsightTypes_accountEntryId", accountEntryId
		).put(
			"r_seoStudioScanToSEOStudioInsightTypes_seoStudioScanId",
			seoStudioScanId
		).put(
			"severity", insightJSONObject.getString("severity")
		);
	}

	private JSONObject _toSEOStudioPageJSONObject(
		long accountEntryId, String pageURL, long seoStudioScanId) {

		return new JSONObject(
		).put(
			"pageURL", pageURL
		).put(
			"r_accountToSEOStudioPages_accountEntryId", accountEntryId
		).put(
			"r_seoStudioScanToSEOStudioPages_seoStudioScanId", seoStudioScanId
		);
	}

	private JSONObject _toSEOStudioScanInsightJSONObject(
		long accountEntryId, String classification, String detectedDateString,
		long seoStudioInsightTypeId, long seoStudioPageId,
		long seoStudioScanId) {

		return new JSONObject(
		).put(
			"classification", classification
		).put(
			"detectedDate", detectedDateString
		).put(
			"r_accountToSEOStudioScanInsights_accountEntryId", accountEntryId
		).put(
			"r_seoStudioInsightTypeToScanInsights_seoStudioInsightTypeId",
			seoStudioInsightTypeId
		).put(
			"r_seoStudioPageToSEOStudioScanInsights_seoStudioPageId",
			seoStudioPageId
		).put(
			"r_seoStudioScanToSEOStudioScanInsights_seoStudioScanId",
			seoStudioScanId
		);
	}

	private static final int _BATCH_SIZE = 100;

	private static final Log _log = LogFactory.getLog(SEOStudioService.class);

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}