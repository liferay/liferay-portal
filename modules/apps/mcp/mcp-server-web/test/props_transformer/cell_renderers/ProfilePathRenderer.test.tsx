/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import '@testing-library/jest-dom';

import ProfilePathRenderer from '../../../src/main/resources/META-INF/resources/js/props_transformer/cell_renderers/ProfilePathRenderer';

import type {Profile} from '../../../src/main/resources/META-INF/resources/js/types/Profile';

describe('ProfilePathRenderer', () => {
	it('copies the URL of the profile', async () => {
		const user = userEvent.setup();

		render(
			<ProfilePathRenderer
				itemData={{
					description: 'Description',
					name: 'Tool Search',
					profilePath: 'tool-search',
				}}
			/>
		);

		expect(screen.getByText('/tool-search')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: 'copy-url'}));

		expect(await navigator.clipboard.readText()).toBe(
			`${window.location.origin}/o/mcp/tool-search`
		);
		expect(
			await screen.findByText('copied-to-clipboard')
		).toBeInTheDocument();
	});

	it('renders a missing profile path as the base URL', () => {
		render(
			<ProfilePathRenderer
				itemData={
					{
						description: 'Description',
						name: 'Profile',
					} as Profile
				}
			/>
		);

		expect(screen.getByText('/')).toBeInTheDocument();
	});

	it('copies the base URL of the default profile', async () => {
		const user = userEvent.setup();

		render(
			<ProfilePathRenderer
				itemData={{
					description: 'Description',
					name: 'Default',
					profilePath: '',
				}}
			/>
		);

		expect(screen.getByText('/')).toBeInTheDocument();

		await user.click(screen.getByRole('button', {name: 'copy-url'}));

		expect(await navigator.clipboard.readText()).toBe(
			`${window.location.origin}/o/mcp`
		);
	});
});
