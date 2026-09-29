/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';

import CustomChartTooltip from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/CustomChartTooltip';

describe('CustomTooltip component', () => {
	it('render matching snapshot when data is provided', async () => {
		const mockPayloadTooltip = [
			{
				payload: {
					date: '2026-02-25',
					numberOfVisits: 189,
					timeSpent: 4800,
				},
			},
		];

		const {asFragment} = render(
			<CustomChartTooltip active={true} payload={mockPayloadTooltip} />
		);

		expect(asFragment()).toMatchSnapshot();

		const tooltip = screen.getByTestId('active-tooltip');

		expect(tooltip).toBeInTheDocument();
		expect(tooltip).toHaveClass('chart-tooltip-area');
		expect(tooltip).toHaveTextContent('Feb 25, 2026');
		expect(tooltip).toHaveTextContent('189');
		expect(tooltip).toHaveTextContent('1h 20 min');
	});

	it('tooltip matches snapshot when no data is provided', () => {
		const {asFragment} = render(
			<CustomChartTooltip active={false} payload={[]} />
		);

		expect(asFragment()).toMatchSnapshot();
	});
});
