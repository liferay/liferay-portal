/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.service;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.seo.studio.constants.PageSpeedConstants;
import com.liferay.seo.studio.model.Domain;
import com.liferay.seo.studio.model.PageSpeedReport;
import com.liferay.seo.studio.model.PageSpeedScanResult;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Kiana Suetani
 */
@Component
public class PageSpeedScanService {

	@Scheduled(fixedDelay = 30000)
	public void scheduledProcessQueuedScans() {
		JSONArray scansJSONArray =
			_liferayService.getQueuedSEOStudioScansJSONArray();

		for (Object object : scansJSONArray) {
			JSONObject scanJSONObject = (JSONObject)object;

			long seoStudioScanId = scanJSONObject.getLong("id");

			try {
				_runScan(scanJSONObject, seoStudioScanId);
			}
			catch (Exception exception1) {
				_log.error(
					"Unable to run the PageSpeed scan " + seoStudioScanId,
					exception1);

				try {
					_liferayService.patchSEOStudioScan(
						exception1.getMessage(), seoStudioScanId,
						PageSpeedConstants.STATE_FAILED);
				}
				catch (Exception exception2) {
					_log.error(
						"Unable to mark the scan " + seoStudioScanId +
							" as failed",
						exception2);
				}
			}
		}
	}

	private PageSpeedReport _getPageSpeedReport(
		String googlePageSpeedAPIKey, String strategy, String url) {

		try {
			return _pageSpeedReportService.getPageSpeedReport(
				googlePageSpeedAPIKey, strategy, url);
		}
		catch (InterruptedException interruptedException) {
			Thread thread = Thread.currentThread();

			thread.interrupt();

			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to complete the PageSpeed scan " + url,
					interruptedException);
			}

			return null;
		}
		catch (IOException ioException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to get PageSpeed scores " + url, ioException);
			}

			return null;
		}
	}

	private PageSpeedScanResult _getPageSpeedScanResult(
		String googlePageSpeedAPIKey, String strategy, List<String> urls) {

		List<CompletableFuture<PageSpeedReport>> completableFutures =
			TransformUtil.transform(
				urls,
				url -> CompletableFuture.supplyAsync(
					() -> _getPageSpeedReport(
						googlePageSpeedAPIKey, strategy, url)));

		int pagesErrored = 0;

		List<PageSpeedReport> pageSpeedReports = new ArrayList<>();

		for (CompletableFuture<PageSpeedReport> completableFuture :
				completableFutures) {

			try {
				PageSpeedReport pageSpeedReport = completableFuture.join();

				if (pageSpeedReport == null) {
					pagesErrored++;
				}
				else {
					pageSpeedReports.add(pageSpeedReport);
				}
			}
			catch (Exception exception) {
				pagesErrored++;

				if (_log.isDebugEnabled()) {
					_log.debug("Unable to add PageSpeed scores", exception);
				}
			}
		}

		int pagesScanned = pageSpeedReports.size();

		if (pagesScanned == 0) {
			return new PageSpeedScanResult(
				new PageSpeedReport(0, 0, 0, 0), pagesErrored, pagesScanned,
				urls.size(), strategy);
		}

		int totalAccessibility = 0;
		int totalBestPractices = 0;
		int totalPerformance = 0;
		int totalSEO = 0;

		for (PageSpeedReport pageSpeedReport : pageSpeedReports) {
			totalAccessibility += pageSpeedReport.getAccessibility();
			totalBestPractices += pageSpeedReport.getBestPractices();
			totalPerformance += pageSpeedReport.getPerformance();
			totalSEO += pageSpeedReport.getSEO();
		}

		PageSpeedReport averagePageSpeedReport = new PageSpeedReport(
			Math.round((float)totalAccessibility / pagesScanned),
			Math.round((float)totalBestPractices / pagesScanned),
			Math.round((float)totalPerformance / pagesScanned),
			Math.round((float)totalSEO / pagesScanned));

		return new PageSpeedScanResult(
			averagePageSpeedReport, pagesErrored, pagesScanned, urls.size(),
			strategy);
	}

	private void _runScan(JSONObject scanJSONObject, long seoStudioScanId) {
		_liferayService.patchSEOStudioScan(
			null, seoStudioScanId, PageSpeedConstants.STATE_RUNNING);

		long seoStudioScanRunId = scanJSONObject.getLong(
			"r_seoStudioScanRunToSEOStudioScans_seoStudioScanRunId");

		JSONObject scanRunJSONObject = _liferayService.getScanRunJSONObject(
			seoStudioScanRunId);

		long domainId = scanRunJSONObject.getLong(
			"r_seoStudioDomainToSEOStudioScanRuns_seoStudioDomainId");

		Domain domain = _liferayService.getDomain(domainId);

		String googlePageSpeedAPIKey = domain.getGooglePageSpeedAPIKey();

		if (Validator.isNull(googlePageSpeedAPIKey)) {
			_liferayService.patchSEOStudioScan(
				"Unable to get a Google PageSpeed API key for SEO Studio " +
					"instance ID " + domain.getSEOStudioInstanceId(),
				seoStudioScanId, PageSpeedConstants.STATE_FAILED);

			return;
		}

		String scopeConfig = scanJSONObject.optString("scopeConfig");

		JSONObject scopeConfigJSONObject = new JSONObject(scopeConfig);

		int maxPagesPerScan = scopeConfigJSONObject.optInt(
			"maxPagesPerScan", 100);

		List<String> urls = _liferayService.getSitemapPageURLs(
			domain.getHostname(), maxPagesPerScan);

		PageSpeedScanResult desktopPageSpeedScanResult =
			_getPageSpeedScanResult(
				googlePageSpeedAPIKey, PageSpeedConstants.STRATEGY_DESKTOP,
				urls);

		_liferayService.postSEOStudioPageSpeedResult(
			desktopPageSpeedScanResult, seoStudioScanId);

		PageSpeedScanResult mobilePageSpeedScanResult = _getPageSpeedScanResult(
			googlePageSpeedAPIKey, PageSpeedConstants.STRATEGY_MOBILE, urls);

		_liferayService.postSEOStudioPageSpeedResult(
			mobilePageSpeedScanResult, seoStudioScanId);

		_liferayService.patchSEOStudioScan(
			null, seoStudioScanId, PageSpeedConstants.STATE_COMPLETED);
	}

	private static final Log _log = LogFactory.getLog(
		PageSpeedScanService.class);

	@Autowired
	private LiferayService _liferayService;

	@Autowired
	private PageSpeedReportService _pageSpeedReportService;

}