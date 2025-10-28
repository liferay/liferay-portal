/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import ScheduleField from '../../../../src/main/resources/META-INF/resources/js/content_editor/components/ScheduleField';

const DATE_CONFIG = {
	clayFormat: 'MM/dd/yyyy',
	firstDayOfWeek: 0,
	isDateTime: true,
	momentFormat: 'MM/DD/YYYY hh:mm A',
	placeholder: '--',
	serverFormat: 'YYYY-MM-DD HH:mm',
	use12Hours: true,
};

const renderComponent = ({...props} = {}) => {
	return render(
		<ScheduleField
			date=""
			dateConfig={DATE_CONFIG}
			label="Date and Time"
			name="dateAndTime"
			updateFieldData={() => null}
			{...props}
		/>
	);
};

describe('ScheduleField', () => {
	beforeEach(() => {
		global.Liferay.ThemeDisplay.getTimeZone = jest
			.fn()
			.mockReturnValue('utc');
	});

	it('renders ScheduleField', () => {
		renderComponent();

		const group = screen.getByRole('group', {name: 'Date and Time'});
		const input = screen.getByPlaceholderText('MM/DD/YYYY --:-- --');
		const neverExpire = screen.queryByLabelText('never-expire');

		expect(group).toBeInTheDocument();
		expect(input).toBeInTheDocument();
		expect(neverExpire).not.toBeInTheDocument();
	});

	it('renders ScheduleField with Never Expire checkbox', async () => {
		renderComponent({neverExpire: true});

		const input = screen.getByPlaceholderText('MM/DD/YYYY --:-- --');
		const neverExpire = screen.getByLabelText('never-expire');

		expect(neverExpire).toBeInTheDocument();
		expect(neverExpire).toBeChecked();
		expect(input).toBeDisabled();

		await userEvent.click(neverExpire);

		await waitFor(() => {
			expect(neverExpire).not.toBeChecked();
			expect(input).not.toBeDisabled();
		});
	});

	it('shows the required error', async () => {
		renderComponent({required: true});

		const input = screen.getByPlaceholderText('MM/DD/YYYY --:-- --');

		await userEvent.click(input);
		await userEvent.tab();

		await waitFor(() => {
			expect(
				screen.getByText('this-field-is-required')
			).toBeInTheDocument();
		});

		const nextYear = new Date().getFullYear() + 1;

		await userEvent.clear(input);
		await userEvent.type(input, `10/31/${nextYear} 01:00 PM`);
		await userEvent.tab();

		await waitFor(() => {
			expect(screen.queryByText('error')).not.toBeInTheDocument();
		});
	});

	it('does not show the required error when the input is not required', async () => {
		renderComponent({required: false});

		const input = screen.getByPlaceholderText('MM/DD/YYYY --:-- --');

		await userEvent.click(input);
		await userEvent.tab();

		await waitFor(() => {
			expect(screen.queryByText('error')).not.toBeInTheDocument();
		});
	});

	it('shows the invalid error', async () => {
		renderComponent();

		const input = screen.getByPlaceholderText('MM/DD/YYYY --:-- --');

		await userEvent.type(input, '10/31/2025');
		await userEvent.tab();

		await waitFor(() => {
			expect(
				screen.getByText('the-field-value-is-invalid')
			).toBeInTheDocument();
		});

		const nextYear = new Date().getFullYear() + 1;

		await userEvent.clear(input);
		await userEvent.type(input, `10/31/${nextYear} 01:00 PM`);
		await userEvent.tab();

		await waitFor(() => {
			expect(screen.queryByText('error')).not.toBeInTheDocument();
		});
	});

	it('shows the past date error', async () => {
		renderComponent();

		const input = screen.getByPlaceholderText('MM/DD/YYYY --:-- --');

		await userEvent.type(input, '10/31/2024 01:00 PM');
		await userEvent.tab();

		await waitFor(() => {
			expect(
				screen.getByText('the-date-entered-is-in-the-past')
			).toBeInTheDocument();
		});

		const nextYear = new Date().getFullYear() + 1;

		await userEvent.clear(input);
		await userEvent.type(input, `10/31/${nextYear} 01:00 PM`);
		await userEvent.tab();

		await waitFor(() => {
			expect(screen.queryByText('error')).not.toBeInTheDocument();
		});
	});
});
