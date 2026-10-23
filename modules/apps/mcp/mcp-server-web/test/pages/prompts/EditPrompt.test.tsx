/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {navigate} from 'frontend-js-web';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import EditPrompt from '../../../src/main/resources/META-INF/resources/js/pages/prompts/EditPrompt';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	navigate: jest.fn(),
}));

const BACK_URL = '/prompts';

describe('EditPrompt', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('generates the identifier from the name until it is edited by hand', async () => {
		render(<EditPrompt backURL={BACK_URL} promptId={0} />);

		const identifierInput = screen.getByRole('textbox', {
			name: /identifier/,
		});

		await userEvent.type(
			screen.getByRole('textbox', {name: /^name/}),
			'First Name'
		);

		expect(identifierInput).toHaveValue('first-name');

		await userEvent.clear(identifierInput);
		await userEvent.type(identifierInput, 'my-prompt');
		await userEvent.type(
			screen.getByRole('textbox', {name: /^name/}),
			' Changed'
		);

		expect(identifierInput).toHaveValue('my-prompt');
	});

	it('shows a format error for an invalid identifier', async () => {
		render(<EditPrompt backURL={BACK_URL} promptId={0} />);

		const identifierInput = screen.getByRole('textbox', {
			name: /identifier/,
		});

		await userEvent.type(identifierInput, 'Invalid_Identifier');
		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(identifierInput).toHaveAccessibleDescription(
			/please-enter-a-valid-identifier/
		);
	});

	it('clears the required error once the field is filled', async () => {
		render(<EditPrompt backURL={BACK_URL} promptId={0} />);

		const nameInput = screen.getByRole('textbox', {name: /^name/});

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(nameInput).toHaveAccessibleDescription(/this-field-is-required/);

		await userEvent.type(nameInput, 'Prompt');

		expect(nameInput).not.toHaveAccessibleDescription(
			/this-field-is-required/
		);
	});

	it('goes back with an error toast when the prompt cannot be loaded', async () => {
		fetch.mockResponseOnce(JSON.stringify({title: 'Prompt not found'}), {
			status: 404,
		});

		render(<EditPrompt backURL={BACK_URL} promptId={1} />);

		expect(await screen.findByRole('alert')).toHaveTextContent(
			'Prompt not found'
		);
		expect(navigate).toHaveBeenCalledWith(BACK_URL);
	});
});
