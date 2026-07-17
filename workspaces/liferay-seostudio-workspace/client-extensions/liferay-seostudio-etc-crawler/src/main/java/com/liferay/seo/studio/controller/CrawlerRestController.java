/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.controller;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.constants.SEOStudioScanConstants;
import com.liferay.seo.studio.model.Domain;
import com.liferay.seo.studio.service.KubernetesJobService;
import com.liferay.seo.studio.service.SEOStudioService;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.batch.v1.Job;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Brooke Dalton
 */
@RequestMapping("/crawler")
@RestController
public class CrawlerRestController extends BaseRestController {

	@PostMapping
	public ResponseEntity<String> post(@RequestBody String json) {
		if (_log.isDebugEnabled()) {
			_log.debug(json);
		}

		JSONObject objectEntryJSONObject = new JSONObject(
			json
		).getJSONObject(
			"objectEntry"
		);

		long seoStudioScanId = objectEntryJSONObject.getLong("objectEntryId");

		try {
			JSONObject valuesJSONObject = objectEntryJSONObject.getJSONObject(
				"values");

			long seoStudioDomainId = valuesJSONObject.getLong(
				"r_seoStudioDomainToSEOStudioScans_seoStudioDomainId");

			Domain domain = _seoStudioService.getSEOStudioDomain(
				seoStudioDomainId);

			if (domain == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get a domain for SEO Studio domain ID " +
							seoStudioDomainId);
				}

				return ResponseEntity.ok(
					_seoStudioService.patchSEOStudioScan(
						"Unable to get a domain for SEO Studio domain ID " +
							seoStudioDomainId,
						seoStudioScanId, SEOStudioScanConstants.STATE_FAILED));
			}

			String domainURL = _seoStudioService.toDomainURL(
				_seoStudioService.toCrawlURI(domain.getHostname()));

			String canonicalDomainURL = _resolveCanonicalDomainURL(domainURL);

			if (!canonicalDomainURL.equals(domainURL)) {
				_seoStudioService.patchSEOStudioDomain(
					new JSONObject(
					).put(
						"hostname", canonicalDomainURL
					),
					seoStudioDomainId);
			}

			String sitemapURL = canonicalDomainURL + "/sitemap.xml";

			if (!_isSitemapReachable(sitemapURL)) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to reach the sitemap at " + sitemapURL);
				}

				return ResponseEntity.ok(
					_seoStudioService.patchSEOStudioScan(
						"Unable to reach the sitemap at " + sitemapURL,
						seoStudioScanId, SEOStudioScanConstants.STATE_FAILED));
			}

			_seoStudioService.patchSEOStudioScan(
				null, seoStudioScanId, SEOStudioScanConstants.STATE_RUNNING);

			JSONObject scopeConfigJSONObject = new JSONObject(
				valuesJSONObject.getString("scopeConfig"));

			Job job = _kubernetesJobService.createJob(
				valuesJSONObject.getLong(
					"r_accountToSEOStudioScans_accountEntryId"),
				canonicalDomainURL,
				scopeConfigJSONObject.getInt("maxCrawlDepth"),
				scopeConfigJSONObject.getInt("maxDuration"),
				_seoStudioService.toIndexName(seoStudioDomainId), sitemapURL);

			ObjectMeta objectMeta = job.getMetadata();

			JSONObject seoStudioScanJSONObject = new JSONObject(
			).put(
				"executionId", objectMeta.getName()
			);

			_seoStudioService.patchSEOStudioScan(
				seoStudioScanJSONObject, seoStudioScanId);

			return ResponseEntity.ok(seoStudioScanJSONObject.toString());
		}
		catch (Exception exception) {
			_log.error("Unable to scan the domain", exception);

			return ResponseEntity.ok(
				_seoStudioService.patchSEOStudioScan(
					"Unable to scan the domain: " + exception.getMessage(),
					seoStudioScanId, SEOStudioScanConstants.STATE_FAILED));
		}
	}

	private boolean _isSitemapReachable(String sitemapURL) {
		try {
			HttpResponse<String> httpResponse = _httpClient.send(
				HttpRequest.newBuilder(
					URI.create(sitemapURL)
				).timeout(
					Duration.ofSeconds(60)
				).GET(
				).build(),
				HttpResponse.BodyHandlers.ofString());

			if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
				return false;
			}

			return Validator.isNotNull(httpResponse.body());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to reach the sitemap at " + sitemapURL, exception);
			}

			return false;
		}
	}

	private String _resolveCanonicalDomainURL(String domainURL)
		throws Exception {

		HttpResponse<Void> httpResponse = _httpClient.send(
			HttpRequest.newBuilder(
				URI.create(domainURL)
			).timeout(
				Duration.ofSeconds(60)
			).GET(
			).build(),
			HttpResponse.BodyHandlers.discarding());

		URI uri = httpResponse.uri();

		if ((uri == null) || Validator.isNull(uri.getHost())) {
			return domainURL;
		}

		return _seoStudioService.toDomainURL(uri);
	}

	private static final Log _log = LogFactory.getLog(
		CrawlerRestController.class);

	private final HttpClient _httpClient = HttpClient.newBuilder(
	).connectTimeout(
		Duration.ofSeconds(5)
	).followRedirects(
		HttpClient.Redirect.NORMAL
	).build();

	@Autowired
	private KubernetesJobService _kubernetesJobService;

	@Autowired
	private SEOStudioService _seoStudioService;

}