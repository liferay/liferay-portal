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
	expiredCount: 6,
	expiringSoonCount: 0,
	inDraftCount: 0,
	pendingCount: 4,
	reviewDateOverdueCount: 3,
	scheduledCount: 0,
	totalCount: 0,
	upcomingReviewCount: 0,
};

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

	it("shows each card's count, from the statistics for the real ones and the placeholder for Broken Links", async () => {
		render(<GovernanceDashboard />);

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
			).getByText('0')
		).toBeInTheDocument();
	});

	it('requests all spaces on the initial render', async () => {
		render(<GovernanceDashboard />);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getAssetStatistics
			).toHaveBeenCalledWith(undefined)
		);
	});

	it('re-fetches the counts scoped by the selected space', async () => {
		render(<GovernanceDashboard />);

		await waitFor(() =>
			expect(
				mockedGovernanceService.getAssetStatistics
			).toHaveBeenCalledWith(undefined)
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
			).toHaveBeenCalledWith('02')
		);
	});

	it('has no accessibility violations', async () => {
		const {container} = render(<GovernanceDashboard />);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
