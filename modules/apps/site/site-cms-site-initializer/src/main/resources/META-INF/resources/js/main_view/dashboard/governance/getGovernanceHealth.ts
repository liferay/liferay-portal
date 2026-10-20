/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {AssetStatistics} from './GovernanceService';

export type GovernanceDimension = {
	issues: number;
	score: number;
	warnings: number;
};

export type GovernanceHealth = {
	flow: number;
	freshness: GovernanceDimension;
	originality?: number;
	quality: GovernanceDimension;
	score: number;
};

type Signal = {
	count: number;
	severity: number;
	warning?: boolean;
};

type WeightedSubScore = {
	value: number;
	weight: number;
};

const FLOW_PENALTY = 4;

const FLOW_WEIGHT = 25;

const FRESHNESS_WEIGHT = 30;

const ORIGINALITY_PENALTY = 2;

const ORIGINALITY_WEIGHT = 15;

const QUALITY_WEIGHT = 40;

function getDimension(signals: Signal[], scope: number): GovernanceDimension {
	let issues = 0;
	let penalty = 0;
	let warnings = 0;

	for (const {count, severity, warning} of signals) {
		if (warning) {
			warnings += count;
		}
		else {
			issues += count;
		}

		penalty += count * severity;
	}

	if (!scope) {
		return {issues, score: 100, warnings};
	}

	return {
		issues,
		score: Math.round(Math.max(0, 100 - (penalty * 100) / scope)),
		warnings,
	};
}

function getScore(subScores: WeightedSubScore[]) {
	const weight = subScores.reduce((total, subScore) => {
		return total + subScore.weight;
	}, 0);

	const weighted = subScores.reduce((total, subScore) => {
		return total + subScore.value * subScore.weight;
	}, 0);

	return Math.round(weighted / weight);
}

function getSubScore(total: number, penalty: number) {
	return Math.max(0, 100 - total * penalty);
}

export default function getGovernanceHealth(
	statistics: AssetStatistics
): GovernanceHealth {
	const flow = getSubScore(statistics.inDraftCount, FLOW_PENALTY);

	const freshness = getDimension(
		[
			{count: statistics.expiredCount, severity: 1.75},
			{count: statistics.reviewDateOverdueCount, severity: 1.5},
			{count: statistics.expiringSoonCount, severity: 0.5, warning: true},
			{
				count: statistics.upcomingReviewCount,
				severity: 0.5,
				warning: true,
			},
		],
		statistics.totalCount
	);

	const quality = getDimension(
		[
			{count: statistics.brokenLinksCount, severity: 2},
			{count: statistics.missingRequiredFieldsCount, severity: 1.5},
		],
		statistics.totalCount
	);

	const subScores: WeightedSubScore[] = [
		{value: flow, weight: FLOW_WEIGHT},
		{value: freshness.score, weight: FRESHNESS_WEIGHT},
		{value: quality.score, weight: QUALITY_WEIGHT},
	];

	if (isNullOrUndefined(statistics.duplicatedCount)) {
		return {flow, freshness, quality, score: getScore(subScores)};
	}

	const originality = getSubScore(
		statistics.duplicatedCount,
		ORIGINALITY_PENALTY
	);

	subScores.push({value: originality, weight: ORIGINALITY_WEIGHT});

	return {
		flow,
		freshness,
		originality,
		quality,
		score: getScore(subScores),
	};
}
