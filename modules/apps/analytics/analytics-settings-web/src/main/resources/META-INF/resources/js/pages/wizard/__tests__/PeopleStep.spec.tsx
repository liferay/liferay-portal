/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';

import PeopleStep from '../PeopleStep';

const response = {
	syncAllAccounts: false,
	syncAllContacts: false,
	syncedAccountGroupIds: [],
	syncedOrganizationIds: [],
	syncedUserGroupIds: [],
};

describe('People Step', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('render PeopleStep without crashing', async () => {
		fetch.mockResponseOnce(JSON.stringify(response));

		await act(async () => {
			const {container, getByText} = render(
				<PeopleStep onCancel={() => {}} onChangeStep={() => {}} />
			);

			const peopleStepTitle = getByText('sync-people');

			const peopleStepDescription = getByText('sync-people-description');

			expect(peopleStepTitle).toBeInTheDocument();

			expect(peopleStepDescription).toBeInTheDocument();

			expect(container.firstChild).toHaveClass('sheet');
		});
	});
});
