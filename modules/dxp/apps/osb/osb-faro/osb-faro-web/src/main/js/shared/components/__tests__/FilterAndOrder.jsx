import FilterAndOrder from '../FilterAndOrder';
import React from 'react';
import {cleanup, fireEvent, render} from '@testing-library/react';

const triggerDropdown = container => {
	const dropdownToggle = container.querySelector('.dropdown-toggle');

	fireEvent.click(dropdownToggle);
};

jest.unmock('react-dom');

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
		render(<FilterAndOrder filterByOptions={FILTER_BY_OPTIONS} />);

		expect(document.body).toMatchSnapshot();
	});

	it('should render with filter by options', () => {
		const {container} = render(
			<FilterAndOrder filterByOptions={FILTER_BY_OPTIONS} />
		);

		triggerDropdown(container);

		expect(document.body).toMatchSnapshot();
	});

	it('should render with order by options', () => {
		const {container} = render(
			<FilterAndOrder orderByOptions={ORDER_BY_OPTIONS} />
		);

		triggerDropdown(container);

		expect(document.body).toMatchSnapshot();
	});

	it('should render with order by options and filter by options', () => {
		const {container} = render(
			<FilterAndOrder
				filterByOptions={FILTER_BY_OPTIONS}
				orderByOptions={ORDER_BY_OPTIONS}
			/>
		);

		triggerDropdown(container);

		expect(document.body).toMatchSnapshot();
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

		triggerDropdown(container);

		expect(document.body).toMatchSnapshot();
	});
});
