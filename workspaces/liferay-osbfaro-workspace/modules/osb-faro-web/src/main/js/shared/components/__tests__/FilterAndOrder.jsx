import FilterAndOrder from '../FilterAndOrder';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

describe('FilterAndOrder', () => {
	afterEach(cleanup);

	const FILTER_BY_OPTIONS = [
		{
			key: 'status',
			label: 'status',
			values: [
				{label: 'done', value: 'done'},
				{label: 'running', value: 'running'}
			]
		}
	];

	const ORDER_BY_OPTIONS = [
		{
			label: 'name',
			value: 'name'
		}
	];

	it('should render', () => {
		const {getByTestId} = render(
			<FilterAndOrder filterByOptions={FILTER_BY_OPTIONS} />
		);

		expect(getByTestId('filter-button')).toBeInTheDocument();
	});

	it('should render with filter by options', () => {
		const {container} = render(
			<FilterAndOrder filterByOptions={FILTER_BY_OPTIONS} />
		);

		expect(
			container.querySelector('[data-testid="filter-button"]')
		).toBeTruthy();
	});

	it('should render with order by options', () => {
		const {container} = render(
			<FilterAndOrder orderByOptions={ORDER_BY_OPTIONS} />
		);

		expect(
			container.querySelector('[data-testid="order-button"]')
		).toBeTruthy();
	});

	it('should render with order by options and filter by options', () => {
		const {container} = render(
			<FilterAndOrder
				filterByOptions={FILTER_BY_OPTIONS}
				orderByOptions={ORDER_BY_OPTIONS}
			/>
		);

		expect(
			container.querySelector('[data-testid="filter-button"]')
		).toBeTruthy();
		expect(
			container.querySelector('[data-testid="order-button"]')
		).toBeTruthy();
	});

	it('should render as disabled', () => {
		const {getByTestId} = render(
			<FilterAndOrder
				disabled
				filterByOptions={FILTER_BY_OPTIONS}
				orderByOptions={ORDER_BY_OPTIONS}
			/>
		);

		expect(getByTestId('filter-button')).toHaveAttribute('disabled');

		expect(getByTestId('order-button')).toHaveAttribute('disabled');
	});

	it('should render filters as a flat list', () => {
		const {container} = render(
			<FilterAndOrder filterByOptions={FILTER_BY_OPTIONS} flat />
		);

		expect(
			container.querySelector('[data-testid="filter-button"]')
		).toBeTruthy();
	});

	it('renders radio inputs for a nested filter of type radio', () => {
		const {baseElement, getByTestId, getByText} = render(
			<FilterAndOrder
				filterByOptions={[
					{
						key: 'activeUsers',
						label: 'Active Users',
						type: 'radio',
						values: [
							{label: 'Last 30 Days', value: '30'},
							{label: 'Last 7 Days', value: '7'}
						]
					}
				]}
			/>
		);

		fireEvent.click(getByTestId('filter-button'));
		fireEvent.click(getByText('Active Users'));

		expect(
			baseElement.querySelectorAll('input[type="radio"]').length
		).toBeGreaterThan(0);
		expect(baseElement.querySelector('input[type="checkbox"]')).toBeNull();
	});
});
