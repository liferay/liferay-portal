/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import GovernanceDashboard from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceDashboard';
import GovernanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/ContentAgeDistribution',
	() => ({ContentAgeDistribution: () => null})
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/Operations',
	() => ({
		Operations: () => null,
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceService'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

const mockedGovernanceService = GovernanceService as jest.Mocked<
	typeof GovernanceService
>;
const mockedSpaceService = SpaceService as jest.Mocked<typeof SpaceService>;

const STATISTICS = {
	approvedCount: 0,
	brokenLinksCount: 7,
	expiredCount: 6,
	expiringSoonCount: 2,
	inDraftCount: 3,
	longStandingDraftsCount: 2,
	missingRequiredFieldsCount: 4,
	overdueWorkflowTasksCount: 2,
	pendingCount: 4,
	reviewDateOverdueCount: 3,
	scheduledCount: 0,
	slaBreachedWorkflowTasksCount: 1,
	totalCount: 100,
	upcomingReviewCount: 0,
	workflowTasksCount: 20,
};

const ADDITIONAL_PROPS = {
	editContentItemURL: '/edit-content-item?objectEntryId={embedded.id}',
	fileMimeTypeCssClasses: {},
	fileMimeTypeIcons: {},
	objectDefinitionCssClasses: {},
	objectDefinitionIcons: {},
} as any;

describe('[CMS Dashboard] GovernanceDashboard', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockedSpaceService.getSpaces.mockResolvedValue([
			{externalReferenceCode: 'ERC_01', id: '01', name: 'space 01'},
			{externalReferenceCode: 'ERC_02', id: '02', name: 'space 02'},
		] as any);

		mockedGovernanceService.getAssetStatistics.mockResolvedValue({
			data: STATISTICS,
			error: null,
		} as any);
	});

	it("shows each card's count from the statistics", async () => {
		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		const freshness = within(
			screen.getByRole('group', {name: 'freshness'})
		);

		expect(
			await within(
				freshness.getByRole('button', {name: /^expired /})
			).findByText('6')
		).toBeInTheDocument();

		expect(
			within(
				freshness.getByRole('button', {name: /^overdue-reviews /})
			).getByText('3')
		).toBeInTheDocument();

		const quality = within(screen.getByRole('group', {name: 'quality'}));

		expect(
			within(
				quality.getByRole('button', {name: /^broken-links /})
			).getByText('7')
		).toBeInTheDocument();
	});

	it('requests all spaces on the initial render', async () => {
		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getAssetStatistics
			).toHaveBeenCalledWith(undefined, expect.any(AbortSignal))
		);
	});

	it('re-fetches the counts scoped by the selected space', async () => {
		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getAssetStatistics
			).toHaveBeenCalledWith(undefined, expect.any(AbortSignal))
		);

		await userEvent.click(
			screen.getByRole('combobox', {name: 'filter-by-spaces'})
		);

		const listbox = await screen.findByRole('listbox');

		await userEvent.click(
			await within(listbox).findByRole('option', {name: 'space 02'})
		);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getAssetStatistics
			).toHaveBeenCalledWith('02', expect.any(AbortSignal))
		);
	});

	it('shows the three sub-scores next to the global score', async () => {
		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		const banner = await screen.findByRole('region', {
			name: 'governance-health',
		});

		const quality = await within(banner).findByText('quality');

		expect(quality.previousSibling).toHaveTextContent('80');
		expect(quality.nextSibling).toHaveTextContent('x-issues');

		const freshness = within(banner).getByText('freshness');

		expect(freshness.previousSibling).toHaveTextContent('85');
		expect(freshness.nextSibling).toHaveTextContent(
			'x-issues · x-warnings'
		);

		const operations = within(banner).getByText('operations');

		expect(operations.previousSibling).toHaveTextContent('77');
		expect(operations.nextSibling).toHaveTextContent('x-issues');

		expect(
			within(banner).getByText('81').parentElement?.nextSibling
		).toHaveTextContent('x-issues');
	});

	it('uses the singular for a single issue or warning', async () => {
		mockedGovernanceService.getAssetStatistics.mockResolvedValue({
			data: {
				...STATISTICS,
				brokenLinksCount: 0,
				expiredCount: 1,
				expiringSoonCount: 1,
				longStandingDraftsCount: 0,
				missingRequiredFieldsCount: 0,
				overdueWorkflowTasksCount: 0,
				reviewDateOverdueCount: 0,
				slaBreachedWorkflowTasksCount: 0,
			},
			error: null,
		} as any);

		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		const banner = await screen.findByRole('region', {
			name: 'governance-health',
		});

		const freshness = await within(banner).findByText('freshness');

		expect(freshness.nextSibling).toHaveTextContent('x-issue · x-warning');
	});

	it('explains what the score measures in a popover', async () => {
		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		const button = await screen.findByRole('button', {
			name: 'about-governance-health',
		});

		expect(
			screen.queryByText('governance-health-help')
		).not.toBeInTheDocument();

		await userEvent.click(button);

		expect(
			await screen.findByText('governance-health-help')
		).toBeInTheDocument();

		expect(
			screen.getByText('governance-health-issues-help')
		).toBeInTheDocument();

		expect(
			screen.getByText('governance-health-warnings-help')
		).toBeInTheDocument();
	});

	it('closes the popover with the escape key', async () => {
		render(<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />);

		const button = await screen.findByRole('button', {
			name: 'about-governance-health',
		});

		await userEvent.click(button);

		expect(button).toHaveAttribute('aria-expanded', 'true');

		await userEvent.keyboard('{Escape}');

		await waitFor(() =>
			expect(button).toHaveAttribute('aria-expanded', 'false')
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = render(
			<GovernanceDashboard additionalProps={ADDITIONAL_PROPS} />
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
