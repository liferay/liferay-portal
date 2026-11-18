import BarComparisonCell from '../BarComparisonCell';
import React from 'react';
import {DataTypes} from 'shared/types/DataTypes';
import {getMaxEventValue, parseBreakdownData} from 'event-analysis/utils/utils';
import {mockBreakdownData} from 'test/data';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('BarComparisonCell', () => {
	const orderedBreakdowns = [
		{dataType: DataTypes.String},
		{dataType: DataTypes.String},
		{dataType: DataTypes.String}
	];

	const event = {name: 'View Article'};

	it('render', () => {
		const items = parseBreakdownData(
			mockBreakdownData(),
			orderedBreakdowns
		);
		const topValue = getMaxEventValue(items, false);
		const {events} = items[0];

		const {container} = render(
			<BarComparisonCell
				compareToPrevious={false}
				event={event}
				events={events}
				topValue={topValue}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('render comparing previous', () => {
		const items = parseBreakdownData(
			mockBreakdownData(true),
			orderedBreakdowns
		);
		const topValue = getMaxEventValue(items, true);
		const {events} = items[0];

		const {container, queryByText} = render(
			<BarComparisonCell
				compareToPrevious
				event={event}
				events={events}
				topValue={topValue}
			/>
		);

		expect(container.querySelectorAll('tbody > tr').length).toBe(2);
		expect(container.querySelector('.metric-bar-root .lines')).toBeTruthy();
		expect(queryByText(/^previous value$/i)).toBeTruthy();
	});

	it('render comparing previous and segment', () => {
		const items = parseBreakdownData(
			mockBreakdownData(true, true),
			orderedBreakdowns
		);
		const topValue = getMaxEventValue(items, true);
		const {events} = items[0];

		const {container, queryByText} = render(
			<BarComparisonCell
				compareToPrevious
				event={event}
				events={events}
				topValue={topValue}
			/>
		);

		expect(container.querySelectorAll('tbody > tr').length).toBe(4);
		expect(queryByText(/^segmented$/i)).toBeTruthy();
	});

	it('render comparing previous and event', () => {
		const items = parseBreakdownData(
			mockBreakdownData(true, false, true),
			orderedBreakdowns
		);
		const topValue = getMaxEventValue(items, true);
		const {events} = items[0];

		const {container, queryByText} = render(
			<BarComparisonCell
				compareToPrevious
				event={event}
				events={events}
				topValue={topValue}
			/>
		);

		expect(container.querySelectorAll('tbody > tr').length).toBe(4);
		expect(queryByText('Read Article')).toBeTruthy();
	});

	it('render two tables when comparing segment and event', () => {
		const items = parseBreakdownData(
			mockBreakdownData(false, true, true),
			orderedBreakdowns
		);
		const topValue = getMaxEventValue(items, false);
		const {events} = items[0];

		const {container} = render(
			<BarComparisonCell
				compareToPrevious={false}
				event={event}
				events={events}
				topValue={topValue}
			/>
		);

		expect(container).toMatchSnapshot();

		expect(container.querySelectorAll('table').length).toBe(2);
	});
});
