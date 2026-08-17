/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AssetStatistics} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';
import getGovernanceHealth from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/getGovernanceHealth';

const NO_ISSUES: AssetStatistics = {
	approvedCount: 0,
	brokenLinksCount: 0,
	expiredCount: 0,
	expiringSoonCount: 0,
	inDraftCount: 0,
	pendingCount: 0,
	reviewDateOverdueCount: 0,
	scheduledCount: 0,
	totalCount: 0,
	upcomingReviewCount: 0,
};

describe('getGovernanceHealth', () => {
	it('scores a perfect hundred when nothing needs attention', () => {
		expect(getGovernanceHealth(NO_ISSUES)).toEqual({
			freshness: 100,
			reliability: 100,
			score: 100,
		});
	});

	it('penalises each reliability issue five points', () => {
		const {reliability} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 2,
			expiredCount: 3,
			pendingCount: 4,
			reviewDateOverdueCount: 1,
		});

		expect(reliability).toBe(50);
	});

	it('penalises each freshness issue three points', () => {
		const {freshness} = getGovernanceHealth({
			...NO_ISSUES,
			expiringSoonCount: 4,
			upcomingReviewCount: 3,
		});

		expect(freshness).toBe(79);
	});

	it('weighs reliability twice as much as freshness', () => {
		const brokenLinksOnly = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 6,
		});

		const expiringOnly = getGovernanceHealth({
			...NO_ISSUES,
			expiringSoonCount: 10,
		});

		expect(brokenLinksOnly.reliability).toBe(70);
		expect(expiringOnly.freshness).toBe(70);

		expect(brokenLinksOnly.score).toBe(80);
		expect(expiringOnly.score).toBe(90);
	});

	it('never scores below zero however many issues there are', () => {
		const {reliability, score} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 500,
		});

		expect(reliability).toBe(0);
		expect(score).toBe(33);
	});
});
