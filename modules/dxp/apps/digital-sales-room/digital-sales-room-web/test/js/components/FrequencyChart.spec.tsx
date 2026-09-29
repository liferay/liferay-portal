/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import React from 'react';
import ResizeObserver from 'resize-observer-polyfill';

import FrequencyChart from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/FrequencyChart';

global.ResizeObserver = ResizeObserver;

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={400} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useIsInViewport',
	() => ({
		__esModule: true,
		default: jest.fn(() => true),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery',
	() => {
		const {
			frequencyChartFixture,
		} = require('../fixtures/FrequencyChartFixture');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: frequencyChartFixture,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('VisitFrequencyChart component', () => {
	let container: HTMLElement;

	beforeEach(() => {
		const view = render(<FrequencyChart isAnalyticsEnabled={true} />);

		container = view.container;
	});

	afterAll(() => {
		delete (global as any).ResizeObserver;
	});

	it('render the chart matching snapshot', async () => {
		const svgChart = container.querySelector(
			'.recharts-surface'
		) as HTMLElement;

		expect(svgChart).toMatchSnapshot();
	});

	it('render the chart with data', async () => {
		const svgChart = container.querySelector(
			'.recharts-surface'
		) as HTMLElement;

		expect(svgChart).toBeInTheDocument();

		const bar = container.querySelector('.recharts-bar');

		expect(bar).toBeInTheDocument();

		const xAxisTick = container.querySelector(
			'.recharts-xAxis .recharts-cartesian-axis-tick-value'
		);

		expect(xAxisTick).toHaveTextContent('daily');
	});

	it('renders the not-configured message when analytics cloud is not configured', () => {
		render(<FrequencyChart isAnalyticsEnabled={false} />);

		expect(
			screen.getByText('analytics-cloud-is-not-configured')
		).toBeInTheDocument();
	});
});
