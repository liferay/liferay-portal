import React from 'react';
import {act, fireEvent, render, within} from '@testing-library/react';
import {SessionsCard} from '../SessionsCard';

jest.unmock('react-dom');

jest.mock('recharts', () => {
	const OriginalModule = jest.requireActual('recharts');

	return {
		...OriginalModule,
		ResponsiveContainer: ({children}: {children: React.ReactNode}) => (
			<OriginalModule.ResponsiveContainer height={320} width={800}>
				{children}
			</OriginalModule.ResponsiveContainer>
		),
	};
});

const histogram = [
	{key: '2026-06-10T00:00:00.000Z', value: 10},
	{key: '2026-06-11T00:00:00.000Z', value: 20},
];

const experiment = {
	dxpVariants: [
		{dxpVariantName: 'Control', sessionsHistogram: histogram},
		{dxpVariantName: 'Variant 1', sessionsHistogram: histogram},
	],
	sessionsHistogram: histogram,
};

const hoverFirstDataPoint = (container: HTMLElement) => {
	const event = new MouseEvent('mousemove', {bubbles: true});

	Object.defineProperties(event, {
		pageX: {value: 100},
		pageY: {value: 100},
	});

	fireEvent(container.querySelector('.recharts-wrapper')!, event);

	act(() => {
		jest.runOnlyPendingTimers();
	});
};

describe('SessionsCard', () => {
	it('renders the day of each data point on the x axis', () => {
		const {container} = render(<SessionsCard experiment={experiment} />);

		const ticks = container.querySelectorAll(
			'.recharts-xAxis .recharts-cartesian-axis-tick text'
		);

		expect(ticks[0]).toHaveTextContent('Jun 10');
		expect(ticks[1]).toHaveTextContent('Jun 11');
	});

	it('renders the day of the hovered data point in the total sessions tooltip', () => {
		const {container} = render(<SessionsCard experiment={experiment} />);

		hoverFirstDataPoint(container);

		expect(
			within(
				container.querySelector('.analytics-tooltip-chart-header')!
			).getByText('Jun 10')
		).toBeInTheDocument();
	});

	it('renders the day of the hovered data point in the per variant tooltip', () => {
		const {container, getByText} = render(
			<SessionsCard experiment={experiment} />
		);

		fireEvent.click(getByText('Per Variant'));

		hoverFirstDataPoint(container);

		expect(
			within(
				container.querySelector('.analytics-tooltip-chart-header')!
			).getByText('Jun 10')
		).toBeInTheDocument();
	});
});
