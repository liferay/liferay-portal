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
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/NeedsReview',
	() => ({
		NeedsReview: () => null,
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/components/WorkflowAndContentProgress',
	() => ({
		WorkflowAndContentProgress: () => null,
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

const CONSTANTS = {
	ercContentStructures: 'ERC_CONTENT_STRUCTURES',
	ercFileTypes: 'ERC_FILE_TYPES',
};

const STATISTICS = {
	approvedCount: 0,
	brokenLinksCount: 7,
	expiredCount: 6,
	expiringSoonCount: 0,
	inDraftCount: 3,
	pendingCount: 4,
	reviewDateOverdueCount: 3,
	scheduledCount: 0,
	totalCount: 0,
	upcomingReviewCount: 0,
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

		mockedGovernanceService.getCMSEntryClassNames.mockResolvedValue(
			'com.liferay.object.ObjectDefinition#A'
		);

		mockedGovernanceService.getDuplicateTopicsCount.mockResolvedValue(9);
	});

	it("shows each card's count from the statistics", async () => {
		render(
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

		expect(
			await within(
				screen.getByRole('button', {name: /expired-assets/})
			).findByText('6')
		).toBeInTheDocument();

		expect(
			within(
				screen.getByRole('button', {name: /overdue-reviews/})
			).getByText('3')
		).toBeInTheDocument();

		expect(
			within(
				screen.getByRole('button', {name: /pending-workflows/})
			).getByText('4')
		).toBeInTheDocument();

		expect(
			within(
				screen.getByRole('button', {name: /broken-links/})
			).getByText('7')
		).toBeInTheDocument();
	});

	it('requests all spaces on the initial render', async () => {
		render(
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getAssetStatistics
			).toHaveBeenCalledWith(undefined, expect.any(AbortSignal))
		);
	});

	it('re-fetches the counts scoped by the selected space', async () => {
		render(
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

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

	it('shows the four sub-scores next to the global score', async () => {
		render(
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

		const banner = await screen.findByRole('region', {
			name: 'governance-health',
		});

		const flow = await within(banner).findByText('flow');

		expect(flow.previousSibling).toHaveTextContent('88');

		expect(
			within(banner).getByText('originality').previousSibling
		).toHaveTextContent('—');

		expect(
			within(banner).getByText('reliability').previousSibling
		).toHaveTextContent('0');

		expect(
			within(banner).getByText('freshness').previousSibling
		).toHaveTextContent('100');

		expect(within(banner).getByText('49')).toBeInTheDocument();
	});

	it('explains what the score measures in a popover', async () => {
		render(
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

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

		expect(screen.getByText('reliability-help')).toBeInTheDocument();

		expect(screen.getByText('flow-help')).toBeInTheDocument();
	});

	it('closes the popover with the escape key', async () => {
		render(
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

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
			<GovernanceDashboard
				additionalProps={ADDITIONAL_PROPS}
				constants={CONSTANTS}
			/>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
