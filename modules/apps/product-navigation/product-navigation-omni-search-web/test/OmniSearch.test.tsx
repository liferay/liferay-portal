/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch, navigate} from 'frontend-js-web';
import React from 'react';

import OmniSearch from '../src/main/resources/META-INF/resources/js/OmniSearch';

const RESULTS_URL = '/omni-search-results?p_p_id=foo';

const RESULTS_RESPONSE = [
	{
		icon: 'grid',
		omniSearchResults: [
			{
				description: 'Control Panel › Users',
				icon: 'grid',
				title: 'Users and Organizations',
				type: 'ENTRY',
				url: '/users-admin',
			},
			{
				description: 'Control Panel › Sites',
				icon: 'grid',
				title: 'Sites',
				type: 'ENTRY',
				url: '/sites-admin',
			},
			{
				description: 'Applications › Content',
				icon: 'grid',
				title: 'Content Dashboard',
				type: 'ENTRY',
				url: '/content-dashboard',
			},
			{
				description: 'Control Panel › Configuration',
				icon: 'grid',
				title: 'Instance Settings',
				type: 'ENTRY',
				url: '/instance-settings',
			},
			{
				description: 'Site Administration › People',
				icon: 'grid',
				title: 'Memberships',
				type: 'ENTRY',
				url: '/memberships',
			},
		],
		title: 'Navigation',
		type: 'SECTION',
	},
	{
		icon: 'cog',
		omniSearchResults: [
			{
				description: 'System Settings › LDAP',
				icon: 'cog',
				title: 'Authentication',
				type: 'ENTRY',
				url: '/ldap-authentication',
			},
		],
		title: 'Settings',
		type: 'SECTION',
	},
	{
		icon: 'search',
		omniSearchResults: [
			{
				description: 'Web Content',
				icon: 'web-content',
				title: 'Welcome Article',
				type: 'ENTRY',
				url: '/edit/welcome-article',
			},
			{
				description: 'Documents and Media',
				icon: 'document',
				title: 'Report.pdf',
				type: 'ENTRY',
				url: '/edit/report',
			},
		],
		title: 'Results (2)',
		type: 'SECTION',
	},
];

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(),
	navigate: jest.fn(),
}));

const mockFetchResponse = (response = RESULTS_RESPONSE) => {
	(fetch as jest.Mock).mockImplementation(() =>
		Promise.resolve({
			json: () => Promise.resolve(response),
		})
	);
};

const openModal = async () => {
	render(<OmniSearch resultsURL={RESULTS_URL} />);

	await userEvent.click(screen.getByLabelText('omni-search (Ctrl+K)'));

	return screen.findByPlaceholderText('search');
};

const searchFor = async (keywords: string) => {
	const input = await openModal();

	await userEvent.type(input, keywords);

	await waitFor(() =>
		expect(fetch).toHaveBeenCalledWith(
			expect.stringContaining(RESULTS_URL),
			expect.anything()
		)
	);

	return input;
};

describe('OmniSearch', () => {
	beforeAll(() => {
		Element.prototype.scrollIntoView = jest.fn();
	});

	beforeEach(() => {
		jest.clearAllMocks();
		mockFetchResponse();
	});

	it('opens the modal when the search button is clicked', async () => {
		const input = await openModal();

		expect(input).toBeInTheDocument();
	});

	it('opens the modal on Ctrl+K', async () => {
		render(<OmniSearch resultsURL={RESULTS_URL} />);

		await userEvent.keyboard('{Control>}k{/Control}');

		expect(
			await screen.findByPlaceholderText('search')
		).toBeInTheDocument();
	});

	it('shows no sections before a query is typed', async () => {
		await openModal();

		expect(screen.queryByRole('listbox')).not.toBeInTheDocument();
		expect(fetch).not.toHaveBeenCalled();
	});

	it('renders sections from the backend response', async () => {
		await searchFor('settings');

		expect(await screen.findByText('Navigation')).toBeInTheDocument();
		expect(screen.getByText('Settings')).toBeInTheDocument();
		expect(screen.getByText('Results (2)')).toBeInTheDocument();
		expect(screen.getByText('Instance Settings')).toBeInTheDocument();
		expect(screen.getByText('Authentication')).toBeInTheDocument();
		expect(screen.getByText('Welcome Article')).toBeInTheDocument();
	});

	it('shows the server-provided type label under a content result', async () => {
		await searchFor('report');

		expect(await screen.findByText('Report.pdf')).toBeInTheDocument();
		expect(screen.getByText('Documents and Media')).toBeInTheDocument();
	});

	it('navigates to the server-resolved URL when a result is clicked', async () => {
		await searchFor('welcome');

		await userEvent.click(await screen.findByText('Welcome Article'));

		expect(navigate).toHaveBeenCalledWith('/edit/welcome-article');
	});

	it('shows the no-results message when the search returns empty', async () => {
		mockFetchResponse([]);

		await searchFor('xyznotfound');

		expect(
			await screen.findByText('there-are-no-results')
		).toBeInTheDocument();
	});

	it('moves selection with arrow keys while input retains focus', async () => {
		const input = await searchFor('users');

		await screen.findByText('Users and Organizations');

		await userEvent.keyboard('{ArrowDown}{ArrowDown}');

		const options = screen.getAllByRole('option');

		expect(options[1]).toHaveClass('active');
		expect(options[1]).toHaveAttribute('aria-selected', 'true');
		expect(input).toHaveFocus();
	});

	it('activates the selected entry with Enter', async () => {
		await searchFor('users');

		await screen.findByText('Users and Organizations');

		await userEvent.keyboard('{ArrowDown}{Enter}');

		expect(navigate).toHaveBeenCalledWith('/users-admin');
	});

	it('keeps repeated entries as distinct options without key collisions', async () => {
		const consoleError = jest.spyOn(console, 'error');

		try {
			mockFetchResponse([
				{
					icon: 'cog',
					omniSearchResults: [
						{
							description: 'System Settings › User Activity',
							icon: 'cog',
							title: 'Social Activity',
							type: 'ENTRY',
							url: '/social-1',
						},
						{
							description: 'System Settings › User Activity',
							icon: 'cog',
							title: 'Social Activity',
							type: 'ENTRY',
							url: '/social-2',
						},
					],
					title: 'Settings',
					type: 'SECTION',
				},
			]);

			await searchFor('social');

			expect(await screen.findAllByRole('option')).toHaveLength(2);

			const collided = consoleError.mock.calls.some(
				(args) =>
					typeof args[0] === 'string' && args[0].includes('same key')
			);

			expect(collided).toBe(false);
		}
		finally {
			consoleError.mockRestore();
		}
	});

	it('has no accessibility violations when the modal is open', async () => {
		await openModal();

		await checkAccessibility({bestPractices: true, context: document.body});
	});
});
