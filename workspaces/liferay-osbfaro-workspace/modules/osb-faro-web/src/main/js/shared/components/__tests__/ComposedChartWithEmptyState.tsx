import ComposedChartWithEmptyState from '../ComposedChartWithEmptyState';
import React from 'react';
import {cleanup, render} from '@testing-library/react';

const MockedComposedChart = () => (
	<div data-testid="my-composed-chart">{'my composed chart content'}</div>
);

describe('CollapsibleOverlay', () => {
	afterEach(cleanup);

	it('should render without empty state', () => {
		const {container, getByTestId, getByText} = render(
			<ComposedChartWithEmptyState
				emptyDescription="this is an empty description"
				emptyTitle="this is an empty title"
			>
				<MockedComposedChart />
			</ComposedChartWithEmptyState>
		);

		const node = container.querySelector(
			'.composed-chart-with-empty-state'
		);

		expect(node).toBeTruthy();
		expect(() => getByText('this is an empty title')).toThrow();
		expect(() => getByText('this is an empty description')).toThrow();
		expect(getByTestId('my-composed-chart')).toBeInTheDocument();
		expect(getByText('my composed chart content')).toBeInTheDocument();
	});

	it('should render empty state', () => {
		const {container, getByTestId, getByText} = render(
			<ComposedChartWithEmptyState
				emptyDescription="this is an empty description"
				emptyTitle="this is an empty title"
				showEmptyState
			>
				<MockedComposedChart />
			</ComposedChartWithEmptyState>
		);

		const node = container.querySelector(
			'.composed-chart-with-empty-state'
		);

		expect(
			node?.className.includes('composed-chart-with-empty-state--show')
		).toBeTruthy();
		expect(getByText('this is an empty title')).toBeInTheDocument();
		expect(getByText('this is an empty description')).toBeInTheDocument();
		expect(getByTestId('my-composed-chart')).toBeInTheDocument();
		expect(getByText('my composed chart content')).toBeInTheDocument();
	});

	it('should render empty state and renders a react component on description', () => {
		const {container, getByTestId, getByText} = render(
			<ComposedChartWithEmptyState
				emptyDescription={
					<div data-testid="my-custom-description">
						{'this is an empty description'}
					</div>
				}
				emptyTitle="this is an empty title"
				showEmptyState
			>
				<MockedComposedChart />
			</ComposedChartWithEmptyState>
		);

		const node = container.querySelector(
			'.composed-chart-with-empty-state'
		);

		expect(
			node?.className.includes('composed-chart-with-empty-state--show')
		).toBeTruthy();
		expect(getByText('this is an empty title')).toBeInTheDocument();
		expect(getByText('this is an empty description')).toBeInTheDocument();
		expect(getByTestId('my-custom-description')).toBeInTheDocument();
		expect(getByTestId('my-composed-chart')).toBeInTheDocument();
		expect(getByText('my composed chart content')).toBeInTheDocument();
	});
});
