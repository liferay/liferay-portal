/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AssetStatistics} from './GovernanceService';

export type GovernanceHealth = {
	freshness: number;
	reliability: number;
	score: number;
};

const RELIABILITY_PENALTY = 5;

const RELIABILITY_WEIGHT = 40;

const FRESHNESS_PENALTY = 3;

const FRESHNESS_WEIGHT = 20;

function getSubScore(total: number, penalty: number) {
	return Math.max(0, 100 - total * penalty);
}

export default function getGovernanceHealth(
	statistics: AssetStatistics
): GovernanceHealth {
	const reliability = getSubScore(
		statistics.brokenLinksCount +
			statistics.expiredCount +
			statistics.pendingCount +
			statistics.reviewDateOverdueCount,
		RELIABILITY_PENALTY
	);

	const freshness = getSubScore(
		statistics.expiringSoonCount + statistics.upcomingReviewCount,
		FRESHNESS_PENALTY
	);

	const score = Math.round(
		(reliability * RELIABILITY_WEIGHT + freshness * FRESHNESS_WEIGHT) /
			(RELIABILITY_WEIGHT + FRESHNESS_WEIGHT)
	);

	return {freshness, reliability, score};
}
