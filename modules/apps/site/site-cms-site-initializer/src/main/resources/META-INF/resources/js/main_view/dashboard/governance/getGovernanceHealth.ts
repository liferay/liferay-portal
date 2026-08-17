/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {AssetStatistics} from './GovernanceService';

export type GovernanceHealth = {
	flow: number;
	freshness: number;
	originality?: number;
	reliability: number;
	score: number;
};

type WeightedSubScore = {
	value: number;
	weight: number;
};

const FLOW_PENALTY = 4;

const FLOW_WEIGHT = 25;

const FRESHNESS_PENALTY = 3;

const FRESHNESS_WEIGHT = 20;

const ORIGINALITY_PENALTY = 2;

const ORIGINALITY_WEIGHT = 15;

const RELIABILITY_PENALTY = 5;

const RELIABILITY_WEIGHT = 40;

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

	const freshness = getSubScore(
		statistics.expiringSoonCount + statistics.upcomingReviewCount,
		FRESHNESS_PENALTY
	);

	const reliability = getSubScore(
		statistics.brokenLinksCount +
			statistics.expiredCount +
			statistics.pendingCount +
			statistics.reviewDateOverdueCount,
		RELIABILITY_PENALTY
	);

	const subScores: WeightedSubScore[] = [
		{value: flow, weight: FLOW_WEIGHT},
		{value: freshness, weight: FRESHNESS_WEIGHT},
		{value: reliability, weight: RELIABILITY_WEIGHT},
	];

	if (isNullOrUndefined(statistics.duplicatedCount)) {
		return {flow, freshness, reliability, score: getScore(subScores)};
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
		reliability,
		score: getScore(subScores),
	};
}
