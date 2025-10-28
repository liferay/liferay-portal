/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import userEvent from '@testing-library/user-event';

import '@testing-library/jest-dom';
import {fireEvent} from '@testing-library/react';
import {act} from 'react-dom/test-utils';

export async function setFieldValue(field, value, checkValue = true) {
	if (field === null) {
		return;
	}

	if (field instanceof HTMLSelectElement) {
		userEvent.selectOptions(field, String(value));
	}
	else {
		if (String(value).length) {
			await userEvent.type(field, String(value));
			field.value = String(value);
		}
		else {
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
