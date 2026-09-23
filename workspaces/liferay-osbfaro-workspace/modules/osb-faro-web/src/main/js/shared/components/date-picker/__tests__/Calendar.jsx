import Calendar, {
	isEnd,
	isHoverDateBeforeStartDate,
	isInHoverRange,
	isSelected,
	isStart
} from '../Calendar';
import moment from 'moment';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('Calendar', () => {
	it('should render', () => {
		const {container} = render(
			<Calendar
				currentMonth={moment(0).startOf('month')}
				date={moment(0)}
			/>
		);
		expect(container).toMatchSnapshot();
	});

	it('should render the short weekday names as the header', () => {
		const {container} = render(
			<Calendar
				currentMonth={moment.utc('2026-06-01')}
				date={moment.utc('2026-06-10')}
			/>
		);

		const weekdays = Array.from(container.querySelectorAll('thead th')).map(
			th => th.textContent
		);

		expect(weekdays).toEqual([
			'Sun',
			'Mon',
			'Tue',
			'Wed',
			'Thu',
			'Fri',
			'Sat'
		]);
	});

	it('should render days as being in a range', () => {
		const {container} = render(
			<Calendar
				currentMonth={moment(0).startOf('month')}
				date={{
					end: moment(0).add(7, 'days'),
					start: moment(0)
				}}
			/>
		);

		expect(container).toMatchSnapshot();
	});

	it('the same day should not be selected as both the start and end', () => {
		const onSelect = jest.fn();

		const {queryByText} = render(
			<Calendar
				currentMonth={moment(0).startOf('month')}
				date={{
					end: null,
					start: moment(0).startOf('day')
				}}
				onSelect={onSelect}
			/>
		);

		const firstDay = queryByText('1');

		fireEvent.click(firstDay);

		expect(onSelect).toHaveBeenCalledTimes(0);
	});

	describe('isSelected', () => {
		const range = {
			end: moment(20000),
			start: moment(50)
		};

		it.each`
			date             | selected
			${moment(0)}     | ${false}
			${moment(50)}    | ${true}
			${moment(1000)}  | ${false}
			${moment(20000)} | ${true}
			${moment(30000)} | ${false}
		`('should return $selected for $date', ({date, selected}) => {
			expect(isSelected(range, date)).toBe(selected);
			expect(isEnd(moment(0), date)).toBe(false);
		});
	});

	describe('isStart', () => {
		const range = {
			end: moment(20000),
			start: moment(50)
		};

		it.each`
			date             | selected
			${moment(0)}     | ${false}
			${moment(50)}    | ${true}
			${moment(1000)}  | ${false}
			${moment(20000)} | ${false}
		`('should return $selected for $date', ({date, selected}) => {
			expect(isStart(range, date)).toBe(selected);
			expect(isEnd(moment(0), date)).toBe(false);
		});
	});

	describe('isEnd', () => {
		const range = {
			end: moment(20000),
			start: moment(50)
		};

		it.each`
			date             | selected
			${moment(0)}     | ${false}
			${moment(50)}    | ${false}
			${moment(1000)}  | ${false}
			${moment(20000)} | ${true}
			${moment(30000)} | ${false}
		`('should return $selected for $date', ({date, selected}) => {
			expect(isEnd(range, date)).toBe(selected);
			expect(isEnd(moment(0), date)).toBe(false);
		});
	});

	describe('isHoverDateBeforeStartDate', () => {
		const range = {
			end: null,
			start: moment(50)
		};

		it.each`
			date             | response
			${moment(0)}     | ${true}
			${moment(50)}    | ${false}
			${moment(1000)}  | ${false}
			${moment(20000)} | ${false}
		`('should return $response for $date', ({date, response}) => {
			expect(isHoverDateBeforeStartDate(range, date)).toBe(response);
		});
	});

	describe('isInHoverRange', () => {
		const range = {
			end: null,
			start: moment(50)
		};

		it.each`
			date           | hoveredDate    | response
			${moment(0)}   | ${moment(0)}   | ${true}
			${moment(100)} | ${moment(0)}   | ${false}
			${moment(100)} | ${moment(150)} | ${true}
			${moment(200)} | ${moment(100)} | ${false}
		`(
			'should return $response for $date in range of $hoveredDate',
			({date, hoveredDate, response}) => {
				expect(isInHoverRange(range, date, hoveredDate)).toBe(response);
			}
		);
	});
});
