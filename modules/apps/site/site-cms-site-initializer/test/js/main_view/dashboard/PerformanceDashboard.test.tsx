/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import SpaceService from '../../../../src/main/resources/META-INF/resources/js/common/services/SpaceService';
import PerformanceDashboard from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceDashboard';
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
}: {
	analyticsCloudEnabled?: boolean;
} = {}) {
	return render(
		<PerformanceDashboard
			admin={false}
			analyticsEnabled={analyticsCloudEnabled}
			constants={constants}
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

	it('shows the connect to Analytics Cloud state when the instance is not connected', () => {
		renderPerformanceDashboard({analyticsCloudEnabled: false});

		expect(
			screen.getByText('connect-to-liferay-analytics-cloud')
		).toBeInTheDocument();
	});

	it('does not show the connect to Analytics Cloud state when the instance is connected', () => {
		renderPerformanceDashboard();

		expect(
			screen.queryByText('connect-to-liferay-analytics-cloud')
		).not.toBeInTheDocument();
	});
});
