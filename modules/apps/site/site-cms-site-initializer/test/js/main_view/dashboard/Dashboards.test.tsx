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
	administeredSpaceIds = ['1'],
	freeTier = false,
}: {administeredSpaceIds?: string[]; freeTier?: boolean} = {}) {
	return render(
		<Dashboards
			additionalProps={
				{} as DashboardAdditionalProps & GovernanceAdditionalProps
			}
			admin={false}
			administeredSpaceGroupIds={administeredSpaceIds.map(
				(id) => `1${id}`
			)}
			administeredSpaceIds={administeredSpaceIds}
			allSpacesAdministered={false}
			analyticsEnabled={true}
			constants={{}}
			freeTier={freeTier}
			learnResources={{} as ILearnResourceContext}
		/>
	);
}

describe('Dashboards', () => {
	beforeEach(() => {
		(global as any).Liferay.FeatureFlags = {
			'LPD-82226': true,
		};
	});

	it('shows the governance and performance tabs to space administrators', () => {
		renderDashboards();

		expect(
			screen.getByRole('button', {name: 'governance'})
		).toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'performance'})
		).toBeInTheDocument();
	});

	it('shows only the inventory dashboard when no space is administered', () => {
		renderDashboards({administeredSpaceIds: []});

		expect(
			screen.queryByRole('button', {name: 'governance'})
		).not.toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'inventory'})
		).not.toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'performance'})
		).not.toBeInTheDocument();

		expect(screen.getByText('inventory-dashboard')).toBeInTheDocument();
	});

	it('hides the governance tab when its feature flag is off', () => {
		(global as any).Liferay.FeatureFlags = {
			'LPD-82226': false,
		};

		renderDashboards();

		expect(
			screen.queryByRole('button', {name: 'governance'})
		).not.toBeInTheDocument();

		expect(
			screen.getByRole('button', {name: 'performance'})
		).toBeInTheDocument();
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
});
