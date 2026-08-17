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
	it('scores every metric it has data for at a hundred when nothing needs attention', () => {
		const {freshness, reliability} = getGovernanceHealth(NO_ISSUES);

		expect(reliability).toBe(100);
		expect(freshness).toBe(100);
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

	it('leaves originality out of the score until its count arrives', () => {
		const {originality, score} = getGovernanceHealth(NO_ISSUES);

		expect(originality).toBeUndefined();
		expect(score).toBe(100);
	});

	it('scores originality as soon as its count arrives', () => {
		const {originality, score} = getGovernanceHealth({
			...NO_ISSUES,
			duplicatedCount: 10,
		});

		expect(originality).toBe(80);
		expect(score).toBe(97);
	});

	it('penalises each draft four points', () => {
		const {flow} = getGovernanceHealth({...NO_ISSUES, inDraftCount: 3});

		expect(flow).toBe(88);
	});

	it('weighs reliability twice as much as freshness', () => {
		const baseline = getGovernanceHealth(NO_ISSUES);

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

		expect(baseline.score - brokenLinksOnly.score).toBe(14);
		expect(baseline.score - expiringOnly.score).toBe(7);
	});

	it('never scores below zero however many issues there are', () => {
		const {reliability, score} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 500,
		});

		expect(reliability).toBe(0);
		expect(score).toBe(53);
	});
});
