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
	longStandingDraftsCount: 0,
	missingRequiredFieldsCount: 0,
	overdueWorkflowTasksCount: 0,
	pendingCount: 0,
	reviewDateOverdueCount: 0,
	scheduledCount: 0,
	slaBreachedWorkflowTasksCount: 0,
	totalCount: 100,
	upcomingReviewCount: 0,
	workflowTasksCount: 0,
};

describe('getGovernanceHealth', () => {
	it('scores every metric it has data for at a hundred when nothing needs attention', () => {
		const {freshness, quality} = getGovernanceHealth(NO_ISSUES);

		expect(quality).toEqual({issues: 0, score: 100});
		expect(freshness).toBe(100);
	});

	it('penalises broken links by their share of the scope with severity two', () => {
		const {quality} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 5,
			totalCount: 20,
		});

		expect(quality).toEqual({issues: 5, score: 50});
	});

	it('penalises missing required data by their share of the scope with severity one and a half', () => {
		const {quality} = getGovernanceHealth({
			...NO_ISSUES,
			missingRequiredFieldsCount: 10,
		});

		expect(quality).toEqual({issues: 10, score: 85});
	});

	it('adds up every quality signal before subtracting it from a hundred', () => {
		const {quality} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 7,
			missingRequiredFieldsCount: 2,
		});

		expect(quality).toEqual({issues: 9, score: 83});
	});

	it('scores quality at a hundred when the scope has no assets', () => {
		const {quality} = getGovernanceHealth({...NO_ISSUES, totalCount: 0});

		expect(quality.score).toBe(100);
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

	it('weighs quality twice as much as freshness', () => {
		const baseline = getGovernanceHealth(NO_ISSUES);

		const brokenLinksOnly = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 15,
		});

		const expiringOnly = getGovernanceHealth({
			...NO_ISSUES,
			expiringSoonCount: 10,
		});

		expect(brokenLinksOnly.quality.score).toBe(70);
		expect(expiringOnly.freshness).toBe(70);

		expect(baseline.score - brokenLinksOnly.score).toBe(14);
		expect(baseline.score - expiringOnly.score).toBe(7);
	});

	it('never scores below zero however many issues there are', () => {
		const {quality, score} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 500,
		});

		expect(quality.score).toBe(0);
		expect(score).toBe(53);
	});
});
