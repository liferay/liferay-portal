/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import SectionHeader from '../components/SectionHeader';
import InsightsView from '../components/insights_view/InsightsView';
import MetricsDashboard from '../components/metrics_dashboard/MetricsDashboard';
import {Metrics} from '../components/metrics_dashboard/types';

import './OnPage.scss';

export default function OnPage({
	apiURL,
	emptyState,
	fdsActionDropdownItems,
	fdsId,
	filters,
	insightDetailsURL,
	lastScanDate,
	metrics,
	views,
}: {
	apiURL: string;
	emptyState: Record<string, unknown>;
	fdsActionDropdownItems: any[];
	fdsId: string;
	filters: any[];
	insightDetailsURL: string;
	lastScanDate: string | null;
	metrics: Metrics | null;
	views: any[];
}) {
	const handleSelectInsight = (externalReferenceCode: string) => {
		window.location.assign(
			`${insightDetailsURL}?backURL=${encodeURIComponent(
				window.location.pathname + window.location.search
			)}&objectEntryExternalReferenceCode=${externalReferenceCode}`
		);
	};

	return (
		<>
			<SectionHeader
				lastScanDate={lastScanDate}
				title={Liferay.Language.get('on-page')}
			/>

			<MetricsDashboard
				metrics={metrics}
				scope={Liferay.Language.get('on-page')}
			/>

			<InsightsView
				apiURL={apiURL}
				emptyState={emptyState}
				fdsActionDropdownItems={fdsActionDropdownItems}
				fdsId={fdsId}
				filters={filters}
				onSelectInsight={handleSelectInsight}
				views={views}
			/>
		</>
	);
}
