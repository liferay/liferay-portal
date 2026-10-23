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

		expect(await screen.findByRole('alert')).toHaveTextContent(
			'Data mask not found'
		);
		expect(navigate).toHaveBeenCalledWith(BACK_URL);
	});
});
