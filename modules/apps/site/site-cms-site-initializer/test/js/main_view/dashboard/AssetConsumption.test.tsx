/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {PerformanceContextProvider} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceContext';
import PerformanceService from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/PerformanceService';
import {AssetConsumption} from '../../../../src/main/resources/META-INF/resources/js/main_view/dashboard/performance/components/AssetConsumption';

const renderComponent = () =>
	render(
		<PerformanceContextProvider>
			<AssetConsumption />
		</PerformanceContextProvider>
	);

describe('AssetConsumption', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders the table rows with the fetched items', async () => {
		jest.spyOn(PerformanceService, 'getAssetConsumption').mockResolvedValue(
			{
				data: {
					performanceAssetConsumptionItems: [
						{count: 211, key: 'k1', title: 'Basic Content'},
						{count: 185, key: 'k2', title: 'Knowledge Base'},
					],
					performanceAssetConsumptionItemsCount: 2,
					totalCount: 396,
				},
				error: null,
			}
		);

		renderComponent();

		expect(await screen.findByText('Basic Content')).toBeInTheDocument();
		expect(screen.getByText('Knowledge Base')).toBeInTheDocument();
		expect(screen.getByText('211')).toBeInTheDocument();
	});

	it('renders the empty state when there are no items', async () => {
		jest.spyOn(PerformanceService, 'getAssetConsumption').mockResolvedValue(
			{
				data: {
					performanceAssetConsumptionItems: [],
					performanceAssetConsumptionItemsCount: 0,
					totalCount: 0,
				},
				error: null,
			}
		);

		renderComponent();

		expect(await screen.findByText('no-assets-yet')).toBeInTheDocument();
	});
});
