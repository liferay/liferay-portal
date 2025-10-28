/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import fetch from 'jest-fetch-mock';

import PeoplePage from '../PeoplePage';

const response = {
	syncAllAccounts: false,
	syncAllContacts: false,
	syncedAccountGroupIds: [],
	syncedOrganizationIds: [],
	syncedUserGroupIds: [],
};

describe('PeoplePage', () => {
	it('renders page title and description', async () => {
		fetch.mockResponseOnce(JSON.stringify(response));

		await act(async () => {
			render(<PeoplePage title="People" />);
		});

		const title = screen.getByText('People');
		const description = screen.getByText('sync-people-description');

		expect(title).toBeInTheDocument();
		expect(description).toBeInTheDocument();
	});
});
