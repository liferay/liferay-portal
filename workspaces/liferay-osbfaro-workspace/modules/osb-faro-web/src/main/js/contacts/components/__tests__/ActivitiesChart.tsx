import ActivitiesChart from '../ActivitiesChart';
import React from 'react';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {render, waitFor} from '@testing-library/react';

jest.unmock('react-dom');

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

const history = [
	{intervalInitDate: 1717200000000, totalEvents: 3, totalSessions: 2},
	{intervalInitDate: 1717286400000, totalEvents: 5, totalSessions: 4},
];

const weeklyHistory = [
	{intervalInitDate: 1717200000000, totalEvents: 3, totalSessions: 2},
	{intervalInitDate: 1717804800000, totalEvents: 5, totalSessions: 4},
];

const CHARACTER_WIDTH = 7;

const originalGetBoundingClientRect = Element.prototype.getBoundingClientRect;

const renderChart = (props = {}) =>
	render(
		<ActivitiesChart
			alwaysShowSelectedTooltip={false}
			history={history}
			interval="D"
			onPointSelect={() => {}}
			rangeSelectors={{
				rangeEnd: null,
				rangeKey: RangeKeyTimeRanges.Last30Days,
				rangeStart: null,
			}}
			{...props}
		/>
	);

describe('ActivitiesChart', () => {
	beforeAll(() => {
		Element.prototype.getBoundingClientRect = function (this: Element) {
			const width = (this.textContent ?? '').length * CHARACTER_WIDTH;

			return {
				bottom: 16,
				height: 16,
				left: 0,
				right: width,
				toJSON: () => ({}),
				top: 0,
				width,
				x: 0,
				y: 0,
			} as DOMRect;
		};
	});

	afterAll(() => {
		Element.prototype.getBoundingClientRect = originalGetBoundingClientRect;
	});

	it('does not crash when the selected point index is out of bounds for the current history', () => {
		expect(() =>
			renderChart({selectedPoint: history.length + 5})
		).not.toThrow();
	});

	it('draws the reference line for an in-bounds selected point', () => {
		const {container} = renderChart({selectedPoint: 1});

		expect(
			container.querySelector('.recharts-reference-line')
		).toBeInTheDocument();
	});

	it('draws bars by default', () => {
		const {container} = renderChart();

		expect(container.querySelector('.recharts-bar')).toBeInTheDocument();
		expect(container.querySelector('.recharts-line')).toBeNull();
	});

	it('draws a line with no dots until hovered when the line view is selected', () => {
		const {container} = renderChart({chartView: 'line'});

		expect(container.querySelector('.recharts-line')).toBeInTheDocument();
		expect(container.querySelector('.recharts-bar')).toBeNull();
		expect(container.querySelector('.recharts-line-dots')).toBeNull();
	});

	it('keeps every bar inside the plot area when the history is sparse enough to widen the bars', async () => {
		const {container} = renderChart();

		await waitFor(() =>
			expect(
				container.querySelector('.recharts-bar-rectangle path')
			).toBeInTheDocument()
		);

		const axisLine = container.querySelector(
			'.recharts-xAxis .recharts-cartesian-axis-line'
		)!;

		const plotStart = Number(axisLine.getAttribute('x1'));
		const plotEnd = Number(axisLine.getAttribute('x2'));

		const bars = container.querySelectorAll('.recharts-bar-rectangle path');

		expect(bars).toHaveLength(history.length);

		bars.forEach((bar) => {
			const barStart = Number(bar.getAttribute('x'));
			const barWidth = Number(bar.getAttribute('width'));

			expect(barStart).toBeGreaterThanOrEqual(plotStart - 1);
			expect(barStart + barWidth).toBeLessThanOrEqual(plotEnd + 1);
		});
	});

	it('keeps the label of a tick that sits at the very end of the axis', () => {
		const {container} = renderChart();

		const ticks = container.querySelectorAll(
			'.recharts-xAxis .recharts-cartesian-axis-tick text'
		);

		expect(ticks).toHaveLength(1);
		expect(ticks[0]).toHaveTextContent('Jun 2');
	});

	it('renders the label of the last week, whose date range is wide enough to run past the end of the axis', () => {
		const {container} = renderChart({
			history: weeklyHistory,
			interval: 'W',
		});

		const ticks = container.querySelectorAll(
			'.recharts-xAxis .recharts-cartesian-axis-tick text'
		);

		expect(ticks).toHaveLength(2);
		expect(ticks[1]).toHaveTextContent('Jun 8 - 14');
	});
});
