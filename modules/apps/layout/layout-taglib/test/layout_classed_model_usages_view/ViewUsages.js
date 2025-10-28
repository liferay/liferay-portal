/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render, screen} from '@testing-library/react';
import React from 'react';

import ViewUsages from '../../src/main/resources/META-INF/resources/js/layout_classed_model_usages_view/ViewUsages';

const renderViewUsages = () => {
	return render(<ViewUsages getUsagesURL="http://example.com" />);
};

global.fetch.enableFetchMocks();

describe('ViewUsages', () => {
	it('renders empty if there are no usages', async () => {
		global.fetch.mockReturnValue(
			Promise.resolve({
				json: jest.fn(() => ({totalNumberOfPages: 0, usages: []})),
			})
		);

		await act(async () => {
			renderViewUsages();
		});

		expect(screen.getByText('there-are-no-usages')).toBeInTheDocument();
	});

	it('renders the usages', async () => {
		global.fetch.mockReturnValue(
			Promise.resolve({
				json: jest.fn(() => ({
					totalNumberOfPages: 0,
					usages: [
						{id: 0, name: 'usage0', type: 'type', url: 'url'},
						{id: 1, name: 'usage1', type: 'type', url: 'url'},
					],
				})),
			})
		);

		await act(async () => {
			renderViewUsages();
		});

		expect(screen.getByText('usage0')).toBeInTheDocument();
		expect(screen.getByText('usage1')).toBeInTheDocument();
	});
});
