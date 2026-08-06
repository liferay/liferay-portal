/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import DateInput from '../../js/components/DateInput';

function getSubmittedInput(container: HTMLElement) {
	return container.querySelector<HTMLInputElement>(
		'input[name="ObjectField_dueDate"]'
	);
}

describe('DateInput', () => {
	it('converts a picked date to server format on the submitted input', async () => {
		const {container} = render(
			<DateInput label="Due Date" name="ObjectField_dueDate" />
		);

		await userEvent.type(screen.getByLabelText('Due Date'), '12/25/2026');

		expect(getSubmittedInput(container)).toHaveValue('2026-12-25');
	});

	it('disables the picker in edit mode', () => {
		render(
			<DateInput editMode label="Due Date" name="ObjectField_dueDate" />
		);

		expect(screen.getByLabelText('Due Date')).toBeDisabled();
	});

	it('keeps the submitted input empty while the typed date is incomplete', async () => {
		const {container} = render(
			<DateInput label="Due Date" name="ObjectField_dueDate" />
		);

		await userEvent.type(screen.getByLabelText('Due Date'), '12/25');

		expect(getSubmittedInput(container)).toHaveValue('');
	});

	it('shows a required error when the field is emptied', async () => {
		render(
			<DateInput label="Due Date" name="ObjectField_dueDate" required />
		);

		await userEvent.click(screen.getByLabelText(/Due Date/));
		await userEvent.tab();

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
	});

	it('shows the value in the user locale format and submits it in server format', () => {
		const {container} = render(
			<DateInput
				label="Due Date"
				name="ObjectField_dueDate"
				value="2026-07-09"
			/>
		);

		expect(screen.getByLabelText('Due Date')).toHaveValue('07/09/2026');

		expect(getSubmittedInput(container)).toHaveValue('2026-07-09');
	});
});
