/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import NewTokenModalContent from '../../src/main/resources/META-INF/resources/js/style-book-editor/NewTokenModalContent';

jest.mock('frontend-js-components-web', () => ({
	...jest.requireActual('frontend-js-components-web'),
	openToast: jest.fn(),
}));

const renderComponent = ({
	tokenSets = [
		{description: 'Set 1 description', label: 'Set 1', name: 'set1'},
	],
} = {}) =>
	render(
		<NewTokenModalContent
			addFrontendTokenURL="/add-frontend-token"
			categoryLabel="Category 1"
			categoryName="category1"
			closeModal={jest.fn()}
			namespace="_com_liferay_style_book_web_"
			onSuccess={jest.fn()}
			styleBookEntryId={1}
			tokenSets={tokenSets}
		/>
	);

const fillRequiredFields = async () => {
	await userEvent.type(
		screen.getByLabelText('token-name', {exact: false}),
		'My Token'
	);

	await userEvent.type(
		screen.getByLabelText('value', {exact: false}),
		'#FFF456'
	);
};

describe('NewTokenModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('disables the Create Token button once the request is sent', async () => {
		fetch.mockResponseOnce(() => new Promise(() => {}));

		renderComponent();

		await fillRequiredFields();

		await userEvent.click(screen.getByText('create-token'));

		await waitFor(() =>
			expect(
				screen.getByRole('button', {name: 'create-token'})
			).toBeDisabled()
		);
	});

	it('keeps the Create Token button enabled after a failed request', async () => {
		fetch.mockResponseOnce(JSON.stringify({error: 'an-error-occurred'}));

		renderComponent();

		await fillRequiredFields();

		await userEvent.click(screen.getByText('create-token'));

		await waitFor(() =>
			expect(openToast).toHaveBeenCalledWith({
				message: 'an-error-occurred',
				type: 'danger',
			})
		);
		expect(
			screen.getByRole('button', {name: 'create-token'})
		).toBeEnabled();
	});

	it('disables the Create Token button until the required fields are filled', async () => {
		renderComponent();

		await waitFor(() =>
			expect(
				screen.getByRole('button', {name: 'create-token'})
			).toBeDisabled()
		);

		await fillRequiredFields();

		await waitFor(() =>
			expect(
				screen.getByRole('button', {name: 'create-token'})
			).toBeEnabled()
		);
	});

	it('requires a token set when the category has none', async () => {
		renderComponent({tokenSets: []});

		await fillRequiredFields();

		fireEvent.blur(screen.getByLabelText('token-set', {exact: false}));

		expect(
			await screen.findByText('this-field-is-required')
		).toBeInTheDocument();
	});

	it('requires a value', async () => {
		renderComponent();

		await userEvent.type(
			screen.getByLabelText('token-name', {exact: false}),
			'My Token'
		);

		const valueInput = screen.getByLabelText('value', {exact: false});

		await userEvent.click(valueInput);
		fireEvent.blur(valueInput);

		expect(
			await screen.findByText('this-field-is-required')
		).toBeInTheDocument();
	});

	describe('when sending the add-token request', () => {
		beforeEach(() => {
			Liferay.Util.ns.mockImplementation((namespace, object) => {
				const value = {};

				Object.keys(object).forEach((key) => {
					value[`${namespace}${key}`] = object[key];
				});

				return value;
			});
		});

		afterEach(() => {
			Liferay.Util.ns.mockReturnValue({});
		});

		it('sends the category label and the selected token set label and description', async () => {
			fetch.mockResponseOnce(
				JSON.stringify({
					customFrontendTokenDefinition: {},
					frontendTokensValues: {},
				})
			);

			renderComponent();

			await fillRequiredFields();

			await userEvent.click(screen.getByText('create-token'));

			await waitFor(() => expect(fetch).toHaveBeenCalled());

			const formData = fetch.mock.calls[0][1].body;

			expect(
				formData.get('_com_liferay_style_book_web_categoryLabel')
			).toBe('Category 1');
			expect(
				formData.get('_com_liferay_style_book_web_tokenSetLabel')
			).toBe('Set 1');
			expect(
				formData.get('_com_liferay_style_book_web_tokenSetDescription')
			).toBe('Set 1 description');
		});

		it('shows a success toast', async () => {
			fetch.mockResponseOnce(
				JSON.stringify({
					customFrontendTokenDefinition: {},
					frontendTokensValues: {},
				})
			);

			renderComponent();

			await fillRequiredFields();

			await userEvent.click(screen.getByText('create-token'));

			await waitFor(() =>
				expect(openToast).toHaveBeenCalledWith({
					message: 'your-request-completed-successfully',
					type: 'success',
				})
			);
		});
	});
});
