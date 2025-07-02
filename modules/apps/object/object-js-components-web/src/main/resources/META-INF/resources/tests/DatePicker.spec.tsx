/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {DatePicker} from '../components/DatePicker';

it('allows deleting only one character instead of clearing all input', async () => {
	const {container} = render(
		<DatePicker
			disabled={false}
			id="datePicker"
			onChange={jest.fn()}
			type="DateTime"
			value="2025-07-01 18:56"
		/>
	);

	const dateInput = container.querySelector(
		`input[id$="datePicker"]`
	) as HTMLElement;

	expect(dateInput).toHaveValue('07/01/2025 06:56 PM');

	await userEvent.click(dateInput);

	await userEvent.type(dateInput, '{backspace}');

	expect(dateInput).toHaveValue('07/01/2025 06:56 P_');
});
