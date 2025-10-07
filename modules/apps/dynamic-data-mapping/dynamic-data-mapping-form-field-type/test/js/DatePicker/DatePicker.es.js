/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import moment from 'moment';
import React from 'react';

import DatePicker from '../../../src/main/resources/META-INF/resources/js/DatePicker/DatePicker';

describe('DatePicker', () => {
	it.skip('calls the onChange callback with a valid date and time', () => {
		const onChange = jest.fn();

		render(<DatePicker onChange={onChange} type="date_time" />);

		userEvent.click(screen.getByLabelText('Choose date'));

		const hours = screen.getByLabelText('Enter the hour in 00:00 format');
		const minutes = screen.getByLabelText(
			'Enter the minutes in 00:00 format'
		);
		const sufix = screen.getByLabelText(
			'Select time of day (AM/PM) using up (PM) and down (AM) arrow keys'
		);

		userEvent.click(screen.getByLabelText('Select current date'));

		userEvent.type(hours, '11');
		userEvent.type(minutes, '30');
		fireEvent.keyDown(sufix, {code: 'ArrowUp', key: 'ArrowUp'}); // PM

		expect(onChange).toHaveBeenCalledWith(
			{},
			moment().format('YYYY-MM-DD [23:30]')
		);
	});

	it.skip('fills the input date and time according to the locale', () => {
		const {container} = render(
			<DatePicker locale="pt_BR" onChange={() => {}} type="date_time" />
		);

		userEvent.click(screen.getByLabelText('Choose date'));

		const hours = screen.getByLabelText('Enter the hour in 00:00 format');
		const minutes = screen.getByLabelText(
			'Enter the minutes in 00:00 format'
		);

		userEvent.click(screen.getByLabelText('Select current date'));

		userEvent.type(hours, '23');
		userEvent.type(minutes, '30');

		expect(container.querySelector('[type=text]')).toHaveValue(
			moment().format('DD/MM/YYYY [23:30]')
		);
	});

	/* TODO: remove skip after alow user to input arabic digits */
	it.skip('passes only occidental digits to the onChange callback', () => {
		const onChange = jest.fn();
		render(
			<DatePicker locale="ar_SA" name="test-date" onChange={onChange} />
		);

		const input = screen.getByRole('textbox');

		userEvent.type(input, '٠١/٠١/٢٠٢١');

		expect(onChange).toHaveBeenLastCalledWith('');
	});

	it('calls the onChange callback with a valid date', () => {
		const onChange = jest.fn();

		render(<DatePicker onChange={onChange} />);

		const [button] = screen.getAllByLabelText('select-date');

		userEvent.click(button);
		fireEvent.click(screen.getByLabelText('select-current-date'));

		expect(onChange).toHaveBeenCalledWith(
			{},
			moment().format('YYYY-MM-DD')
		);
	});

	it('does not have aria-invalid attribute on first render when it is required', () => {
		const {container} = render(<DatePicker required={true} />);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not have aria-invalid attribute when it is required and has a value', () => {
		const {container} = render(
			<DatePicker required={true} value="2021-01-01" />
		);

		const input = container.querySelector('input[aria-required="true"]');

		expect(input.hasAttribute('aria-invalid')).toBe(false);
	});

	it('does not render the html autocomplete attribute', () => {
		render(<DatePicker />);

		expect(
			document.querySelector('.form-control').hasAttribute('autocomplete')
		).toBe(false);
	});

	it('expands the datepicker on calendar icon click', () => {
		render(<DatePicker />);

		const [button] = screen.getAllByLabelText('select-date');

		userEvent.click(button);

		expect(
			document.body.querySelector('.date-picker-dropdown-menu.show')
		).toBeInTheDocument();
	});

	it('fills the input completely when last item of a date mask is a symbol', () => {
		render(<DatePicker locale="hu_HU" onChange={() => {}} />);

		const input = screen.getByRole('textbox');

		userEvent.type(input, '1111.11.11.');

		expect(input).toHaveValue('1111.11.11.');
	});

	it('fills the input date according to the locale', () => {
		render(<DatePicker locale="ja_JP" onChange={() => {}} />);

		const [button] = screen.getAllByLabelText('select-date');

		userEvent.click(button);
		fireEvent.click(screen.getByLabelText('select-current-date'));

		expect(screen.getByRole('textbox', {hidden: true})).toHaveValue(
			moment().format('YYYY/MM/DD')
		);
	});

	it('fills the input with the date selected on Date Picker', () => {
		const {getByLabelText} = render(<DatePicker onChange={() => {}} />);

		const [button] = screen.getAllByLabelText('select-date');

		userEvent.click(button);
		fireEvent.click(getByLabelText('select-current-date'));

		expect(screen.getByRole('textbox', {hidden: true})).toHaveValue(
			moment().format('MM/DD/YYYY')
		);
	});

	it('renders the help text', () => {
		render(<DatePicker tip="Type something" />);

		expect(
			document.querySelector('.form-feedback-group')
		).toHaveTextContent('Type something');
	});

	it('renders the html autocomplete attribute', () => {
		render(<DatePicker htmlAutocompleteAttribute="name" />);

		expect(
			document.querySelector('.form-control').getAttribute('autocomplete')
		).toBe('name');
	});

	it('renders the label', () => {
		render(<DatePicker label="Date picker" />);

		const allByText = screen.getAllByText('Date picker');
		expect(allByText).toHaveLength(2);
		expect(allByText[0]).toBeInTheDocument();
		expect(allByText[1]).toBeInTheDocument();
	});

	it('renders the predefined value', () => {
		render(<DatePicker predefinedValue="2020-06-02" />);

		expect(screen.getByRole('textbox')).toHaveValue('06/02/2020');
	});

	it('sets the hidden input with occidental digits', () => {
		render(
			<DatePicker
				defaultLanguageId="ar_SA"
				name="test-date"
				onChange={() => {}}
				value="2021-01-01"
			/>
		);
		const input = screen.getByRole('textbox');
		const hiddenInput = document.querySelector('[name=test-date]');

		expect(input).toHaveValue('٠١/٠١/٢٠٢١');
		expect(hiddenInput).toHaveValue('2021-01-01');
	});
});
