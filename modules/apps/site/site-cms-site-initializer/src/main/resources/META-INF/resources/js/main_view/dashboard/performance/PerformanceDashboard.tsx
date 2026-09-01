/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLoadingIndicator from '@clayui/loading-indicator';
import {
	ConnectSites,
	ConnectToAnalyticsCloud,
} from '@liferay/analytics-reports-js-components-web';
import React from 'react';

import {PerformanceContextProvider} from './PerformanceContext';
import {AudienceAndDistribution} from './components/AudienceAndDistribution';
import {ContentConsumption} from './components/ContentConsumption';
import {Filters} from './components/Filters';
import {Overview} from './components/Overview';
import useConnectedSpaces from './hooks/useConnectedSpaces';
import {DashboardAdditionalProps} from './types';

export default function PerformanceDashboard({
	additionalProps,
	admin,
	analyticsEnabled,
	constants,
	spaceIds,
}: {
	additionalProps?: DashboardAdditionalProps;
	admin: boolean;
	analyticsEnabled: boolean;
	constants: {[key: string]: string};
	spaceIds: string[];
}) {
	if (!analyticsEnabled) {
		return (
			<div
				className="align-items-center d-flex justify-content-center pb-6"
				style={{minHeight: '50vh'}}
			>
				<ConnectToAnalyticsCloud admin={admin} />
			</div>
		);
	}

	return (
		<PerformanceContextProvider
			additionalProps={additionalProps}
			constants={constants}
			spaceIds={spaceIds}
		>
			<Sections />
		</PerformanceContextProvider>
	);
}

function Sections() {
	const {connected, loading} = useConnectedSpaces();

	if (loading) {
		return <ClayLoadingIndicator />;
	}

	if (!connected) {
		return <ConnectSites />;
	}

	return (
		<>
			<Filters />

			<Overview />

			<AudienceAndDistribution />

			<ContentConsumption />
		</>
	);
}
