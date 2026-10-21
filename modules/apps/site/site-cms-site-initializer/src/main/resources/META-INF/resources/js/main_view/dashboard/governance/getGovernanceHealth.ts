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
	issues: number;
	operations: GovernanceDimension;
	quality: GovernanceDimension;
	score: number;
};

type Signal = {
	count: number;
	scope: number;
	severity: number;
};

const FRESHNESS_WEIGHT = 30;

const OPERATIONS_WEIGHT = 30;

const QUALITY_WEIGHT = 40;

function getScore(score: number, issues: number) {
	return issues ? Math.min(score, 99) : score;
}

function getDimension(signals: Signal[], warnings = 0): GovernanceDimension {
	let issues = 0;
	let penalty = 0;

	for (const {count, scope, severity} of signals) {
		issues += count;

		if (scope) {
			penalty += (count * severity * 100) / scope;
		}
	}

	return {
		issues,
		score: getScore(Math.round(Math.max(0, 100 - penalty)), issues),
		warnings,
	};
}

export default function getGovernanceHealth(
	statistics: AssetStatistics
): GovernanceHealth {
	const assets = statistics.totalCount;
	const tasks = statistics.workflowTasksCount;

	const freshness = getDimension(
		[
			{count: statistics.expiredCount, scope: assets, severity: 1.75},
			{
				count: statistics.reviewDateOverdueCount,
				scope: assets,
				severity: 1.5,
			},
		],
		statistics.expiringSoonCount + statistics.upcomingReviewCount
	);

	const operations = getDimension([
		{
			count: Math.max(
				statistics.overdueWorkflowTasksCount,
				statistics.slaBreachedWorkflowTasksCount
			),
			scope: tasks,
			severity: 2,
		},
		{
			count: statistics.longStandingDraftsCount,
			scope: assets,
			severity: 1.5,
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

	const issues = freshness.issues + operations.issues + quality.issues;

	return {
		freshness,
		issues,
		operations,
		quality,
		score: getScore(
			Math.round(
				(freshness.score * FRESHNESS_WEIGHT +
					operations.score * OPERATIONS_WEIGHT +
					quality.score * QUALITY_WEIGHT) /
					100
			),
			issues
		),
	};
}
