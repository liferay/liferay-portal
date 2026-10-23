/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import AddDSRequestModal from '../../src/main/resources/META-INF/resources/js/AddDSRequestModal';

const buyer = {
	emailAddress: 'buyer@liferay.com',
	name: 'Buyer',
	userId: 1,
};

function renderAddDSRequestModal() {
	render(
		<AddDSRequestModal
			addDSRequestModalProps={{
				accountEntryUsers: [
					buyer,
					{
						emailAddress: 'other@liferay.com',
						name: 'Other',
						userId: 2,
					},
				],
				buyer,
				commerceOrderId: 1,
				getDSRequestRecipientUsersURL: 'http://localhost/search',
				portletNamespace: '_namespace_',
			}}
			addDSRequestURL="http://localhost/add"
			attachment={{id: 1, title: 'contract.pdf'}}
			closeModal={jest.fn()}
		/>
	);
}

describe('AddDSRequestModal', () => {
	afterAll(() => {
		delete window.ResizeObserver;
	});

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	it('does not send the request to the selected user after the email is edited', async () => {
		renderAddDSRequestModal();

		await userEvent.type(
			screen.getByLabelText(/email-subject/),
			'Please sign'
		);

		await userEvent.type(screen.getByLabelText(/recipient-email/), 'x');

		await userEvent.click(screen.getByText('send'));

		expect(screen.getByText('this-field-is-required')).toBeInTheDocument();
		expect(screen.getByLabelText(/recipient-full-name/)).toHaveValue('');
	});

	it('keeps the prefilled buyer when the email field gains and loses focus', async () => {
		renderAddDSRequestModal();

		const emailInput = screen.getByLabelText(/recipient-email/);

		await userEvent.click(emailInput);
		await userEvent.tab();

		expect(emailInput).toHaveValue('buyer@liferay.com');
		expect(screen.getByLabelText(/recipient-full-name/)).toHaveValue(
			'Buyer'
		);
	});
});
