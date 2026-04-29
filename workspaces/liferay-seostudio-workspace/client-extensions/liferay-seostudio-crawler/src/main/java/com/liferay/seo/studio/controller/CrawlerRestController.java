/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.controller;

import com.liferay.client.extension.util.spring.boot3.BaseRestController;
import com.liferay.petra.string.StringUtil;
import com.liferay.seo.studio.crawler.DetectOrphanPagesCrawler;
import com.liferay.seo.studio.service.SEOStudioService;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
	public ResponseEntity<String> post(
		@AuthenticationPrincipal Jwt jwt, @RequestBody String json) {

		if (_log.isDebugEnabled()) {
			_log.debug(json);
		}

		JSONObject jsonObject = new JSONObject(json);

		JSONObject objectEntryJSONObject = jsonObject.getJSONObject(
			"objectEntry");

		JSONObject valuesJSONObject = objectEntryJSONObject.getJSONObject(
			"values");

		Path path = null;

		try {
			path = Files.createTempFile("crawler-config", ".yml");

			String crawlerConfig = null;

			try (InputStream inputStream = getClass().getResourceAsStream(
					"/crawler-config-template.yml")) {

				crawlerConfig = new String(
					inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}

			long domainId = valuesJSONObject.getLong(
				"r_seoStudioDomainToSEOStudioScans_seoStudioDomainId");

			String domainJSON = _seoStudioService.fetchDomain(domainId);

			if (domainJSON == null) {
				_log.error("No domain found for domainId " + domainId);

				return new ResponseEntity<>(
					"No domain found for domainId " + domainId,
					HttpStatus.INTERNAL_SERVER_ERROR);
			}

			JSONObject domainJSONObject = new JSONObject(domainJSON);

			URI hostname = SEOStudioService.toCrawlURI(
				domainJSONObject.getString("hostname"));

			String domainUrl = SEOStudioService.toDomainURL(hostname);

			String sitemapURL = domainUrl + "/sitemap.xml";

			if (!_isSitemapReachable(sitemapURL)) {
				return new ResponseEntity<>(
					"Sitemap unavailable at " + sitemapURL + " - aborting scan",
					HttpStatus.UNPROCESSABLE_ENTITY);
			}

			String indexName = SEOStudioService.toIndexName(hostname);

			crawlerConfig = _replace(
				Map.ofEntries(
					Map.entry("[$CRAWLER_DOMAIN_URL$]", domainUrl),
					Map.entry(
						"[$CRAWLER_ELASTICSEARCH_HOST$]",
						_crawlerElasticsearchHost),
					Map.entry(
						"[$CRAWLER_ELASTICSEARCH_PIPELINE$]",
						_crawlerElasticsearchPipeline),
					Map.entry(
						"[$CRAWLER_ELASTICSEARCH_PORT$]",
						String.valueOf(_crawlerElasticsearchPort)),
					Map.entry(
						"[$CRAWLER_LOOPBACK_ALLOWED$]",
						String.valueOf(_crawlerLocalNetworkAllowed)),
					Map.entry(
						"[$CRAWLER_MAX_CRAWL_DEPTH$]",
						String.valueOf(
							valuesJSONObject.optInt("maxCrawlDepth", 1))),
					Map.entry(
						"[$CRAWLER_MAX_DURATION$]",
						String.valueOf(
							valuesJSONObject.optInt("maxDuration", 30))),
					Map.entry("[$CRAWLER_OUTPUT_INDEX$]", indexName),
					Map.entry(
						"[$CRAWLER_PRIVATE_NETWORKS_ALLOWED$]",
						String.valueOf(_crawlerLocalNetworkAllowed)),
					Map.entry("[$CRAWLER_SEED_URL$]", domainUrl),
					Map.entry("[$CRAWLER_SITEMAP_URL$]", sitemapURL),
					Map.entry(
						"[$CRAWLER_URL_QUEUE_SIZE_LIMIT$]",
						String.valueOf(_crawlerUrlQueueSizeLimit))),
				crawlerConfig);

			Files.writeString(path, crawlerConfig, StandardCharsets.UTF_8);

			ProcessBuilder processBuilder = new ProcessBuilder(
				"bundle", "exec", "bin/crawler", "crawl",
				path.toAbsolutePath(
				).toString());

			processBuilder.directory(new File("/opt/liferay/crawler"));

			processBuilder.redirectErrorStream(true);

			if (_log.isInfoEnabled()) {
				_log.info(
					"Launching crawler: " +
						String.join(" ", processBuilder.command()));
			}

			Process process = processBuilder.start();

			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(
						process.getInputStream(), StandardCharsets.UTF_8))) {

				String line;

				while ((line = bufferedReader.readLine()) != null) {
					if (_log.isInfoEnabled()) {
						_log.info("[crawler] " + line);
					}
				}
			}

			int exitCode = process.waitFor();

			if (_log.isInfoEnabled()) {
				_log.info("Crawler finished with exit code " + exitCode);
			}

			if (exitCode == 0) {
				try {
					_detectOrphanPagesCrawler.detect(
						objectEntryJSONObject.getLong("objectEntryId"),
						hostname, "orphan_page");
				}
				catch (Exception exception) {
					_log.error(
						"Orphan page detection failed for scan " +
							objectEntryJSONObject.optLong("objectEntryId", -1),
						exception);
				}

				return ResponseEntity.ok(
				).build();
			}

			return new ResponseEntity<>(
				"Crawler finished with exit code " + exitCode,
				HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch (Exception exception) {
			if (exception instanceof InterruptedException) {
				Thread.currentThread(
				).interrupt();
			}

			_log.error("Crawler execution failed", exception);

			return new ResponseEntity<>(
				"Crawler execution failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		finally {
			if (path != null) {
				try {
					Files.deleteIfExists(path);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to delete temporary crawler config", exception);
				}
			}
		}
	}

	private boolean _isSitemapReachable(String sitemapURL) {
		try {
			HttpClient httpClient = HttpClient.newBuilder(
			).followRedirects(
				HttpClient.Redirect.NORMAL
			).build();

			HttpResponse<String> httpResponse = httpClient.send(
				HttpRequest.newBuilder(
					URI.create(sitemapURL)
				).GET(
				).build(),
				HttpResponse.BodyHandlers.ofString());

			if (httpResponse.statusCode() != 200) {
				return false;
			}

			String body = httpResponse.body();

			if ((body == null) || body.isBlank()) {
				return false;
			}

			return true;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Sitemap probe failed for " + sitemapURL, exception);
			}

			return false;
		}
	}

	private String _replace(Map<String, String> map, String string) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			string = StringUtil.replace(
				string, entry.getKey(), entry.getValue());
		}

		return string;
	}

	private static final Log _log = LogFactory.getLog(
		CrawlerRestController.class);

	@Value("${liferay.seo.studio.crawler.elasticsearch.host}")
	private String _crawlerElasticsearchHost;

	@Value("${liferay.seo.studio.crawler.elasticsearch.pipeline}")
	private String _crawlerElasticsearchPipeline;

	@Value("${liferay.seo.studio.crawler.elasticsearch.port}")
	private int _crawlerElasticsearchPort;

	@Value("${liferay.seo.studio.crawler.local.network.allowed}")
	private boolean _crawlerLocalNetworkAllowed;

	@Value("${liferay.seo.studio.crawler.url.queue.size.limit}")
	private int _crawlerUrlQueueSizeLimit;

	@Autowired
	private DetectOrphanPagesCrawler _detectOrphanPagesCrawler;

	@Autowired
	private SEOStudioService _seoStudioService;

}