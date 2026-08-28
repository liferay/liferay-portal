/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {act, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import SitesSelector from '../../../../src/main/resources/META-INF/resources/js/components/sites/SitesSelector';
import {Site} from '../../../../src/main/resources/META-INF/resources/js/types';

const mockConnectedSites: Site[] = [
	{
		descriptiveName: 'Connected Site 1',
		externalReferenceCode: 'connected-erc-1',
		id: '1',
		logo: 'logo1.png',
		name: 'Connected Site 1',
		searchable: true,
	},
	{
		descriptiveName: 'Connected Site 2',
		externalReferenceCode: 'connected-erc-2',
		id: '2',
		logo: 'logo2.png',
		name: 'Connected Site 2',
		searchable: true,
	},
];

const mockUnconnectedSite: Site = {
	descriptiveName: 'Unconnected Site',
	externalReferenceCode: 'unconnected-erc',
	id: '3',
	logo: 'logo3.png',
	name: 'Unconnected Site',
	searchable: true,
};

const DEFAULT_PROPS = {
	connectSite: jest.fn(),
	connectedSites: mockConnectedSites,
};

const mockSitesFetch = (sites: Site[] = [mockUnconnectedSite]) => {
	fetch.mockResponseOnce(
		JSON.stringify({items: sites, lastPage: 1, page: 1}),
		{headers: {'Content-Type': 'application/json'}}
	);
};

describe('SitesSelector', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;
	const escapeHTMLOriginal = Liferay.Util.escapeHTML;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));

		// The shared Liferay mock does not expose Util.escapeHTML.

		Liferay.Util.escapeHTML = jest.fn((value: string) => value);
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
		Liferay.Util.escapeHTML = escapeHTMLOriginal;
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('has no accessibility violations', async () => {
		mockSitesFetch();

		const {container} = render(<SitesSelector {...DEFAULT_PROPS} />);

		await act(async () => {
			await checkAccessibility({
				bestPractices: true,
				context: container,
			});
		});
	});

	it('excludes already-connected sites from the site request', async () => {
		mockSitesFetch();

		render(<SitesSelector {...DEFAULT_PROPS} />);

		await userEvent.click(screen.getByPlaceholderText('select-a-site'));

		await waitFor(() => {
			expect(fetch).toHaveBeenCalledTimes(1);
		});

		expect(fetch).toHaveBeenLastCalledWith(
			expect.stringContaining('/o/headless-admin-site/v1.0/sites'),
			expect.any(Object)
		);

		const sitesURL = new URL(fetch.mock.calls[0][0] as string);

		expect(
			sitesURL.searchParams.getAll('excludedExternalReferenceCodes')
		).toEqual(mockConnectedSites.map((site) => site.externalReferenceCode));
	});

	it('disables the connect button until an unconnected site is selected', async () => {
		mockSitesFetch();

		render(<SitesSelector {...DEFAULT_PROPS} />);

		expect(screen.getByRole('button', {name: 'connect'})).toBeDisabled();

		await userEvent.click(screen.getByPlaceholderText('select-a-site'));

		await userEvent.click(
			await screen.findByRole('option', {
				name: mockUnconnectedSite.descriptiveName,
			})
		);

		expect(screen.getByRole('button', {name: 'connect'})).toBeEnabled();
	});
});
