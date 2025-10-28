/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import fetch from 'jest-fetch-mock';

import PropertiesPage from '../PropertiesPage';

const response = {
	actions: {},
	facets: [],
	items: [],
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: 2,
};

describe('PropertiesPage', () => {
	it('renders page title and description', async () => {
		fetch.mockResponseOnce(JSON.stringify(response));

		await act(async () => {
			render(<PropertiesPage title="Properties" />);
		});

		const title = screen.getByText('Properties');
		const description = screen.getByText('property-description');

		expect(title).toBeInTheDocument();
		expect(description).toBeInTheDocument();
	});
});
