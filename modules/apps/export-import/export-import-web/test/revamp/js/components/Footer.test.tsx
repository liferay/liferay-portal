/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {Form, Formik} from 'formik';
import React from 'react';

import '@testing-library/jest-dom';

import Footer from '../../../../src/main/resources/META-INF/resources/revamp/js/components/Footer';

const renderFooter = ({
	continueDisabled,
	initialStatus,
	onSubmit = () => {},
}: {
	continueDisabled?: boolean;
	initialStatus?: {submitted: boolean};
	onSubmit?: () => void | Promise<void>;
} = {}) =>
	render(
		<Formik
			initialStatus={initialStatus}
			initialValues={{}}
			onSubmit={onSubmit}
		>
			<Form>
				<Footer backURL="/back" continueDisabled={continueDisabled} />
			</Form>
		</Formik>
	);

describe('Footer', () => {
	it('enables the action button by default', () => {
		renderFooter();

		expect(screen.getByRole('button', {name: 'continue'})).toBeEnabled();
	});

	it('disables the action button when the caller says so', () => {
		renderFooter({continueDisabled: true});

		expect(screen.getByRole('button', {name: 'continue'})).toBeDisabled();
	});

	it('disables the action button while the form is submitting', async () => {
		renderFooter({onSubmit: () => new Promise(() => {})});

		const button = screen.getByRole('button', {name: 'continue'});

		await userEvent.click(button);

		expect(button).toBeDisabled();
	});

	it('keeps the action button disabled once the form has been submitted', () => {
		renderFooter({initialStatus: {submitted: true}});

		expect(screen.getByRole('button', {name: 'continue'})).toBeDisabled();
	});
});
