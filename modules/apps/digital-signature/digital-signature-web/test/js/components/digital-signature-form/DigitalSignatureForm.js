/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {AppContext} from '../../../../src/main/resources/META-INF/resources/js/AppContext';
import DigitalSignatureForm from '../../../../src/main/resources/META-INF/resources/js/components/digital-signature-form/DigitalSignatureForm';

const DigitalSignatureFormWithProvider = (props) => (
	<AppContext.Provider value={{}}>
		<DigitalSignatureForm {...props} />
	</AppContext.Provider>
);

describe('DigitalSignatureForm', () => {
	afterEach(cleanup);

	it('Renders with the Send button disabled', () => {
		const {getByText} = render(<DigitalSignatureFormWithProvider />);

		const button = getByText('send');

		expect(button.hasAttribute('disabled')).toBeTruthy();
	});

	it('Validates required fields', async () => {
		const {asFragment, container, getByLabelText} = render(
			<DigitalSignatureFormWithProvider />
		);

		const inputEmailMessage = getByLabelText('email-message');

		await act(async () => {
			fireEvent.change(inputEmailMessage, {
				target: {
					value: 'Email Message',
				},
			});
		});

		const parentEnvelopeName = container
			.querySelector('[for="envelopeName"]')
			.closest('div');
		const parentRecipientFullName = container
			.querySelector('[for="recipients[0].fullName"]')
			.closest('div');
		const parentRecipentEmail = container
			.querySelector('[for="recipients[0].email"]')
			.closest('div');
		const parentEmailSubject = container
			.querySelector('[for="emailSubject"]')
			.closest('div');

		expect(parentEnvelopeName.classList.contains('has-error')).toBeTruthy();
		expect(
			parentRecipientFullName.classList.contains('has-error')
		).toBeTruthy();
		expect(
			parentRecipentEmail.classList.contains('has-error')
		).toBeTruthy();
		expect(parentEmailSubject.classList.contains('has-error')).toBeTruthy();

		expect(asFragment()).toMatchSnapshot();
	});

	it('Restricts recipients to existing users when embedded signing is enabled', () => {
		const {getByPlaceholderText, queryByPlaceholderText} = render(
			<AppContext.Provider value={{enableEmbeddedView: true}}>
				<DigitalSignatureForm />
			</AppContext.Provider>
		);

		expect(getByPlaceholderText('search-for')).toBeTruthy();
		expect(queryByPlaceholderText('recipient-email')).toBeNull();
		expect(
			getByPlaceholderText('recipient-full-name').hasAttribute('disabled')
		).toBeTruthy();
	});
});
