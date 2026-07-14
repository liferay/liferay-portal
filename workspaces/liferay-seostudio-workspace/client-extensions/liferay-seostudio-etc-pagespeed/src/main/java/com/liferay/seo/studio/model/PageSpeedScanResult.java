/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.model;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScanResult {

	public PageSpeedScanResult(
		PageSpeedReport averagePageSpeedReport, String errorMessage,
		int pagesErrored, int pagesScanned, int pagesTotal, String strategy) {

		_averagePageSpeedReport = averagePageSpeedReport;
		_errorMessage = errorMessage;
		_pagesErrored = pagesErrored;
		_pagesScanned = pagesScanned;
		_pagesTotal = pagesTotal;
		_strategy = strategy;
	}

	public PageSpeedReport getAveragePageSpeedReport() {
		return _averagePageSpeedReport;
	}

	public String getErrorMessage() {
		return _errorMessage;
	}

	public int getPagesErrored() {
		return _pagesErrored;
	}

	public int getPagesScanned() {
		return _pagesScanned;
	}

	public int getPagesTotal() {
		return _pagesTotal;
	}

	public String getStrategy() {
		return _strategy;
	}

	private final PageSpeedReport _averagePageSpeedReport;
	private final String _errorMessage;
	private final int _pagesErrored;
	private final int _pagesScanned;
	private final int _pagesTotal;
	private final String _strategy;

}