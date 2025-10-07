/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {ContextProvider} from './Context';
import {CheckPermissions} from './components/cms/CheckPermissions';
import GlobalFilters from './components/cms/GlobalFilters';
import OverviewMetrics from './components/cms/OverviewMetrics';
import {TopPagesMetrics} from './components/cms/TopPagesMetrics';
import {TrafficChannels} from './components/cms/TrafficChannels';
import {AssetMetrics} from './components/cms/asset-metrics/AssetMetrics';

import '../css/cms_performance.scss';

interface ICMSPerformanceProps extends React.HTMLAttributes<HTMLElement> {
	externalReferenceCode?: string;
	objectEntryFolderExternalReferenceCode?: string;
	onConnectSites: (loadData: () => void) => void;
	scopeId?: number;
}

const CMSPerformance: React.FC<ICMSPerformanceProps> = ({
	externalReferenceCode,
	objectEntryFolderExternalReferenceCode,
	onConnectSites,
	scopeId,
}) => {
	return (
		<div className="cms-performance">
			<CheckPermissions
				onConnectSites={onConnectSites}
				scopeId={String(scopeId)}
			>
				<ContextProvider
					customState={{
						externalReferenceCode: String(externalReferenceCode),
						objectEntryFolderExternalReferenceCode: String(
							objectEntryFolderExternalReferenceCode
						),
						scopeId: String(scopeId),
					}}
				>
					<GlobalFilters />

					<OverviewMetrics />

					<AssetMetrics />

					<TopPagesMetrics />

					<TrafficChannels />
				</ContextProvider>
			</CheckPermissions>
		</div>
	);
};

export default CMSPerformance;
