/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import fetch from 'jest-fetch-mock';

import AttributesPage from '../AttributesPage';

const response = {
	account: 25,
	order: 0,
	people: 44,
	product: 34,
};

describe('AttributesPage', () => {
	it('renders page title and description', async () => {
		fetch.mockResponseOnce(JSON.stringify(response));

		await act(async () => {
			render(<AttributesPage title="Attributes" />);
		});

		const title = screen.getByText('Attributes');
		const description = screen.getByText('attributes-step-description');

		expect(title).toBeInTheDocument();
		expect(description).toBeInTheDocument();
	});
});
