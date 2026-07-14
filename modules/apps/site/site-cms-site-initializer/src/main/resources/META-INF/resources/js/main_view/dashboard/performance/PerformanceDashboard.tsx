/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ConnectToAnalyticsCloud} from '@liferay/analytics-reports-js-components-web';
import React from 'react';

import {PerformanceContextProvider} from './PerformanceContext';
import {AudienceAndDistribution} from './components/AudienceAndDistribution';
import {ContentConsumption} from './components/ContentConsumption';
import {Filters} from './components/Filters';
import {Overview} from './components/Overview';

import '../../../../css/dashboard/PerformanceDashboard.scss';

export default function PerformanceDashboard({
	admin,
	analyticsEnabled,
}: {
	admin: boolean;
	analyticsEnabled: boolean;
}) {
	if (!analyticsEnabled) {
		return (
			<div
				className="align-items-center d-flex justify-content-center"
				style={{minHeight: '50vh'}}
			>
				<ConnectToAnalyticsCloud admin={admin} />
			</div>
		);
	}

	return (
		<PerformanceContextProvider>
			<Filters />

			<Overview />

			<AudienceAndDistribution />

			<ContentConsumption />
		</PerformanceContextProvider>
	);
}
