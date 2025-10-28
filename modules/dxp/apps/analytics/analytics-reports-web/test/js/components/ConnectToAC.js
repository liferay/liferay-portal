/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import ConnectToAC from '../../../src/main/resources/META-INF/resources/js/components/ConnectToAC';

import '@testing-library/jest-dom';

describe('ConnectToAC', () => {
	it('renders a link to analytics cloud in order to connect with our site when analytics is connected', () => {
		const testProps = {
			analyticsCloudTrialURL: 'https://localhost/',
			analyticsURL: 'https://localhost/',
			hideAnalyticsReportsPanelURL: 'https://localhost/',
			isAnalyticsConnected: true,
			pathToAssets: '/',
		};

		const {container, getByText} = render(
			<ConnectToAC
				analyticsCloudTrialURL={testProps.analyticsCloudTrialURL}
				analyticsURL={testProps.analyticsURL}
				hideAnalyticsReportsPanelURL={
					testProps.hideAnalyticsReportsPanelURL
				}
				isAnalyticsConnected={testProps.isAnalyticsConnected}
				pathToAssets={testProps.pathToAssets}
			/>
		);

		expect(getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(getByText('open-analytics-cloud')).toBeInTheDocument();

		const link = container.querySelectorAll('a');
		expect(link.length).toBe(1);
		expect(link[0].href).toBe(testProps.analyticsURL);
	});
});
