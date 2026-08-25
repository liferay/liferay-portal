/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import {ILearnResourceContext} from 'frontend-js-components-web';
import React from 'react';

import Dashboards from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/Dashboards';
import {GovernanceAdditionalProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/types';
import {DashboardAdditionalProps} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/types';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/components/Breadcrumb',
	() => ({
		__esModule: true,
		default: () => 'breadcrumb',
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/components/EnterpriseOnlyPlaceholder',
	() => ({
		__esModule: true,
		default: () => 'enterprise-only-placeholder',
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/governance/GovernanceDashboard',
	() => ({
		__esModule: true,
		default: () => 'governance-dashboard',
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/inventory/InventoryDashboard',
	() => ({
		__esModule: true,
		default: () => 'inventory-dashboard',
	})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceDashboard',
	() => ({
		__esModule: true,
		default: () => 'performance-dashboard',
	})
);

function renderDashboards({
	cmsAdmin = true,
	freeTier = false,
}: {cmsAdmin?: boolean; freeTier?: boolean} = {}) {
	return render(
		<Dashboards
			additionalProps={
				{} as DashboardAdditionalProps & GovernanceAdditionalProps
			}
			admin={false}
			analyticsEnabled={true}
			cmsAdmin={cmsAdmin}
			constants={{}}
			freeTier={freeTier}
			learnResources={{} as ILearnResourceContext}
		/>
	);
}

describe('Dashboards', () => {
	beforeEach(() => {
		(global as any).Liferay.FeatureFlags = {
			'LPD-58315': true,
			'LPD-82226': true,
		};
	});

	it('shows the performance tab to CMS admins', () => {
		renderDashboards();

		expect(
			screen.getByRole('button', {name: 'performance'})
		).toBeInTheDocument();
	});

	it('hides the performance tab from non admins', () => {
		renderDashboards({cmsAdmin: false});

		expect(
			screen.queryByRole('button', {name: 'performance'})
		).not.toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'governance'})
		).toBeInTheDocument();
	});

	it('shows only the inventory dashboard when the performance tab is the only one gated out', () => {
		(global as any).Liferay.FeatureFlags = {
			'LPD-58315': true,
			'LPD-82226': false,
		};

		renderDashboards({cmsAdmin: false});

		expect(
			screen.queryByRole('button', {name: 'inventory'})
		).not.toBeInTheDocument();

		expect(screen.getByText('inventory-dashboard')).toBeInTheDocument();
	});

	it('shows the enterprise placeholder instead of any dashboard on the free tier', () => {
		renderDashboards({freeTier: true});

		expect(
			screen.getByText('enterprise-only-placeholder')
		).toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'governance'})
		).not.toBeInTheDocument();

		expect(
			screen.queryByText('governance-dashboard')
		).not.toBeInTheDocument();
	});

	it('hides the performance tab when its feature flag is disabled', () => {
		(global as any).Liferay.FeatureFlags = {
			'LPD-58315': false,
			'LPD-82226': true,
		};

		renderDashboards();

		expect(
			screen.queryByRole('button', {name: 'performance'})
		).not.toBeInTheDocument();
	});
});
