/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import {ChartState} from '../../src/main/resources/META-INF/resources/js';

interface DummyChartProps {
	className?: string;
	data?: Array<{value: number}>;
	title: string;
}

function DummyChart({className, data = [], title}: DummyChartProps) {
	return (
		<figure className={className}>
			<figcaption>{title}</figcaption>

			<span>{`points: ${data.length}`}</span>
		</figure>
	);
}

describe('ChartState', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders a loading indicator when loading is true', () => {
		render(
			<ChartState loading>
				<DummyChart data={[{value: 1}]} title="Visits" />
			</ChartState>
		);

		expect(screen.getByRole('status')).toBeInTheDocument();
		expect(screen.queryByText('Visits')).not.toBeInTheDocument();
	});

	it('renders a danger alert with a string error', () => {
		render(
			<ChartState error="Boom">
				<DummyChart data={[{value: 1}]} title="Visits" />
			</ChartState>
		);

		expect(screen.getByRole('alert')).toHaveTextContent('Boom');
	});

	it('renders a danger alert with an Error object', () => {
		render(
			<ChartState error={new Error('Kaboom')}>
				<DummyChart title="Visits" />
			</ChartState>
		);

		expect(screen.getByRole('alert')).toHaveTextContent('Kaboom');
	});

	it('renders the empty state with a default message when empty is true', () => {
		render(
			<ChartState empty>
				<DummyChart data={[]} title="Visits" />
			</ChartState>
		);

		expect(screen.getByText('no-data-available')).toBeInTheDocument();
		expect(screen.getByText('there-is-no-data')).toBeInTheDocument();
	});

	it('renders the empty state with a configurable message', () => {
		render(
			<ChartState empty emptyStateMessage="Nothing here yet">
				<DummyChart data={[]} title="Visits" />
			</ChartState>
		);

		expect(screen.getByText('Nothing here yet')).toBeInTheDocument();
	});

	it('renders the children when no state flag is set', () => {
		render(
			<ChartState>
				<DummyChart
					className="my-chart"
					data={[{value: 1}, {value: 2}]}
					title="Visits"
				/>
			</ChartState>
		);

		expect(screen.getByText('Visits')).toBeInTheDocument();
		expect(screen.getByText('points: 2')).toBeInTheDocument();
		expect(screen.getByText('Visits').closest('figure')).toHaveClass(
			'my-chart'
		);
		expect(screen.queryByRole('alert')).not.toBeInTheDocument();
		expect(screen.queryByRole('status')).not.toBeInTheDocument();
	});

	it('catches a render error, calls fallbackError, and renders an alert', () => {
		jest.spyOn(console, 'error').mockImplementation(() => {});

		function ThrowingChart(): JSX.Element {
			throw new Error('render fail');
		}

		const fallbackError = jest.fn();

		render(
			<ChartState fallbackError={fallbackError}>
				<ThrowingChart />
			</ChartState>
		);

		expect(screen.getByRole('alert')).toHaveTextContent(
			'an-error-occurred'
		);
		expect(fallbackError).toHaveBeenCalledTimes(1);
		expect(fallbackError.mock.calls[0][0]).toBeInstanceOf(Error);
	});

	it('catches a render error without a fallbackError callback', () => {
		jest.spyOn(console, 'error').mockImplementation(() => {});

		function ThrowingChart(): JSX.Element {
			throw new Error('render fail');
		}

		render(
			<ChartState>
				<ThrowingChart />
			</ChartState>
		);

		expect(screen.getByRole('alert')).toBeInTheDocument();
	});

	it('leaves emptiness to the caller, regardless of chart data shape', () => {
		interface LineDummyProps {
			categories: string[];
			series: unknown[];
			title: string;
		}

		function LineDummy({series, title}: LineDummyProps) {
			return (
				<figure>
					<figcaption>{title}</figcaption>

					<span>{`series: ${series.length}`}</span>
				</figure>
			);
		}

		const {rerender} = render(
			<ChartState empty>
				<LineDummy categories={[]} series={[]} title="Trend" />
			</ChartState>
		);

		expect(screen.getByText('no-data-available')).toBeInTheDocument();

		rerender(
			<ChartState empty={false}>
				<LineDummy categories={['Jan']} series={[{}]} title="Trend" />
			</ChartState>
		);

		expect(screen.getByText('Trend')).toBeInTheDocument();
		expect(screen.getByText('series: 1')).toBeInTheDocument();
	});

	it('short-circuits to the empty state even when children would render', () => {
		render(
			<ChartState empty>
				<DummyChart data={[{value: 1}]} title="Visits" />
			</ChartState>
		);

		expect(screen.getByText('no-data-available')).toBeInTheDocument();
		expect(screen.queryByText('Visits')).not.toBeInTheDocument();
	});

	it('has no accessibility violations in the empty state', async () => {
		const {container} = render(
			<ChartState empty>
				<DummyChart data={[]} title="Visits" />
			</ChartState>
		);

		await checkAccessibility({bestPractices: true, context: container});
	});
});
