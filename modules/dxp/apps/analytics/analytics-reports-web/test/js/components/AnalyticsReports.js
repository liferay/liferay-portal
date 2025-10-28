/* eslint-disable sort-keys */

/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import AnalyticsReports from '../../../src/main/resources/META-INF/resources/js/components/AnalyticsReports';
import APIService from '../../../src/main/resources/META-INF/resources/js/utils/APIService';

jest.mock('../../../src/main/resources/META-INF/resources/js/utils/APIService');
APIService.getAnalyticsReportsData.mockResolvedValue({
	context: {
		analyticsData: {
			isSynced: false,
			hasValidConnection: false,
			cloudTrialURL:
				'https://www.liferay.com/products/analytics-cloud/get-started',
			url: 'https://analytics-uat.liferay.com/workspace/253875',
		},
		hideAnalyticsReportsPanelURL: 'http://example.com',
		languageTag: 'en-US',
		page: {
			plid: 4,
		},
		pathToAssets: '/o/analytics-reports-web',
	},
});

describe('AnalyticsReports', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('fetches data when the panel is open', async () => {
		render(
			<AnalyticsReports
				analyticsReportsDataURL="https://localhost:8080/api"
				hoverOrFocusEventTriggered={false}
				isPanelStateOpen={true}
			/>
		);

		expect(
			await screen.findByText('connect-to-liferay-analytics-cloud')
		).toBeInTheDocument();

		expect(APIService.getAnalyticsReportsData).toHaveBeenCalled();
	});

	it('does not fetch data when the panel is open', async () => {
		render(
			<AnalyticsReports
				analyticsReportsDataURL="https://localhost:8080/api"
				hoverOrFocusEventTriggered={false}
				isPanelStateOpen={false}
			/>
		);

		expect(APIService.getAnalyticsReportsData).not.toHaveBeenCalled();
	});
});
