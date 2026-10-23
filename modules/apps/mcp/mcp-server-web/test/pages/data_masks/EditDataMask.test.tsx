/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {navigate} from 'frontend-js-web';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import EditDataMask from '../../../src/main/resources/META-INF/resources/js/pages/data_masks/EditDataMask';

const BACK_URL = '/data-masks';

describe('EditDataMask', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('requires the name, the regex pattern and the replacement', () => {
		render(<EditDataMask backURL={BACK_URL} dataMaskId={0} />);

		expect(screen.getByRole('textbox', {name: /^name/})).toBeRequired();
		expect(
			screen.getByRole('textbox', {name: /^regex-pattern/})
		).toBeRequired();
		expect(
			screen.getByRole('textbox', {name: /^replacement/})
		).toBeRequired();
		expect(
			screen.getByRole('textbox', {name: /^match-pattern/})
		).not.toBeRequired();
	});

	it('moves the focus to the first invalid field when saving', async () => {
		render(<EditDataMask backURL={BACK_URL} dataMaskId={0} />);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(screen.getByRole('textbox', {name: /^name/})).toHaveFocus()
		);
	});

	it('keeps the focus away from the regex tester output after a failed save', async () => {
		fetch.mockResponses(
			[
				JSON.stringify({
					error: 'Unclosed group near index 4',
					output: 'abc',
				}),
				{status: 200},
			],
			[JSON.stringify({title: 'Unable to save'}), {status: 400}]
		);

		render(<EditDataMask backURL={BACK_URL} dataMaskId={0} />);

		await userEvent.type(
			screen.getByRole('textbox', {name: /^name/}),
			'Mask'
		);
		await userEvent.type(
			screen.getByRole('textbox', {name: /^regex-pattern/}),
			'(abc'
		);
		await userEvent.type(
			screen.getByRole('textbox', {name: /^replacement/}),
			'***'
		);
		await userEvent.type(screen.getByLabelText('sample'), 'abc');
		await userEvent.click(screen.getByRole('button', {name: 'test'}));

		await screen.findByText('Unclosed group near index 4');

		const saveButton = screen.getByRole('button', {name: 'save'});

		await userEvent.click(saveButton);

		await screen.findByText('Unable to save');
		await waitFor(() => expect(saveButton).toBeEnabled());

		expect(screen.getByLabelText('output')).not.toHaveFocus();
	});

	it('clears the required error once the field is filled', async () => {
		render(<EditDataMask backURL={BACK_URL} dataMaskId={0} />);

		const nameInput = screen.getByRole('textbox', {name: /^name/});

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(nameInput).toHaveAccessibleDescription(/this-field-is-required/);

		await userEvent.type(nameInput, 'Mask');

		expect(nameInput).not.toHaveAccessibleDescription(
			/this-field-is-required/
		);
	});

	it('describes the regex fields with their help text', () => {
		render(<EditDataMask backURL={BACK_URL} dataMaskId={0} />);

		expect(
			screen.getByRole('textbox', {name: /^regex-pattern/})
		).toHaveAccessibleDescription(
			'use-a-standard-regular-expression-named-capture-groups-are-supported'
		);
		expect(
			screen.getByRole('textbox', {name: /^match-pattern/})
		).toHaveAccessibleDescription(
			'leave-empty-to-replace-the-entire-detected-value-with-the-replacement-token'
		);
	});

	it('goes back with an error toast when the data mask cannot be loaded', async () => {
		fetch.mockResponseOnce(JSON.stringify({title: 'Data mask not found'}), {
			status: 404,
		});

		render(<EditDataMask backURL={BACK_URL} dataMaskId={1} />);

		expect(
			(await screen.findByText('Data mask not found')).closest(
				'[role="alert"]'
			)
		).toBeInTheDocument();
		expect(navigate).toHaveBeenCalledWith(BACK_URL);
	});
});
