/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.crawler;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.seo.studio.service.SEOStudioService;

import java.net.URI;
import java.net.URISyntaxException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Brooke Dalton
 */
@Component
public class DetectOrphanPagesCrawler {

	public void detect(
			long scanId, long accountEntryId, long seoStudioDomainId,
			URI hostname, String insightType)
		throws Exception {

		String seedURL = SEOStudioService.toDomainURL(hostname);

		long insightTypeId = _findOrCreateInsightTypeId(
			scanId, accountEntryId, insightType);

		String hitsJSON = _seoStudioService.fetchCrawlHits(
			seoStudioDomainId, _detectorMaxDocs);

		JSONObject hitsJSONObject = new JSONObject(hitsJSON);

		JSONArray hitsJSONArray = hitsJSONObject.optJSONArray("items");

		Map<String, String> urlsByNormalizedURL = new HashMap<>();
		Set<String> linkedNormalizedURLs = new HashSet<>();

		for (int i = 0; (hitsJSONArray != null) && (i < hitsJSONArray.length());
			 i++) {

			JSONObject hitJSONObject = hitsJSONArray.getJSONObject(i);

			String url = hitJSONObject.optString("url", null);

			if ((url == null) || url.isBlank()) {
				continue;
			}

			String normalizedURL = _normalize(url);

			urlsByNormalizedURL.putIfAbsent(normalizedURL, url);

			JSONArray linksJSONArray = hitJSONObject.optJSONArray("links");

			if (linksJSONArray != null) {
				for (Object linkObject : linksJSONArray) {
					if (!(linkObject instanceof String)) {
						continue;
					}

					String link = (String)linkObject;

					if (link.isBlank()) {
						continue;
					}

					String normalizedLink = _normalize(link);

					if (!normalizedLink.equals(normalizedURL)) {
						linkedNormalizedURLs.add(normalizedLink);
					}
				}
			}
		}

		Set<String> localePrefixes = _fetchLocalePrefixes();

		Set<String> linkedRemovedLocaleURLs = new HashSet<>();

		for (String linkedNormalizedURL : linkedNormalizedURLs) {
			linkedRemovedLocaleURLs.add(linkedNormalizedURL);
			linkedRemovedLocaleURLs.add(
				_removeLocale(localePrefixes, linkedNormalizedURL));
		}

		String normalizedSeedURL = _normalize(seedURL);

		String seedRemovedLocaleURL = _removeLocale(
			localePrefixes, normalizedSeedURL);

		Map<String, String> orphanURLsByRemovedLocaleURL =
			new LinkedHashMap<>();

		for (String normalizedURL : urlsByNormalizedURL.keySet()) {
			if (normalizedURL.equals(normalizedSeedURL)) {
				continue;
			}

			String removedLocaleURL = _removeLocale(
				localePrefixes, normalizedURL);

			if (removedLocaleURL.equals(seedRemovedLocaleURL) ||
				linkedRemovedLocaleURLs.contains(normalizedURL) ||
				linkedRemovedLocaleURLs.contains(removedLocaleURL)) {

				continue;
			}

			String orphanURL = orphanURLsByRemovedLocaleURL.get(
				removedLocaleURL);

			if ((orphanURL == null) || normalizedURL.equals(removedLocaleURL)) {
				orphanURLsByRemovedLocaleURL.put(
					removedLocaleURL, normalizedURL);
			}
		}

		Collection<String> orphans = orphanURLsByRemovedLocaleURL.values();

		for (String orphan : orphans) {
			JSONObject pageJSONObject = new JSONObject();

			pageJSONObject.put(
				_PAGE_ACCOUNT_JOIN_COLUMN, accountEntryId
			).put(
				_PAGE_SCAN_JOIN_COLUMN, scanId
			).put(
				"pageURL", urlsByNormalizedURL.get(orphan)
			);

			JSONObject pageResponseJSONObject = new JSONObject(
				_seoStudioService.createPage(pageJSONObject));

			long pageId = pageResponseJSONObject.getLong("id");

			JSONObject scanInsightJSONObject = new JSONObject();

			scanInsightJSONObject.put(
				_SCAN_INSIGHT_ACCOUNT_JOIN_COLUMN, accountEntryId
			).put(
				_SCAN_INSIGHT_INSIGHT_TYPE_JOIN_COLUMN, insightTypeId
			).put(
				_SCAN_INSIGHT_PAGE_JOIN_COLUMN, pageId
			).put(
				_SCAN_INSIGHT_SCAN_JOIN_COLUMN, scanId
			).put(
				"classification", "informational"
			).put(
				"detectedDate",
				Instant.now(
				).truncatedTo(
					ChronoUnit.SECONDS
				).toString()
			);

			_seoStudioService.createScanInsight(scanInsightJSONObject);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Wrote ", orphans.size(),
					" orphan page scan insights for insight type ",
					insightTypeId));
		}
	}

	private Set<String> _fetchLocalePrefixes() {
		Set<String> localePrefixes = new HashSet<>();

		JSONObject sitesJSONObject = new JSONObject(
			_seoStudioService.fetchSites());

		JSONArray itemsJSONArray = sitesJSONObject.optJSONArray("items");

		if (itemsJSONArray == null) {
			return localePrefixes;
		}

		for (Object itemObject : itemsJSONArray) {
			JSONObject itemJSONObject = (JSONObject)itemObject;

			JSONObject descriptiveNameJSONObject = itemJSONObject.optJSONObject(
				"descriptiveName_i18n");

			if (descriptiveNameJSONObject == null) {
				continue;
			}

			for (String languageId : descriptiveNameJSONObject.keySet()) {
				String localeId = StringUtil.toLowerCase(languageId);

				localePrefixes.add(localeId);

				int index = localeId.indexOf('-');

				if (index == -1) {
					index = localeId.indexOf('_');
				}

				if (index > 0) {
					localePrefixes.add(localeId.substring(0, index));
				}
			}
		}

		return localePrefixes;
	}

	private long _findOrCreateInsightTypeId(
		long scanId, long accountEntryId, String insightType) {

		String externalReferenceCode = insightType + ":" + scanId;

		try {
			String body = _seoStudioService.findInsightTypeByERC(
				externalReferenceCode);

			JSONObject bodyJSONObject = new JSONObject(body);

			long id = bodyJSONObject.optLong("id", -1);

			if (id > 0) {
				return id;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"No existing insight type for \"" + externalReferenceCode +
						"\"; creating",
					exception);
			}
		}

		JSONObject bodyJSONObject = new JSONObject();

		bodyJSONObject.put(
			_INSIGHT_TYPE_ACCOUNT_JOIN_COLUMN, accountEntryId
		).put(
			_INSIGHT_TYPE_SCAN_JOIN_COLUMN, scanId
		).put(
			"category", "urls"
		).put(
			"externalReferenceCode", externalReferenceCode
		).put(
			"name", "Orphan Page"
		).put(
			"severity", "high"
		);

		JSONObject responseJSONObject = new JSONObject(
			_seoStudioService.createInsightType(bodyJSONObject));

		return responseJSONObject.getLong("id");
	}

	private boolean _isLocalePrefix(
		Set<String> localePrefixes, String segment) {

		return localePrefixes.contains(StringUtil.toLowerCase(segment));
	}

	private String _normalize(String url) {
		if ((url == null) || url.isBlank()) {
			return url;
		}

		URI uri;

		try {
			uri = new URI(url.trim());
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse URL \"" + url + "\"", uriSyntaxException);
			}

			return url.trim();
		}

		StringBuilder sb = new StringBuilder();

		if (uri.getScheme() != null) {
			sb.append(
				uri.getScheme(
				).toLowerCase());
			sb.append("://");
		}

		if (uri.getHost() != null) {
			sb.append(
				uri.getHost(
				).toLowerCase());
		}

		String path = uri.getPath();

		if ((path == null) || path.isEmpty()) {
			sb.append("/");
		}
		else if ((path.length() > 1) && path.endsWith("/")) {
			sb.append(path, 0, path.length() - 1);
		}
		else {
			sb.append(path);
		}

		String rawQuery = uri.getRawQuery();

		if ((rawQuery != null) && !rawQuery.isEmpty()) {
			sb.append("?");
			sb.append(rawQuery);
		}

		return sb.toString();
	}

	private String _removeLocale(
		Set<String> localePrefixes, String normalizedURL) {

		if ((normalizedURL == null) || normalizedURL.isBlank()) {
			return normalizedURL;
		}

		URI uri;

		try {
			uri = new URI(normalizedURL);
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to parse URL \"" + normalizedURL + "\"",
					uriSyntaxException);
			}

			return normalizedURL;
		}

		String path = uri.getPath();

		if ((path == null) || (path.length() <= 1)) {
			return normalizedURL;
		}

		String[] pathSegments = path.split("/");

		if ((pathSegments.length < 2) ||
			!_isLocalePrefix(localePrefixes, pathSegments[1])) {

			return normalizedURL;
		}

		String remainingPath = path.substring(pathSegments[1].length() + 1);

		if (remainingPath.isEmpty()) {
			remainingPath = "/";
		}

		return _normalize(
			StringBundler.concat(
				uri.getScheme(), "://", uri.getHost(), remainingPath));
	}

	private static final String _INSIGHT_TYPE_ACCOUNT_JOIN_COLUMN =
		"r_accountToSEOStudioInsightTypes_accountEntryId";

	private static final String _INSIGHT_TYPE_SCAN_JOIN_COLUMN =
		"r_seoStudioScanToSEOStudioInsightTypes_seoStudioScanId";

	private static final String _PAGE_ACCOUNT_JOIN_COLUMN =
		"r_accountToSEOStudioPages_accountEntryId";

	private static final String _PAGE_SCAN_JOIN_COLUMN =
		"r_seoStudioScanToSEOStudioPages_seoStudioScanId";

	private static final String _SCAN_INSIGHT_ACCOUNT_JOIN_COLUMN =
		"r_accountToSEOStudioScanInsights_accountEntryId";

	private static final String _SCAN_INSIGHT_INSIGHT_TYPE_JOIN_COLUMN =
		"r_seoStudioInsightTypeToScanInsights_seoStudioInsightTypeId";

	private static final String _SCAN_INSIGHT_PAGE_JOIN_COLUMN =
		"r_seoStudioPageToSEOStudioScanInsights_seoStudioPageId";

	private static final String _SCAN_INSIGHT_SCAN_JOIN_COLUMN =
		"r_seoStudioScanToSEOStudioScanInsights_seoStudioScanId";

	private static final Log _log = LogFactory.getLog(
		DetectOrphanPagesCrawler.class);

	@Value("${liferay.seo.studio.detector.max.docs:10000}")
	private int _detectorMaxDocs;

	@Autowired
	private SEOStudioService _seoStudioService;

}