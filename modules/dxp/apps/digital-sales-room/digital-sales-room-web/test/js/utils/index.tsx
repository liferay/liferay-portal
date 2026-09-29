/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

export async function setFieldValue(
	field: HTMLInputElement | HTMLSelectElement,
	value: null | number | string,
	checkValue = true
) {
	if (field === null) {
		return;
	}

	if (field instanceof HTMLSelectElement) {
		userEvent.selectOptions(field, String(value));
	}
	else {
		if (String(value).length) {
			await userEvent.clear(field);
			await userEvent.type(field, String(value));
			field.value = String(value);
		}
		else {
			await userEvent.clear(field);
			field.value = '';
		}
	}
	act(() => {
		fireEvent.change(field);
	});

	if (checkValue) {
		if (field.type === 'number' && (value === '' || value === 0)) {
			expect(field).not.toHaveValue();
		}
		else {
			expect(field).toHaveValue(value);
		}
	}
}
