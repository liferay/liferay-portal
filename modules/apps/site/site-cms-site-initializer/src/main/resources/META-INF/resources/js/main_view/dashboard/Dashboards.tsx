/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayLayout from '@clayui/layout';
import ClayNavigationBar from '@clayui/navigation-bar';
import {ClayTooltipProvider} from '@clayui/tooltip';
import React, {useState} from 'react';

import Breadcrumb from '../../common/components/Breadcrumb';
import GovernanceDashboard from './governance/GovernanceDashboard';
import InventoryDashboard from './inventory/InventoryDashboard';
import PerformanceDashboard from './performance/PerformanceDashboard';
import {DashboardAdditionalProps} from './performance/types';

import '../../../css/dashboard/Dashboards.scss';

import {ILearnResourceContext} from 'frontend-js-components-web';

import EnterpriseOnlyPlaceholder from '../../common/components/EnterpriseOnlyPlaceholder';

const TABS = {
	governance: Liferay.Language.get('governance'),
	inventory: Liferay.Language.get('inventory'),
	performance: Liferay.Language.get('performance'),
} as const;

type TabId = keyof typeof TABS;

const ORDERED_TAB_IDS: TabId[] = ['governance', 'inventory', 'performance'];

function isTabEnabled(tabId: TabId, cmsAdmin: boolean) {
	if (tabId === 'governance') {
		return Boolean(Liferay.FeatureFlags['LPD-82226']);
	}

	if (tabId === 'performance') {
		return Boolean(Liferay.FeatureFlags['LPD-58315']) && cmsAdmin;
	}

	return true;
}

function Wrapper({
	additionalProps,
	admin,
	analyticsEnabled,
	cmsAdmin,
	constants,
	freeTier,
	learnResources,
}: {
	additionalProps: DashboardAdditionalProps;
	admin: boolean;
	analyticsEnabled: boolean;
	cmsAdmin: boolean;
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
					additionalProps={additionalProps}
					admin={admin}
					analyticsEnabled={analyticsEnabled}
					cmsAdmin={cmsAdmin}
					constants={constants}
					freeTier={freeTier}
					learnResources={learnResources}
				/>
			</ClayTooltipProvider>
		</>
	);
}

function Dashboards({
	additionalProps,
	admin,
	analyticsEnabled,
	cmsAdmin,
	constants,
	freeTier,
	learnResources,
}: {
	additionalProps: DashboardAdditionalProps;
	admin: boolean;
	analyticsEnabled: boolean;
	cmsAdmin: boolean;
	constants: {[key: string]: string};
	freeTier: boolean;
	learnResources: ILearnResourceContext;
}) {
	const enabledTabIds = ORDERED_TAB_IDS.filter((id) =>
		isTabEnabled(id, cmsAdmin)
	);

	const [tabId, setTabId] = useState<TabId>(enabledTabIds[0] ?? 'inventory');

	if (freeTier) {
		return (
			<ClayLayout.Container className="px-4" fluid>
				<EnterpriseOnlyPlaceholder learnResources={learnResources} />
			</ClayLayout.Container>
		);
	}

	if (enabledTabIds.length === 1) {
		return (
			<ClayLayout.Container className="px-4" fluid>
				<InventoryDashboard constants={constants} />
			</ClayLayout.Container>
		);
	}

	return (
		<>
			<Tabs
				enabledTabIds={enabledTabIds}
				setTabId={setTabId}
				tabId={tabId}
			/>

			<ClayLayout.Container className="px-4" fluid>
				{tabId === 'governance' ? <GovernanceDashboard /> : null}

				{tabId === 'inventory' ? (
					<InventoryDashboard constants={constants} />
				) : null}

				{tabId === 'performance' ? (
					<PerformanceDashboard
						additionalProps={additionalProps}
						admin={admin}
						analyticsEnabled={analyticsEnabled}
						constants={constants}
					/>
				) : null}
			</ClayLayout.Container>
		</>
	);
}

function Tabs({
	enabledTabIds,
	setTabId,
	tabId,
}: {
	enabledTabIds: TabId[];
	setTabId: (id: TabId) => void;
	tabId: TabId;
}) {
	return (
		<ClayNavigationBar
			className="mb-4"
			fluidSize={false}
			triggerLabel={TABS[tabId]}
		>
			{enabledTabIds.map((id) => (
				<ClayNavigationBar.Item active={id === tabId} key={id}>
					<ClayButton
						className="nav-link"
						displayType="unstyled"
						onClick={() => setTabId(id)}
					>
						{TABS[id]}
					</ClayButton>
				</ClayNavigationBar.Item>
			))}
		</ClayNavigationBar>
	);
}

export default Wrapper;
