/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {act, fireEvent, render} from '@testing-library/react';
import fetch from 'jest-fetch-mock';

import Attributes from '../../../components/attributes/Attributes';
import People from '../../../components/people/People';
import Properties from '../../../components/properties/Properties';
import {fetchPropertiesResponse} from '../../../utils/__tests__/mocks';
import DefaultPage from '../DefaultPage';

describe('DefaultPage', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('renders DefaultPage component without crashing ', () => {
		const {getByText} = render(<DefaultPage />);

		const menu = getByText('Menu');

		expect(menu).toBeInTheDocument();
	});

	it('renders the Workspace Connection page based on the selected menu item', async () => {
		const {findByRole} = render(<DefaultPage />);

		fireEvent.click(
			await findByRole('menuitem', {
				name: 'workspace-connection',
			})
		);

		const title = document.querySelector('.sheet-title');
		expect(title).toBeInTheDocument();
		expect(title?.textContent).toBe('workspace-connection');
	});

	it('renders the People page based on the selected menu item', async () => {
		await act(async () => {
			const {findByRole} = render(<DefaultPage />);

			fireEvent.click(
				await findByRole('menuitem', {
					name: 'people',
				})
			);

			fetch.mockResponse(
				JSON.stringify({
					syncAllAccounts: false,
					syncAllContacts: false,
					syncedAccountGroupIds: [],
					syncedOrganizationIds: [],
					syncedUserGroupIds: [],
				})
			);
			render(<People />);

			const title = document.querySelector('.sheet-title');
			expect(title).toBeInTheDocument();
			expect(title?.textContent).toBe('people');
		});
	});

	it('renders the Properties page based on the selected menu item', async () => {
		await act(async () => {
			const {findByRole} = render(<DefaultPage />);
			fireEvent.click(
				await findByRole('menuitem', {
					name: 'properties',
				})
			);

			fetch.mockResponse(JSON.stringify(fetchPropertiesResponse));
			render(<Properties />);

			const title = document.querySelector('.sheet-title');
			expect(title).toBeInTheDocument();
			expect(title?.textContent).toBe('properties');
		});
	});

	it('renders the Attributes page based on the selected menu item', async () => {
		await act(async () => {
			const {findByRole} = render(<DefaultPage />);

			fireEvent.click(
				await findByRole('menuitem', {
					name: 'attributes',
				})
			);

			fetch.mockResponse(
				JSON.stringify({
					account: 25,
					order: 35,
					people: 44,
					product: 34,
				})
			);
			render(<Attributes />);

			const title = document.querySelector('.sheet-title');
			expect(title).toBeInTheDocument();
			expect(title?.textContent).toBe('attributes');
		});
	});
});
