/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import CheckPermissions from './components/content-dashboard/CheckPermissions';
import GlobalFilters from './components/content-dashboard/GlobalFilters';
import OverviewMetrics from './components/content-dashboard/OverviewMetrics';
import InteractionsByPage from './components/content-dashboard/interactions-by-page/InteractionsByPage';
import VisitorsBehavior from './components/content-dashboard/visitors-behavior/VisitorsBehavior';
import Technology from './components/technology/Technology';

import '../css/content_dashboard_performance.scss';

interface IContentDashboardPerformanceProps
	extends React.HTMLAttributes<HTMLElement> {
	contentPerformanceDataFetchURL: string;
	getItemVersionsURL: string;
}

const ContentDashboardPerformance: React.FC<
	IContentDashboardPerformanceProps
> = ({contentPerformanceDataFetchURL, getItemVersionsURL}) => {
	return (
		<div className="content-dashboard-performance">
			<CheckPermissions
				contentPerformanceDataFetchURL={contentPerformanceDataFetchURL}
				getItemVersionsURL={getItemVersionsURL}
			>
				<GlobalFilters />

				<OverviewMetrics />

				<VisitorsBehavior />

				<InteractionsByPage />

				<Technology />
			</CheckPermissions>
		</div>
	);
};

export default ContentDashboardPerformance;
