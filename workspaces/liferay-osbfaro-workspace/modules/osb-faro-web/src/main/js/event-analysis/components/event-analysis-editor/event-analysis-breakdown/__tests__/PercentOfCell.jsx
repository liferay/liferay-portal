import PercentOfCell from '../PercentOfCell';
import React from 'react';
import {DataTypes} from 'shared/types/DataTypes';
import {mockBreakdownData} from 'test/data';
import {parseBreakdownData} from 'event-analysis/utils/utils';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

describe('PercentOfCell', () => {
	const orderedBreakdowns = [
		{dataType: DataTypes.String},
		{dataType: DataTypes.String},
		{dataType: DataTypes.String}
	];

	it('render', () => {
		const data = mockBreakdownData();
		const items = parseBreakdownData(data, orderedBreakdowns);
		const {events} = items[0];

		const {container} = render(
			<PercentOfCell
				compareToPrevious={false}
				events={events}
				totalEvents={data.totalEvents}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('render comparing previous', () => {
		const data = mockBreakdownData(true);
		const items = parseBreakdownData(data, orderedBreakdowns);
		const {events} = items[0];

		const {container} = render(
			<PercentOfCell
				compareToPrevious
				events={events}
				totalEvents={data.totalEvents}
			/>
		);

		expect(container.querySelectorAll('ul > li').length).toBe(2);
	});

	it('render comparing previous and segment', () => {
		const data = mockBreakdownData(true, true);
		const items = parseBreakdownData(data, orderedBreakdowns);
		const {events} = items[0];

		const {container} = render(
			<PercentOfCell
				compareToPrevious
				events={events}
				totalEvents={data.totalEvents}
			/>
		);

		expect(container.querySelectorAll('ul > li').length).toBe(4);
	});

	it('render comparing previous and event', () => {
		const data = mockBreakdownData(true, false, true);
		const items = parseBreakdownData(data, orderedBreakdowns);
		const {events} = items[0];

		const {container} = render(
			<PercentOfCell
				compareToPrevious
				events={events}
				totalEvents={data.totalEvents}
			/>
		);

		expect(container.querySelectorAll('ul > li').length).toBe(4);
	});

	it('render two list when comparing segment and event', () => {
		const data = mockBreakdownData(false, true, true);
		const items = parseBreakdownData(data, orderedBreakdowns);
		const {events} = items[0];

		const {container} = render(
			<PercentOfCell
				compareToPrevious
				events={events}
				totalEvents={data.totalEvents}
			/>
		);

		expect(container.querySelectorAll('ul').length).toBe(2);
	});

	it('render the percentage of the total', () => {
		const {container} = render(
			<PercentOfCell
				events={[{leafNode: true, name: 'Event', value: 25}]}
				totalValue={200}
			/>
		);

		expect(container.querySelector('li').textContent).toBe('12.5%');
	});

	it('render a zero percentage when the value is zero', () => {
		const {container} = render(
			<PercentOfCell
				events={[{leafNode: true, name: 'Event', value: 0}]}
				totalValue={200}
			/>
		);

		expect(container.querySelector('li').textContent).toBe('0%');
	});

	it('render no data when the total is zero', () => {
		const {container} = render(
			<PercentOfCell
				events={[{leafNode: true, name: 'Event', value: 0}]}
				totalValue={0}
			/>
		);

		expect(container.querySelector('li .text-secondary').textContent).toBe(
			'–'
		);
	});

	it('render no data when the total is missing', () => {
		const {container} = render(
			<PercentOfCell
				events={[{leafNode: true, name: 'Event', value: 25}]}
			/>
		);

		expect(container.querySelector('li .text-secondary').textContent).toBe(
			'–'
		);
	});

	it('display "undefined" value is attribute "name" is empty', () => {
		const data = {
			breakdownItems: [
				{
					breakdownItems: [
						{
							breakdownItems: [
								{
									breakdownItems: [],
									leafNode: true,
									name: 'All Individuals',
									value: 1717
								}
							],
							leafNode: false,
							value: 3367
						}
					],
					leafNode: false,
					value: 3367
				}
			],
			count: 1,
			page: 1,
			value: 5033
		};
		const items = parseBreakdownData(data, orderedBreakdowns);

		expect(items).toEqual([
			{
				breakdown0: {name: 'undefined', rowSpan: 1, value: 3367},
				breakdown1: {name: 'undefined', rowSpan: 1, value: 3367},
				events: [
					{
						breakdownItems: [],
						leafNode: true,
						name: 'All Individuals',
						value: 1717
					}
				],
				index: 0
			}
		]);
	});
});
