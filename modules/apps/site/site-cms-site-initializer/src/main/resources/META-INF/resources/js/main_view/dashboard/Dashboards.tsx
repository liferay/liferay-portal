/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLayout from '@clayui/layout';
import ClayLink from '@clayui/link';
import ClayNavigationBar from '@clayui/navigation-bar';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useState} from 'react';

import Breadcrumb from '../../common/components/Breadcrumb';
import InventoryDashboard from './inventory/InventoryDashboard';
import PerformanceDashboard from './performance/PerformanceDashboard';

import '../../../css/dashboard/Dashboards.scss';

import {ILearnResourceContext} from 'frontend-js-components-web';

import EnterpriseOnlyPlaceholder from '../../common/components/EnterpriseOnlyPlaceholder';

const TABS = {
	inventory: Liferay.Language.get('inventory'),
	performance: Liferay.Language.get('performance'),
} as const;

type TabId = keyof typeof TABS;

function Wrapper({
	admin,
	analyticsEnabled,
	constants,
	freeTier,
	learnResources,
}: {
	admin: boolean;
	analyticsEnabled: boolean;
	constants: {[key: string]: string};
	freeTier: boolean;
	learnResources: ILearnResourceContext;
}) {
	return (
		<>
			<Breadcrumb
				breadcrumbItems={[{label: Liferay.Language.get('dashboard')}]}
				freeTier={freeTier}
				hideSpace
			/>

			<ClayTooltipProvider>
				<Dashboards
					admin={admin}
					analyticsEnabled={analyticsEnabled}
					constants={constants}
					freeTier={freeTier}
					learnResources={learnResources}
				/>
			</ClayTooltipProvider>
		</>
	);
}

function Dashboards({
	admin,
	analyticsEnabled,
	constants,
	freeTier,
	learnResources,
}: {
	admin: boolean;
	analyticsEnabled: boolean;
	constants: {[key: string]: string};
	freeTier: boolean;
	learnResources: ILearnResourceContext;
}) {
	const [tabId, setTabId] = useState<TabId>('inventory');

	if (freeTier) {
		return (
			<ClayLayout.Container className="px-4" fluid>
				<EnterpriseOnlyPlaceholder learnResources={learnResources} />
			</ClayLayout.Container>
		);
	}

	if (!Liferay.FeatureFlags['LPD-58315']) {
		return (
			<ClayLayout.Container className="px-4" fluid>
				<InventoryDashboard constants={constants} />
			</ClayLayout.Container>
		);
	}

	return (
		<>
			<Tabs setTabId={setTabId} tabId={tabId} />

			<ClayLayout.Container className="px-4" fluid>
				{tabId === 'inventory' ? (
					<InventoryDashboard constants={constants} />
				) : null}

				{tabId === 'performance' ? (
					<PerformanceDashboard
						admin={admin}
						analyticsEnabled={analyticsEnabled}
					/>
				) : null}
			</ClayLayout.Container>
		</>
	);
}

function Tabs({
	setTabId,
	tabId,
}: {
	setTabId: (id: TabId) => void;
	tabId: TabId;
}) {
	return (
		<ClayNavigationBar
			className="mb-4"
			fluidSize={false}
			triggerLabel={TABS[tabId]}
		>
			{(Object.keys(TABS) as TabId[]).map((id) => (
				<ClayNavigationBar.Item active={id === tabId} key={id}>
					<ClayLink
						onClick={(event) => {
							event.preventDefault();

							setTabId(id);
						}}
						role="tab"
					>
						{TABS[id]}
					</ClayLink>
				</ClayNavigationBar.Item>
			))}
		</ClayNavigationBar>
	);
}

export default Wrapper;
