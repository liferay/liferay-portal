/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScanResult {

	public static final String STATUS_COMPLETED = "completed";

	public static final String STATUS_FAILED = "failed";

	public static final String STATUS_RUNNING = "running";

	public PageSpeedScanResult(
		PageSpeedScores averageScores, String errorMessage, int pagesErrored,
		int pagesScanned, int pagesTotal, String status) {

		_averageScores = averageScores;
		_errorMessage = errorMessage;
		_pagesErrored = pagesErrored;
		_pagesScanned = pagesScanned;
		_pagesTotal = pagesTotal;
		_status = status;
	}

	public PageSpeedScores getAverageScores() {
		return _averageScores;
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

	public String getStatus() {
		return _status;
	}

	private final PageSpeedScores _averageScores;
	private final String _errorMessage;
	private final int _pagesErrored;
	private final int _pagesScanned;
	private final int _pagesTotal;
	private final String _status;

}