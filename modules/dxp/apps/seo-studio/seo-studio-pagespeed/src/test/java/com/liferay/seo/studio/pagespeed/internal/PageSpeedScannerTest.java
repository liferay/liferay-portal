/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScannerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testScanURLsWithAllPagesFailed() throws Exception {
		PageSpeedScoreProvider pageSpeedScoreProvider = Mockito.mock(
			PageSpeedScoreProvider.class);

		Mockito.when(
			pageSpeedScoreProvider.getScores(Mockito.anyString())
		).thenThrow(
			new PageSpeedScoreProvider.PageSpeedScoreProviderException(
				"Connection error")
		);

		PageSpeedScanResult pageSpeedScanResult = _scanURLs(
			pageSpeedScoreProvider, _createURLs(3));

		Assert.assertEquals(
			PageSpeedScanResult.STATUS_FAILED, pageSpeedScanResult.getStatus());
		Assert.assertEquals(0, pageSpeedScanResult.getPagesScanned());
		Assert.assertEquals(3, pageSpeedScanResult.getPagesErrored());
		Assert.assertEquals(
			"All pages failed to scan", pageSpeedScanResult.getErrorMessage());
	}

	@Test
	public void testScanURLsWithMixedResults() throws Exception {
		PageSpeedScoreProvider pageSpeedScoreProvider = Mockito.mock(
			PageSpeedScoreProvider.class);

		Mockito.when(
			pageSpeedScoreProvider.getScores("https://example.com/page1")
		).thenReturn(
			new PageSpeedScores(80, 90, 70, 60)
		);

		Mockito.when(
			pageSpeedScoreProvider.getScores("https://example.com/page2")
		).thenThrow(
			new PageSpeedScoreProvider.PageSpeedScoreProviderException(
				"Connection error")
		);

		Mockito.when(
			pageSpeedScoreProvider.getScores("https://example.com/page3")
		).thenReturn(
			new PageSpeedScores(90, 80, 80, 80)
		);

		PageSpeedScanResult pageSpeedScanResult = _scanURLs(
			pageSpeedScoreProvider, _createURLs(3));

		Assert.assertEquals(
			PageSpeedScanResult.STATUS_COMPLETED,
			pageSpeedScanResult.getStatus());
		Assert.assertEquals(2, pageSpeedScanResult.getPagesScanned());
		Assert.assertEquals(1, pageSpeedScanResult.getPagesErrored());
		Assert.assertNull(pageSpeedScanResult.getErrorMessage());

		PageSpeedScores pageSpeedScores =
			pageSpeedScanResult.getAverageScores();

		Assert.assertEquals(85, pageSpeedScores.getAccessibility());
		Assert.assertEquals(85, pageSpeedScores.getBestPractices());
		Assert.assertEquals(75, pageSpeedScores.getPerformance());
		Assert.assertEquals(70, pageSpeedScores.getSeo());
	}

	@Test
	public void testScanURLsWithQuotaExceeded() throws Exception {
		PageSpeedScoreProvider pageSpeedScoreProvider = Mockito.mock(
			PageSpeedScoreProvider.class);

		Mockito.when(
			pageSpeedScoreProvider.getScores("https://example.com/page1")
		).thenReturn(
			new PageSpeedScores(80, 90, 70, 60)
		);

		Mockito.when(
			pageSpeedScoreProvider.getScores("https://example.com/page2")
		).thenThrow(
			new PageSpeedScoreProvider.PageSpeedScoreProviderException(
				JSONUtil.put("error", JSONUtil.put("code", 429)),
				"Quota exceeded")
		);

		Mockito.when(
			pageSpeedScoreProvider.getScores("https://example.com/page3")
		).thenReturn(
			new PageSpeedScores(90, 80, 80, 80)
		);

		PageSpeedScanResult pageSpeedScanResult = _scanURLs(
			pageSpeedScoreProvider, _createURLs(3));

		Assert.assertEquals(
			PageSpeedScanResult.STATUS_COMPLETED,
			pageSpeedScanResult.getStatus());
		Assert.assertTrue(
			pageSpeedScanResult.getErrorMessage(
			).contains(
				"quota exceeded"
			));
	}

	@Test
	public void testScanURLsWithSuccessfulScan() throws Exception {
		PageSpeedScoreProvider pageSpeedScoreProvider = Mockito.mock(
			PageSpeedScoreProvider.class);

		Mockito.when(
			pageSpeedScoreProvider.getScores(Mockito.anyString())
		).thenReturn(
			new PageSpeedScores(80, 90, 70, 60)
		);

		PageSpeedScanResult pageSpeedScanResult = _scanURLs(
			pageSpeedScoreProvider, _createURLs(5));

		Assert.assertEquals(
			PageSpeedScanResult.STATUS_COMPLETED,
			pageSpeedScanResult.getStatus());
		Assert.assertEquals(5, pageSpeedScanResult.getPagesScanned());
		Assert.assertEquals(5, pageSpeedScanResult.getPagesTotal());
		Assert.assertEquals(0, pageSpeedScanResult.getPagesErrored());
		Assert.assertNull(pageSpeedScanResult.getErrorMessage());

		PageSpeedScores pageSpeedScores =
			pageSpeedScanResult.getAverageScores();

		Assert.assertEquals(80, pageSpeedScores.getAccessibility());
		Assert.assertEquals(90, pageSpeedScores.getBestPractices());
		Assert.assertEquals(70, pageSpeedScores.getPerformance());
		Assert.assertEquals(60, pageSpeedScores.getSeo());
	}

	private List<String> _createURLs(int count) {
		List<String> urls = new ArrayList<>();

		for (int i = 1; i <= count; i++) {
			urls.add("https://example.com/page" + i);
		}

		return urls;
	}

	private PageSpeedScanResult _scanURLs(
			PageSpeedScoreProvider pageSpeedScoreProvider, List<String> urls)
		throws Exception {

		PageSpeedScanner pageSpeedScanner = new PageSpeedScanner();

		Method method = PageSpeedScanner.class.getDeclaredMethod(
			"_scanURLs", PageSpeedScoreProvider.class, List.class);

		method.setAccessible(true);

		return (PageSpeedScanResult)method.invoke(
			pageSpeedScanner, pageSpeedScoreProvider, urls);
	}

}