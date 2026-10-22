/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {AppContext} from '../../../../src/main/resources/META-INF/resources/js/AppContext';
import DigitalSignatureForm from '../../../../src/main/resources/META-INF/resources/js/components/digital-signature-form/DigitalSignatureForm';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

const DigitalSignatureFormWithProvider = (props) => (
	<AppContext.Provider value={{}}>
		<DigitalSignatureForm {...props} />
	</AppContext.Provider>
);

describe('DigitalSignatureForm', () => {
	afterAll(() => {
		delete window.ResizeObserver;
	});

	afterEach(cleanup);

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	it('Renders with the Send button disabled', () => {
		const {getByText} = render(<DigitalSignatureFormWithProvider />);

		const button = getByText('send');

		expect(button.hasAttribute('disabled')).toBeTruthy();
	});

	it('Restricts recipients to existing users when the embedded view is enabled', () => {
		const {getByPlaceholderText, queryByPlaceholderText} = render(
			<AppContext.Provider value={{embeddedViewEnabled: true}}>
				<DigitalSignatureForm />
			</AppContext.Provider>
		);

		expect(getByPlaceholderText('search-for')).toBeTruthy();
		expect(queryByPlaceholderText('recipient-email')).toBeNull();
		expect(
			getByPlaceholderText('recipient-full-name').hasAttribute('disabled')
		).toBeTruthy();
	});

	it('Sends the expiration days with the envelope', async () => {
		const navigate = jest.fn();

		fetch.mockImplementation((resource) =>
			Promise.resolve(
				new Response(
					JSON.stringify(
						String(resource).includes('user-accounts')
							? {
									items: [
										{
											emailAddress: 'test1@liferay.com',
											id: 1,
											name: 'Test Test',
										},
									],
								}
							: {dsEnvelopeId: 1}
					)
				)
			)
		);

		const {findByText, getByLabelText, getByPlaceholderText, getByText} =
			render(
				<AppContext.Provider
					value={{
						allowedFileExtensions: ['pdf'],
						baseResourceURL:
							'http://localhost:8080?p_p_id=unitTest',
						embeddedViewEnabled: true,
						portletNamespace: '_namespace_',
					}}
				>
					<DigitalSignatureForm
						fileEntries={[
							{
								extension: 'pdf',
								fileEntryId: 1,
								title: 'Document.pdf',
							},
						]}
						navigate={navigate}
					/>
				</AppContext.Provider>
			);

		await userEvent.type(
			getByPlaceholderText('my-envelope-name'),
			'Envelope Name'
		);
		await userEvent.type(getByLabelText('recipient-email'), 'test');
		await userEvent.click(
			await findByText('Test Test (test1@liferay.com)')
		);
		await userEvent.type(
			getByPlaceholderText('please-sign-this-document'),
			'Email Subject'
		);

		const inputExpireAfter = getByLabelText('days-until-expiration');

		await userEvent.clear(inputExpireAfter);
		await userEvent.type(inputExpireAfter, '30');
		await userEvent.click(getByText('send'));

		await waitFor(() => expect(navigate).toHaveBeenCalled());

		const [, {body}] = fetch.mock.calls.find(([resource]) =>
			String(resource).includes('add_ds_envelope')
		);

		expect(body.get('_namespace_expireAfter')).toBe('30');
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
});
