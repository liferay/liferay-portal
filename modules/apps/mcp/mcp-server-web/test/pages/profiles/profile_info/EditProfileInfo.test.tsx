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

import EditProfileInfo from '../../../../src/main/resources/META-INF/resources/js/pages/profiles/profile_info/EditProfileInfo';

const BACK_URL = '/profiles';

function renderEditProfileInfo(profileERC = '') {
	render(
		<EditProfileInfo
			backURL={BACK_URL}
			editProfileURL="/edit-profile"
			portletNamespace="_portlet_"
			profileERC={profileERC}
		/>
	);
}

describe('EditProfileInfo', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('moves the focus to the first invalid field when saving', async () => {
		renderEditProfileInfo();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(screen.getByRole('textbox', {name: /^title/})).toHaveFocus()
		);
	});

	it('skips the valid fields when moving the focus', async () => {
		renderEditProfileInfo();

		await userEvent.type(
			screen.getByRole('textbox', {name: /^title/}),
			'Profile'
		);
		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(
				screen.getByRole('textbox', {name: /^description/})
			).toHaveFocus()
		);
	});

	it('leaves the focus where the user types after saving', async () => {
		renderEditProfileInfo();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		const titleInput = screen.getByRole('textbox', {name: /^title/});

		await waitFor(() => expect(titleInput).toHaveFocus());

		const descriptionInput = screen.getByRole('textbox', {
			name: /^description/,
		});

		await userEvent.click(descriptionInput);
		await userEvent.type(descriptionInput, 'Profile description');

		expect(descriptionInput).toHaveFocus();
	});

	it('clears the required error once the field is filled', async () => {
		renderEditProfileInfo();

		const titleInput = screen.getByRole('textbox', {name: /^title/});

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(titleInput).toHaveAccessibleDescription(
			/this-field-is-required/
		);

		await userEvent.type(titleInput, 'Profile');

		expect(titleInput).not.toHaveAccessibleDescription(
			/this-field-is-required/
		);
	});

	it('goes back with an error toast when the profile cannot be loaded', async () => {
		fetch.mockResponseOnce(JSON.stringify({title: 'Profile not found'}), {
			status: 404,
		});

		renderEditProfileInfo('PROFILE_ERC');

		expect(await screen.findByRole('alert')).toHaveTextContent(
			'Profile not found'
		);
		expect(navigate).toHaveBeenCalledWith(BACK_URL);
	});
});
