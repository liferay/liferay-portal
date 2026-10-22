/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import PersonalMenu from '../../src/main/resources/META-INF/resources/js/PersonalMenu';

const ITEMS = [
	{
		items: [{href: '#', label: 'My Profile'}],
		label: 'Test Test',
		type: 'group',
	},
	{type: 'divider'},
	{
		items: [{href: '#', label: 'Sign Out'}],
		type: 'group',
	},
];

function renderPersonalMenu({showColorSchemeButton = true} = {}) {
	fetch.mockResponseOnce(JSON.stringify(ITEMS));

	render(
		<PersonalMenu
			itemsURL="http://localhost/personal-menu-items"
			showColorSchemeButton={showColorSchemeButton}
			userName="Test Test"
		/>
	);
}

describe('PersonalMenu', () => {
	beforeAll(() => {
		Liferay.Util.Session = Liferay.Session;
	});

	afterAll(() => {
		delete Liferay.Util.Session;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		document.documentElement.dataset.colorScheme = 'light';
	});

	afterEach(() => {
		delete document.documentElement.dataset.colorScheme;
	});

	it('renders the color scheme switch next to the user name', async () => {
		renderPersonalMenu();

		await userEvent.click(screen.getByTitle('user-profile-menu'));

		expect(
			await screen.findByRole('button', {name: 'switch-to-dark-mode'})
		).toHaveAttribute('aria-pressed', 'false');
		expect(
			screen.getByRole('menuitem', {name: 'My Profile'})
		).toBeInTheDocument();
	});

	it('keeps the focus on the trigger when opened with the mouse', async () => {
		renderPersonalMenu();

		const trigger = screen.getByTitle('user-profile-menu');

		await userEvent.click(trigger);

		await screen.findByRole('button', {name: 'switch-to-dark-mode'});

		await waitFor(() => expect(trigger).toHaveFocus());
	});

	it('moves the focus to the color scheme button when opened with the keyboard', async () => {
		renderPersonalMenu();

		const trigger = screen.getByTitle('user-profile-menu');

		trigger.focus();

		await userEvent.keyboard('{Enter}');

		await waitFor(() =>
			expect(
				screen.getByRole('button', {name: 'switch-to-dark-mode'})
			).toHaveFocus()
		);
	});

	it('switches the color scheme and stores it in the session', async () => {
		renderPersonalMenu();

		await userEvent.click(screen.getByTitle('user-profile-menu'));
		await userEvent.click(
			await screen.findByRole('button', {name: 'switch-to-dark-mode'})
		);

		expect(document.documentElement.dataset.colorScheme).toBe('dark');
		expect(
			screen.getByRole('button', {name: 'switch-to-light-mode'})
		).toHaveAttribute('aria-pressed', 'true');
	});

	it('does not render the switch for users without administration access', async () => {
		renderPersonalMenu({showColorSchemeButton: false});

		await userEvent.click(screen.getByTitle('user-profile-menu'));

		await screen.findByRole('menuitem', {name: 'My Profile'});

		expect(
			screen.queryByRole('button', {name: 'switch-to-dark-mode'})
		).not.toBeInTheDocument();
	});
});
