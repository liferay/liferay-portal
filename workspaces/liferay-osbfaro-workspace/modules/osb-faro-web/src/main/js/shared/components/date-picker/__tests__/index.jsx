import DatePicker from '../index';
import moment from 'moment';
import React from 'react';
import {render} from '@testing-library/react';

jest.unmock('react-dom');

const renderDatePicker = () =>
	render(
		<DatePicker
			date={moment.utc('2026-06-10T14:30:00Z')}
			maxDate={moment.utc('2027-06-10')}
			minDate={moment.utc('2025-06-10')}
		/>
	);

const getSelects = container =>
	container.querySelectorAll('.date-picker-select-root');

describe('DatePicker', () => {
	afterEach(() => {
		jest.useRealTimers();
	});

	beforeEach(() => {
		jest.useFakeTimers().setSystemTime(new Date('2026-06-10T14:30:00Z'));
	});

	it('renders the month names as the month options, with the month of the date selected', () => {
		const {container} = renderDatePicker();

		const [monthSelect] = getSelects(container);

		expect(
			Array.from(monthSelect.querySelectorAll('option')).map(
				option => option.textContent
			)
		).toEqual([
			'January',
			'February',
			'March',
			'April',
			'May',
			'June',
			'July',
			'August',
			'September',
			'October',
			'November',
			'December'
		]);
		expect(monthSelect).toHaveValue('June');
	});

	it('renders the years as the year options, with the year of the date selected', () => {
		const {container} = renderDatePicker();

		const [, yearSelect] = getSelects(container);

		expect(
			Array.from(yearSelect.querySelectorAll('option')).map(
				option => option.textContent
			)
		).toEqual(['2027', '2026', '2025']);
		expect(yearSelect).toHaveValue('2026');
	});
});
