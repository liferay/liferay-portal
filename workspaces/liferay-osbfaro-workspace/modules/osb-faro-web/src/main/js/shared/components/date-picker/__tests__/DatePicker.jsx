import DatePicker from '../index';
import mockCurrentDate from 'test/mock-date';
import moment from 'moment';
import React from 'react';
import {fireEvent, render} from '@testing-library/react';

jest.unmock('react-dom');

describe('DatePicker', () => {
	let currentDate;

	afterEach(() => {
		currentDate.mockReset();
		currentDate.mockRestore();
	});

	beforeEach(() => {
		currentDate = mockCurrentDate();
	});

	it('should render', () => {
		const {container} = render(
			<DatePicker date={moment(0)} minDate={moment(0)} />
		);

		jest.runAllTimers();

		expect(container).toMatchSnapshot();
	});

	it('should render the next month', () => {
		const {getByTestId, queryAllByText} = render(
			<DatePicker date={moment(0)} minDate={moment(0)} />
		);

		expect(queryAllByText(/30/).length).toBe(2);

		fireEvent.click(getByTestId('next-month'));

		jest.runAllTimers();

		expect(queryAllByText(/30/).length).toBe(0);
	});

	it('should render label when a range is passed', () => {
		const {queryByText} = render(
			<DatePicker
				date={{
					end: null,
					start: moment(0)
				}}
				minDate={moment(0)}
			/>
		);

		jest.runAllTimers();

		expect(queryByText(/^end date$/i)).toBeTruthy();
	});

	fit('should render max range error when range > maxRange', () => {
		const {queryByText} = render(
			<DatePicker
				date={{
					end: moment().add(13, 'months'),
					start: moment(0)
				}}
				maxRange={365}
			/>
		);

		jest.runAllTimers();

		expect(
			queryByText('This exceeds the maximum range of 365 days.')
		).toBeTruthy();
	});
});
