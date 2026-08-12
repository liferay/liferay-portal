import React from 'react';
import {fireEvent, render} from '@testing-library/react';
import {SegmentGrowthChart} from '../Growth';

const chartData = [
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724112000000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724198400000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724284800000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724371200000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724457600000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724544000000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724630400000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724716800000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724803200000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724889600000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1724976000000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725062400000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725148800000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725235200000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725321600000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725408000000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725494400000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725580800000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725667200000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725753600000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725840000000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1725926400000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726012800000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726099200000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726185600000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726272000000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726358400000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726444800000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726531200000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726617600000,
		removed: 0,
		value: 0,
	},
	{
		added: 0,
		anonymousCount: 0,
		knownCount: 0,
		modifiedDate: 1726704000000,
		removed: 0,
		value: 0,
	},
];

jest.unmock('react-dom');

/**
 * Override Recharts Responsive Container
 * width dimensions fixed to be able to render charts
 */

const tooltipEnabled = jest.fn();

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={350} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
		Tooltip: ({children, ...props}: {children: React.ReactNode}) => {
			tooltipEnabled();

			return (
				<OriginalModule.Tooltip {...props} active>
					{children}
				</OriginalModule.Tooltip>
			);
		},
	};
});

describe('SegmentGrowthChart', () => {
	it('should render chart and display the tooltip on mouse over', () => {
		const {container} = render(
			<SegmentGrowthChart
				data={chartData}
				hasSelectedPoint
				selectedPoint={1}
			/>
		);

		const chart = document.querySelector('.recharts-wrapper');

		expect(chart).toBeInTheDocument();

		fireEvent.mouseEnter(chart!);

		expect(tooltipEnabled).toHaveBeenCalledTimes(1);

		expect(
			container.querySelector('.recharts-wrapper')
		).toBeInTheDocument();
	});

	it('does not throw when clicked without a selection handler', () => {
		const {container} = render(
			<SegmentGrowthChart data={chartData} hasSelectedPoint={false} />
		);

		const event = new MouseEvent('click', {bubbles: true});

		Object.defineProperty(event, 'pageX', {value: 400});
		Object.defineProperty(event, 'pageY', {value: 150});

		expect(() =>
			fireEvent(container.querySelector('.recharts-wrapper')!, event)
		).not.toThrow();
	});
});
