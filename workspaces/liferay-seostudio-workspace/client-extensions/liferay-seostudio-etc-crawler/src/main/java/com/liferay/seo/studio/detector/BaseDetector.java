/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.detector;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.seo.studio.model.CrawlHit;
import com.liferay.seo.studio.service.SEOStudioService;

import java.net.URI;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Noor Najjar
 */
public abstract class BaseDetector {

	public abstract void detect(
			long accountEntryId, List<CrawlHit> crawlHits, URI crawlURI,
			long seoStudioScanId)
		throws Exception;

	protected void postInsights(
			long accountEntryId, JSONObject definitionJSONObject,
			List<String> pageURLs, Map<String, Long> pageURLToPageIdMap,
			long seoStudioScanId)
		throws Exception {

		if (ListUtil.isEmpty(pageURLs)) {
			return;
		}

		long seoStudioInsightTypeId = _postInsightType(
			accountEntryId, definitionJSONObject, seoStudioScanId);

		_postScanInsights(
			accountEntryId, definitionJSONObject.getString("classification"),
			pageURLs, pageURLToPageIdMap, seoStudioInsightTypeId,
			seoStudioScanId);

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Posted ", pageURLs.size(), " ",
					definitionJSONObject.getString("name"),
					" scan insights for insight type ",
					seoStudioInsightTypeId));
		}
	}

	protected Map<String, Long> resolvePageIds(
			long accountEntryId, List<String> pageURLs, long seoStudioScanId)
		throws Exception {

		Map<String, Long> pageURLToPageIdMap = _getPages(seoStudioScanId);

		List<String> missingPageURLs = ListUtil.filter(
			pageURLs, pageURL -> !pageURLToPageIdMap.containsKey(pageURL));

		if (ListUtil.isEmpty(missingPageURLs)) {
			return pageURLToPageIdMap;
		}

		_postPages(accountEntryId, missingPageURLs, seoStudioScanId);

		long deadline = System.currentTimeMillis() + 60000;

		while (true) {
			Set<String> existingPageURLs = pageURLToPageIdMap.keySet();

			if (existingPageURLs.containsAll(pageURLs)) {
				break;
			}

			if (System.currentTimeMillis() > deadline) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Timed out waiting for pages to be readable for scan " +
							seoStudioScanId);
				}

				break;
			}

			Thread.sleep(1000);

			pageURLToPageIdMap.putAll(_getPages(seoStudioScanId));
		}

		return pageURLToPageIdMap;
	}

	@Autowired
	protected SEOStudioService seoStudioService;

	private Map<String, Long> _getPages(long seoStudioScanId) {
		Map<String, Long> pageURLToPageIdMap = new HashMap<>();

		int page = 1;

		while (true) {
			JSONArray itemsJSONArray = new JSONObject(
				seoStudioService.getPage(page, 2000, seoStudioScanId)
			).optJSONArray(
				"items"
			);

			if ((itemsJSONArray == null) || itemsJSONArray.isEmpty()) {
				break;
			}

			for (Object object : itemsJSONArray) {
				JSONObject itemJSONObject = (JSONObject)object;

				pageURLToPageIdMap.put(
					itemJSONObject.getString("pageURL"),
					itemJSONObject.getLong("id"));
			}

			page++;
		}

		return pageURLToPageIdMap;
	}

	private long _postInsightType(
		long accountEntryId, JSONObject definitionJSONObject,
		long seoStudioScanId) {

		return new JSONObject(
			seoStudioService.postInsightType(
				new JSONObject(
				).put(
					"category", definitionJSONObject.getString("category")
				).put(
					"description", definitionJSONObject.optString("description")
				).put(
					"externalReferenceCode",
					definitionJSONObject.getString("name") + "_" +
						seoStudioScanId
				).put(
					"fixHint", definitionJSONObject.optString("fixHint")
				).put(
					"name", definitionJSONObject.getString("name")
				).put(
					"r_accountToSEOStudioInsightTypes_accountEntryId",
					accountEntryId
				).put(
					"r_seoStudioScanToSEOStudioInsightTypes_seoStudioScanId",
					seoStudioScanId
				).put(
					"severity", definitionJSONObject.getString("severity")
				))
		).getLong(
			"id"
		);
	}

	private void _postPages(
			long accountEntryId, List<String> pageURLs, long seoStudioScanId)
		throws Exception {

		for (int i = 0; i < pageURLs.size(); i += _BATCH_SIZE) {
			JSONArray pagesJSONArray = new JSONArray();

			List<String> batchPageURLs = pageURLs.subList(
				i, Math.min(i + _BATCH_SIZE, pageURLs.size()));

			for (String pageURL : batchPageURLs) {
				pagesJSONArray.put(
					_toPageJSONObject(
						accountEntryId, pageURL, seoStudioScanId));
			}

			seoStudioService.postPagesBatch(pagesJSONArray);
		}
	}

	private void _postScanInsights(
			long accountEntryId, String classification, List<String> pageURLs,
			Map<String, Long> pageURLToPageIdMap, long seoStudioInsightTypeId,
			long seoStudioScanId)
		throws Exception {

		String detectedDateString = Instant.now(
		).truncatedTo(
			ChronoUnit.SECONDS
		).toString();

		for (int i = 0; i < pageURLs.size(); i += _BATCH_SIZE) {
			JSONArray scanInsightsJSONArray = new JSONArray();

			List<String> batchPageURLs = pageURLs.subList(
				i, Math.min(i + _BATCH_SIZE, pageURLs.size()));

			for (String pageURL : batchPageURLs) {
				Long seoStudioPageId = pageURLToPageIdMap.get(pageURL);

				if (seoStudioPageId == null) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to find a page for URL " + pageURL +
								"; skipping scan insight");
					}

					continue;
				}

				scanInsightsJSONArray.put(
					_toScanInsightJSONObject(
						accountEntryId, classification, detectedDateString,
						seoStudioInsightTypeId, seoStudioPageId,
						seoStudioScanId));
			}

			if (scanInsightsJSONArray.isEmpty()) {
				continue;
			}

			seoStudioService.postScanInsightsBatch(scanInsightsJSONArray);
		}
	}

	private JSONObject _toPageJSONObject(
		long accountEntryId, String pageURL, long seoStudioScanId) {

		JSONObject pageJSONObject = new JSONObject();

		pageJSONObject.put(
			"pageURL", pageURL
		).put(
			"r_accountToSEOStudioPages_accountEntryId", accountEntryId
		).put(
			"r_seoStudioScanToSEOStudioPages_seoStudioScanId", seoStudioScanId
		);

		return pageJSONObject;
	}

	private JSONObject _toScanInsightJSONObject(
		long accountEntryId, String classification, String detectedDateString,
		long seoStudioInsightTypeId, long seoStudioPageId,
		long seoStudioScanId) {

		JSONObject scanInsightJSONObject = new JSONObject();

		scanInsightJSONObject.put(
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

		return scanInsightJSONObject;
	}

	private static final int _BATCH_SIZE = 100;

	private static final Log _log = LogFactory.getLog(BaseDetector.class);

}