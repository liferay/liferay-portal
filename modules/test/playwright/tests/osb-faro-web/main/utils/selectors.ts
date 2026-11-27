/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export enum CardSelectors {
	Acquisition = '.acquisitions-card-root',
	Audience = '.analytics-audience-report-card',
	CohortAnalysis = '.cohort-analysis-card-root',
	Interests = '.interests-card-root',
	Metrics = '.analytics-metrics-card',
	SearchTerms = '.search-terms-card-root',
	SessionsByLocation = '.analytics-locations-card',
	SessionsTechnology = '.analytics-devices-card',
	TopPages = '.top-pages-card-root',
	VisitorsByDayAndTime = '.visitors-by-time-card',
}

export enum SegmentConditions {
	atMostOrAtLeast = '.operator-input:not(.criterion-input):not([data-testid])',
	criteriaCondition = '.criterion-input:not([aria-label="Match Logic"])',
	selectedTime = '[data-testid="clay-select"]',
	timeConjunction = '[data-testid="conjunction-input"]',
}
