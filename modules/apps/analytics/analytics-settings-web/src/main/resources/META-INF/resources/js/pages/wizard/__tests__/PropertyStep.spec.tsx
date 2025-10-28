/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import fetch from 'jest-fetch-mock';
import React from 'react';

import PropertyStep from '../PropertyStep';

const response = {
	actions: {},
	facets: [],
	items: [],
	lastPage: 1,
	page: 1,
	pageSize: 20,
	totalCount: 3,
};

describe('Property Step', () => {
	afterEach(() => {
		jest.restoreAllMocks();
	});

	it('render PropertyStep without crashing', async () => {
		await act(async () => {
			fetch.mockResponseOnce(JSON.stringify(response));

			const {container, getByText} = render(
				<PropertyStep onCancel={() => {}} onChangeStep={() => {}} />
			);

			const propertyStepTitle = getByText('property-assignment');

			const propertyStepDescription = getByText('property-description');

			expect(propertyStepTitle).toBeInTheDocument();

			expect(propertyStepDescription).toBeInTheDocument();

			expect(container.firstChild).toHaveClass('sheet');
		});
	});
});
