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

	it('generates the profile path from the title until it is edited by hand', async () => {
		renderEditProfileInfo();

		const profilePathInput = screen.getByRole('textbox', {
			name: /^url/,
		});

		await userEvent.type(
			screen.getByRole('textbox', {name: /^title/}),
			'My Team / Docs'
		);

		expect(profilePathInput).toHaveValue('my-team-docs');
		expect(
			screen.getByText(`${window.location.origin}/o/mcp/`)
		).toBeInTheDocument();

		await userEvent.clear(profilePathInput);
		await userEvent.type(profilePathInput, 'docs');
		await userEvent.type(
			screen.getByRole('textbox', {name: /^title/}),
			' Changed'
		);

		expect(profilePathInput).toHaveValue('docs');
	});

	it('keeps the profile path of an existing profile when the title changes', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({
				description: 'Description',
				externalReferenceCode: 'PROFILE_ERC',
				name: 'Docs',
				profilePath: 'docs',
				profileStatus: {key: 'inactive'},
			})
		);

		renderEditProfileInfo('PROFILE_ERC');

		const titleInput = await screen.findByRole('textbox', {
			name: /^title/,
		});

		await userEvent.type(titleInput, ' Changed');

		expect(screen.getByRole('textbox', {name: /^url/})).toHaveValue('docs');
	});

	it('serves the default profile at the base URL without a profile path', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({
				description: 'Description',
				externalReferenceCode: 'L_MCP_SERVER_DEFAULT_PROFILE',
				name: 'Default',
				profilePath: '',
				profileStatus: {key: 'active'},
			})
		);

		renderEditProfileInfo('L_MCP_SERVER_DEFAULT_PROFILE');

		const profilePathInput = await screen.findByRole('textbox', {
			name: /^url/,
		});

		expect(profilePathInput).toBeDisabled();
		expect(profilePathInput).toHaveValue('');
		expect(
			screen.getByText(`${window.location.origin}/o/mcp`)
		).toBeInTheDocument();
		expect(
			screen.getByText('the-default-profile-has-no-profile-path')
		).toBeInTheDocument();
	});

	it('copies the base URL of the default profile', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({
				description: 'Description',
				externalReferenceCode: 'L_MCP_SERVER_DEFAULT_PROFILE',
				name: 'Default',
				profilePath: '',
				profileStatus: {key: 'active'},
			})
		);

		const user = userEvent.setup();

		renderEditProfileInfo('L_MCP_SERVER_DEFAULT_PROFILE');

		await user.click(await screen.findByRole('button', {name: 'copy-url'}));

		expect(await navigator.clipboard.readText()).toBe(
			`${window.location.origin}/o/mcp`
		);

		expect(
			await screen.findByText('copied-to-clipboard')
		).toBeInTheDocument();
	});

	it('copies the URL of the profile path', async () => {
		const user = userEvent.setup();

		renderEditProfileInfo();

		const copyButton = screen.getByRole('button', {name: 'copy-url'});

		expect(copyButton).toBeDisabled();

		await user.type(
			screen.getByRole('textbox', {name: /^title/}),
			'My Docs'
		);
		await user.click(copyButton);

		expect(await navigator.clipboard.readText()).toBe(
			`${window.location.origin}/o/mcp/my-docs`
		);
	});

	it('keeps only valid characters in the profile path while typing', async () => {
		renderEditProfileInfo();

		const profilePathInput = screen.getByRole('textbox', {
			name: /^url/,
		});

		await userEvent.type(profilePathInput, 'My Docs/V2_');

		expect(profilePathInput).toHaveValue('my-docsv2');
	});

	it('shows a format error for an invalid profile path', async () => {
		renderEditProfileInfo();

		const profilePathInput = screen.getByRole('textbox', {
			name: /^url/,
		});

		await userEvent.type(profilePathInput, 'invalid--path-');
		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(profilePathInput).toHaveAccessibleDescription(
			/please-enter-a-valid-profile-path/
		);
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

		expect(
			await screen.findByText('Profile not found')
		).toBeInTheDocument();
		expect(navigate).toHaveBeenCalledWith(BACK_URL);
	});
});
