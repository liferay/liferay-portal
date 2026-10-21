/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {Formik} from 'formik';
import React from 'react';

import '@testing-library/jest-dom';

import {FormToggle} from '../../src/main/resources/META-INF/resources/js/forms/FormToggle';

function renderToggle(active: boolean, disabled?: boolean) {
	return render(
		<Formik initialValues={{active}} onSubmit={() => {}}>
			<FormToggle
				ariaLabel="Profile Status"
				disabled={disabled}
				name="active"
			/>
		</Formik>
	);
}

describe('FormToggle', () => {
	it('exposes a switch named after its purpose, not after its state', () => {
		renderToggle(false);

		const toggle = screen.getByRole('switch', {name: 'Profile Status'});

		expect(toggle).not.toBeChecked();
		expect(screen.getByText('inactive')).toBeInTheDocument();
	});

	it('keeps the same accessible name after toggling', async () => {
		renderToggle(false);

		await userEvent.click(
			screen.getByRole('switch', {name: 'Profile Status'})
		);

		expect(
			screen.getByRole('switch', {name: 'Profile Status'})
		).toBeChecked();
		expect(screen.getByText('active')).toBeInTheDocument();
	});

	it('can be disabled', () => {
		renderToggle(false, true);

		expect(
			screen.getByRole('switch', {name: 'Profile Status'})
		).toBeDisabled();
	});
});
