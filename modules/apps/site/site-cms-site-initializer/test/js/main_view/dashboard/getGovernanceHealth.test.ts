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
	workflowTasksCount: 20,
};

describe('getGovernanceHealth', () => {
	it('scores every dimension at a hundred when nothing needs attention', () => {
		const {freshness, operations, quality, score} =
			getGovernanceHealth(NO_ISSUES);

		expect(quality).toEqual({issues: 0, score: 100, warnings: 0});
		expect(freshness).toEqual({issues: 0, score: 100, warnings: 0});
		expect(operations).toEqual({issues: 0, score: 100, warnings: 0});
		expect(score).toBe(100);
	});

	it('adds up the issues of every dimension without the warnings', () => {
		const {issues} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 2,
			expiredCount: 3,
			expiringSoonCount: 4,
			longStandingDraftsCount: 1,
		});

		expect(issues).toBe(6);
	});

	it('penalises broken links by their share of the scope with severity two', () => {
		const {quality} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 5,
			totalCount: 20,
		});

		expect(quality).toEqual({issues: 5, score: 50, warnings: 0});
	});

	it('penalises missing required data by their share of the scope with severity one and a half', () => {
		const {quality} = getGovernanceHealth({
			...NO_ISSUES,
			missingRequiredFieldsCount: 10,
		});

		expect(quality).toEqual({issues: 10, score: 85, warnings: 0});
	});

	it('adds up every quality signal before subtracting it from a hundred', () => {
		const {quality} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 7,
			missingRequiredFieldsCount: 2,
		});

		expect(quality).toEqual({issues: 9, score: 83, warnings: 0});
	});

	it('caps the score at ninety-nine while any issue remains', () => {
		const {quality, score} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 1,
			totalCount: 1000,
		});

		expect(quality).toEqual({issues: 1, score: 99, warnings: 0});
		expect(score).toBe(99);
	});

	it('scores quality at a hundred when the scope has no assets', () => {
		const {quality} = getGovernanceHealth({...NO_ISSUES, totalCount: 0});

		expect(quality.score).toBe(100);
	});

	it('counts expired assets and overdue reviews as freshness issues', () => {
		const {freshness} = getGovernanceHealth({
			...NO_ISSUES,
			expiredCount: 4,
			reviewDateOverdueCount: 2,
		});

		expect(freshness).toEqual({issues: 6, score: 90, warnings: 0});
	});

	it('counts expiring soon and upcoming reviews as freshness warnings that do not affect the score', () => {
		const {freshness} = getGovernanceHealth({
			...NO_ISSUES,
			expiringSoonCount: 4,
			upcomingReviewCount: 6,
		});

		expect(freshness).toEqual({issues: 0, score: 100, warnings: 10});
	});

	it('counts overdue tasks and SLA breaches by the worse of the two', () => {
		const {operations} = getGovernanceHealth({
			...NO_ISSUES,
			overdueWorkflowTasksCount: 2,
			slaBreachedWorkflowTasksCount: 1,
		});

		expect(operations).toEqual({issues: 2, score: 80, warnings: 0});
	});

	it('penalises long-standing drafts by their share of the assets', () => {
		const {operations} = getGovernanceHealth({
			...NO_ISSUES,
			longStandingDraftsCount: 4,
		});

		expect(operations).toEqual({issues: 4, score: 94, warnings: 0});
	});

	it('weighs quality more than freshness', () => {
		const baseline = getGovernanceHealth(NO_ISSUES);

		const brokenLinksOnly = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 15,
		});

		const expiredOnly = getGovernanceHealth({
			...NO_ISSUES,
			expiredCount: 12,
			reviewDateOverdueCount: 6,
		});

		expect(brokenLinksOnly.quality.score).toBe(70);
		expect(expiredOnly.freshness.score).toBe(70);

		expect(baseline.score - brokenLinksOnly.score).toBe(12);
		expect(baseline.score - expiredOnly.score).toBe(9);
	});

	it('never scores below zero however many issues there are', () => {
		const {quality, score} = getGovernanceHealth({
			...NO_ISSUES,
			brokenLinksCount: 500,
		});

		expect(quality.score).toBe(0);
		expect(score).toBe(60);
	});
});
