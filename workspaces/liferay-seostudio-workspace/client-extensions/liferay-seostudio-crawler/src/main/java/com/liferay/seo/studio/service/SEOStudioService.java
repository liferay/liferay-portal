/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.service.BaseService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;

import java.net.URI;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Brooke Dalton
 */
@Component
public class SEOStudioService extends BaseService {

	public static URI toCrawlURI(String hostname) {
		if ((hostname == null) || hostname.isBlank()) {
			throw new IllegalArgumentException("Hostname is required");
		}

		String trimmed = StringUtil.toLowerCase(hostname.trim());

		if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
			trimmed = "https://" + trimmed;
		}

		URI uri = URI.create(trimmed);

		if (uri.getHost() == null) {
			throw new IllegalArgumentException(
				"Hostname \"" + hostname + "\" has no host component");
		}

		return uri;
	}

	public static String toDomainURL(URI uri) {
		if (uri.getPort() == -1) {
			return uri.getScheme() + "://" + uri.getHost();
		}

		return StringBundler.concat(
			uri.getScheme(), "://", uri.getHost(), ":", uri.getPort());
	}

	public static String toIndexName(long seoStudioDomainId) {
		return _INDEX_NAME_PREFIX + seoStudioDomainId;
	}

	public String createInsightType(JSONObject jsonObject) {
		return post(
			_authorization(), jsonObject.toString(),
			URI.create(_INSIGHT_TYPES));
	}

	public String createPage(JSONObject jsonObject) {
		return post(
			_authorization(), jsonObject.toString(), URI.create(_PAGES));
	}

	public String createScanInsight(JSONObject jsonObject) {
		return post(
			_authorization(), jsonObject.toString(),
			URI.create(_SCAN_INSIGHTS));
	}

	public String fetchCrawlHits(long seoStudioDomainId, int maxDocs) {
		return get(
			_authorization(),
			URI.create(
				StringBundler.concat(
					"/o/seo-studio/v1.0/seo-studio-domains/", seoStudioDomainId,
					"/crawl-hits?maxDocs=", maxDocs)));
	}

	public String fetchDomain(long domainId) {
		return get(_authorization(), URI.create(_DOMAINS + "/" + domainId));
	}

	public String fetchSites() {
		return get(_authorization(), URI.create(_SITES));
	}

	public String findInsightTypeByERC(String externalReferenceCode) {
		String encodedERC = URLEncoder.encode(
			externalReferenceCode, StandardCharsets.UTF_8);

		return get(
			_authorization(),
			URI.create(
				_INSIGHT_TYPES + "/by-external-reference-code/" + encodedERC));
	}

	public String updateDomain(long domainId, JSONObject jsonObject) {
		return patch(
			_authorization(), jsonObject.toString(),
			URI.create(_DOMAINS + "/" + domainId));
	}

	private String _authorization() {
		return _liferayOAuth2AccessTokenManager.getAuthorization(
			"liferay-seostudio-crawler-oahs");
	}

	private static final String _DOMAINS = "/o/seo-studio/domains";

	private static final String _INDEX_NAME_PREFIX = "seo_studio_";

	private static final String _INSIGHT_TYPES = "/o/seo-studio/insight-types";

	private static final String _PAGES = "/o/seo-studio/pages";

	private static final String _SCAN_INSIGHTS = "/o/seo-studio/scan-insights";

	private static final String _SITES = "/o/headless-admin-site/v1.0/sites";

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

}