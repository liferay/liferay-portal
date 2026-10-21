/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AssetStatistics} from './GovernanceService';

export type GovernanceDimension = {
	issues: number;
	score: number;
	warnings: number;
};

export type GovernanceHealth = {
	freshness: GovernanceDimension;
	operations: GovernanceDimension;
	quality: GovernanceDimension;
	score: number;
};

type Signal = {
	count: number;
	scope: number;
	severity: number;
	warning?: boolean;
};

const FRESHNESS_WEIGHT = 30;

const OPERATIONS_WEIGHT = 30;

const QUALITY_WEIGHT = 40;

function getDimension(signals: Signal[]): GovernanceDimension {
	let issues = 0;
	let penalty = 0;
	let warnings = 0;

	for (const {count, scope, severity, warning} of signals) {
		if (warning) {
			warnings += count;
		}
		else {
			issues += count;
		}

		if (scope) {
			penalty += (count * severity * 100) / scope;
		}
	}

	return {
		issues,
		score: Math.round(Math.max(0, 100 - penalty)),
		warnings,
	};
}

export default function getGovernanceHealth(
	statistics: AssetStatistics
): GovernanceHealth {
	const assets = statistics.totalCount;
	const tasks = statistics.workflowTasksCount;

	const freshness = getDimension([
		{count: statistics.expiredCount, scope: assets, severity: 1.75},
		{
			count: statistics.reviewDateOverdueCount,
			scope: assets,
			severity: 1.5,
		},
		{
			count: statistics.expiringSoonCount,
			scope: assets,
			severity: 0.5,
			warning: true,
		},
		{
			count: statistics.upcomingReviewCount,
			scope: assets,
			severity: 0.5,
			warning: true,
		},
	]);

	const operations = getDimension([
		{
			count: statistics.overdueWorkflowTasksCount,
			scope: tasks,
			severity: 2,
		},
		{
			count: statistics.longStandingDraftsCount,
			scope: assets,
			severity: 1.5,
		},
		{
			count: statistics.slaBreachedWorkflowTasksCount,
			scope: tasks,
			severity: 2,
		},
	]);

	const quality = getDimension([
		{count: statistics.brokenLinksCount, scope: assets, severity: 2},
		{
			count: statistics.missingRequiredFieldsCount,
			scope: assets,
			severity: 1.5,
		},
	]);

	return {
		freshness,
		operations,
		quality,
		score: Math.round(
			(freshness.score * FRESHNESS_WEIGHT +
				operations.score * OPERATIONS_WEIGHT +
				quality.score * QUALITY_WEIGHT) /
				100
		),
	};
}
