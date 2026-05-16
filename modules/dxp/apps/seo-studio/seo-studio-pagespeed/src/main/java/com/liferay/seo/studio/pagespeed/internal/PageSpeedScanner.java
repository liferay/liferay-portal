/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScanner {

	public PageSpeedScanResult scan(
			String apiKey, String authToken, String portalURL, String strategy)
		throws Exception {

		LiferayHeadlessClient liferayHeadlessClient = new LiferayHeadlessClient(
			authToken, portalURL);

		List<String> urls = liferayHeadlessClient.getPageURLs(_PAGE_LIMIT);

		if (urls.isEmpty()) {
			return new PageSpeedScanResult(
				new PageSpeedScores(0, 0, 0, 0), null, 0, 0, 0,
				PageSpeedScanResult.STATUS_COMPLETED);
		}

		PageSpeedScoreProvider pageSpeedScoreProvider =
			new PageSpeedScoreProvider(apiKey, strategy);

		if (!pageSpeedScoreProvider.isValidConnection()) {
			return new PageSpeedScanResult(
				null, "Google PageSpeed API key is not configured", 0, 0, 0,
				PageSpeedScanResult.STATUS_FAILED);
		}

		return _scanURLs(pageSpeedScoreProvider, urls);
	}

	private PageSpeedScores _computeAverageScores(
		int count, int totalAccessibility, int totalBestPractices,
		int totalPerformance, int totalSeo) {

		if (count == 0) {
			return new PageSpeedScores(0, 0, 0, 0);
		}

		return new PageSpeedScores(
			Math.round((float)totalAccessibility / count),
			Math.round((float)totalBestPractices / count),
			Math.round((float)totalPerformance / count),
			Math.round((float)totalSeo / count));
	}

	private PageSpeedScanResult _scanURLs(
		PageSpeedScoreProvider pageSpeedScoreProvider, List<String> urls) {

		int pagesTotal = urls.size();

		AtomicBoolean quotaExceeded = new AtomicBoolean(false);
		AtomicInteger pagesErrored = new AtomicInteger(0);
		AtomicInteger pagesScanned = new AtomicInteger(0);
		AtomicInteger totalAccessibility = new AtomicInteger(0);
		AtomicInteger totalBestPractices = new AtomicInteger(0);
		AtomicInteger totalPerformance = new AtomicInteger(0);
		AtomicInteger totalSeo = new AtomicInteger(0);

		ExecutorService executorService = Executors.newFixedThreadPool(
			_WORKER_COUNT);

		List<Future<?>> futures = new ArrayList<>();

		for (String url : urls) {
			futures.add(
				executorService.submit(
					() -> {
						if (quotaExceeded.get()) {
							return;
						}

						try {
							PageSpeedScores pageSpeedScores =
								pageSpeedScoreProvider.getScores(url);

							totalAccessibility.addAndGet(
								pageSpeedScores.getAccessibility());
							totalBestPractices.addAndGet(
								pageSpeedScores.getBestPractices());
							totalPerformance.addAndGet(
								pageSpeedScores.getPerformance());
							totalSeo.addAndGet(pageSpeedScores.getSeo());

							pagesScanned.incrementAndGet();
						}
						catch (PageSpeedScoreProvider.
									PageSpeedScoreProviderException
										pageSpeedScoreProviderException) {

							if (pageSpeedScoreProviderException.
									isQuotaExceeded()) {

								quotaExceeded.set(true);
							}
							else {
								pagesErrored.incrementAndGet();

								if (_log.isDebugEnabled()) {
									_log.debug(
										"Unable to get PageSpeed scores for " +
											url,
										pageSpeedScoreProviderException);
								}
							}
						}
					}));
		}

		executorService.shutdown();

		for (Future<?> future : futures) {
			try {
				future.get();
			}
			catch (Exception exception) {
				pagesErrored.incrementAndGet();

				if (_log.isDebugEnabled()) {
					_log.debug(
						"Unable to complete PageSpeed scan task", exception);
				}
			}
		}

		String errorMessage = null;
		String status = PageSpeedScanResult.STATUS_COMPLETED;

		int scanned = pagesScanned.get();

		if (quotaExceeded.get()) {
			errorMessage = StringBundler.concat(
				"Google PageSpeed API quota exceeded after scanning ", scanned,
				" of ", pagesTotal, " pages");
		}

		if ((scanned == 0) && (pagesErrored.get() > 0)) {
			errorMessage = "All pages failed to scan";
			status = PageSpeedScanResult.STATUS_FAILED;
		}

		return new PageSpeedScanResult(
			_computeAverageScores(
				scanned, totalAccessibility.get(), totalBestPractices.get(),
				totalPerformance.get(), totalSeo.get()),
			errorMessage, pagesErrored.get(), scanned, pagesTotal, status);
	}

	private static final int _PAGE_LIMIT = 100;

	private static final int _WORKER_COUNT = 5;

	private static final Log _log = LogFactoryUtil.getLog(
		PageSpeedScanner.class);

}