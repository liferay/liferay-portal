/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import {Space} from '../../../../src/main/resources/META-INF/resources/js/common/types/Space';
import PerformanceDashboard from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceDashboard';
import PerformanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceService';
import {mockFetch} from '../../__mocks__/frontend-js-web';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService'
);

const mockedSpaceService = SpaceService as jest.Mocked<typeof SpaceService>;

const constants = {
	cmsGroupId: '123',
	ercContentStructures: 'CONTENT_STRUCTURES',
	ercFileTypes: 'FILE_TYPES',
};

function renderPerformanceDashboard({
	analyticsCloudEnabled = true,
	spaceIds = ['1', '2'],
}: {
	analyticsCloudEnabled?: boolean;
	spaceIds?: string[];
} = {}) {
	return render(
		<PerformanceDashboard
			admin={false}
			analyticsEnabled={analyticsCloudEnabled}
			constants={constants}
			spaceIds={spaceIds}
		/>
	);
}

describe('PerformanceDashboard', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {items: []},
			error: null,
		});

		mockedSpaceService.getSpaces.mockResolvedValue([]);

		mockFetch.mockResolvedValue({
			json: async () => ({
				items: [],
				lastPage: 1,
				page: 1,
				pageSize: 20,
				totalCount: 0,
			}),
			ok: true,
			status: 200,
			text: async () => '',
		} as Response);
	});

	it('shows the connect to Liferay Data Platform state when the instance is not connected', () => {
		renderPerformanceDashboard({analyticsCloudEnabled: false});

		expect(
			screen.getByText('see-how-your-content-performs')
		).toBeInTheDocument();
	});

	it('does not show the connect to Liferay Data Platform state when the instance is connected', () => {
		renderPerformanceDashboard();

		expect(
			screen.queryByText('see-how-your-content-performs')
		).not.toBeInTheDocument();
	});

	it('shows the connect sites state without a button when no space has connected sites', async () => {
		mockedSpaceService.getSpaces.mockResolvedValue([
			{id: 1, name: 'Marketing', siteId: 11},
			{id: 2, name: 'HR', siteId: 22},
		] as Space[]);

		jest.spyOn(PerformanceService, 'getConnectionInfo').mockResolvedValue({
			data: {
				admin: true,
				connectedToAnalyticsCloud: true,
				connectedToSpace: false,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		renderPerformanceDashboard();

		expect(
			await screen.findByText('connect-sites-to-show-data')
		).toBeInTheDocument();
		expect(screen.queryByText('connect-sites')).not.toBeInTheDocument();
		expect(
			screen.queryByLabelText('filter-by-spaces')
		).not.toBeInTheDocument();
	});

	it('shows the dashboard sections when some space has connected sites', async () => {
		mockedSpaceService.getSpaces.mockResolvedValue([
			{id: 1, name: 'Marketing', siteId: 11},
			{id: 2, name: 'HR', siteId: 22},
		] as Space[]);

		jest.spyOn(PerformanceService, 'getConnectionInfo')
			.mockResolvedValueOnce({
				data: {
					admin: true,
					connectedToAnalyticsCloud: true,
					connectedToSpace: false,
					siteSyncedToAnalyticsCloud: true,
				},
				error: null,
			})
			.mockResolvedValueOnce({
				data: {
					admin: true,
					connectedToAnalyticsCloud: true,
					connectedToSpace: true,
					siteSyncedToAnalyticsCloud: true,
				},
				error: null,
			});

		renderPerformanceDashboard();

		expect(
			await screen.findByText('performance-overview')
		).toBeInTheDocument();
		expect(
			screen.queryByText('connect-sites-to-show-data')
		).not.toBeInTheDocument();
		expect(screen.getByLabelText('filter-by-spaces')).toBeInTheDocument();
	});

	it('shows the dashboard sections when there are no spaces', async () => {
		renderPerformanceDashboard();

		expect(
			await screen.findByText('performance-overview')
		).toBeInTheDocument();
	});

	it('ignores the connection info of the spaces the user does not administer', async () => {
		mockedSpaceService.getSpaces.mockResolvedValue([
			{id: 1, name: 'Marketing', siteId: 11},
			{id: 2, name: 'HR', siteId: 22},
		] as Space[]);

		const getConnectionInfo = jest
			.spyOn(PerformanceService, 'getConnectionInfo')
			.mockResolvedValue({
				data: {
					admin: true,
					connectedToAnalyticsCloud: true,
					connectedToSpace: true,
					siteSyncedToAnalyticsCloud: true,
				},
				error: null,
			});

		renderPerformanceDashboard({spaceIds: ['2']});

		await screen.findByText('performance-overview');

		expect(getConnectionInfo).toHaveBeenCalledTimes(1);

		expect(getConnectionInfo).toHaveBeenCalledWith({
			depotEntryGroupId: 22,
		});
	});
});
