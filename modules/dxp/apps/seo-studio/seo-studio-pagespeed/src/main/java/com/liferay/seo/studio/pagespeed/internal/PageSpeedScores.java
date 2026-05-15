/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.pagespeed.internal;

/**
 * @author Kiana Suetani
 */
public class PageSpeedScores {

	public PageSpeedScores(
		int accessibility, int bestPractices, int performance, int seo) {

		_accessibility = accessibility;
		_bestPractices = bestPractices;
		_performance = performance;
		_seo = seo;
	}

	public int getAccessibility() {
		return _accessibility;
	}

	public int getBestPractices() {
		return _bestPractices;
	}

	public int getPerformance() {
		return _performance;
	}

	public int getSeo() {
		return _seo;
	}

	private final int _accessibility;
	private final int _bestPractices;
	private final int _performance;
	private final int _seo;

}