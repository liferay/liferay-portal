/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import ProcessStepFilter from '../../../src/main/resources/META-INF/resources/js/components/filter/ProcessStepFilter.es';
import {MockRouter} from '../../mock/MockRouter.es';

const query = '?filters.taskNames%5B0%5D=update';

const items = [
	{label: 'Review', name: 'review'},
	{label: 'Update', name: 'update'},
];

const wrapper = ({children}) => (
	<MockRouter query={query}>{children}</MockRouter>
);

describe('The process step filter component should', () => {
	afterEach(cleanup);

	beforeEach(async () => {
		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items, totalCount: items.length}),
			ok: true,
		});

		render(<ProcessStepFilter processId={12345} />, {
			wrapper,
		});

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with filter item names', () => {
		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0].innerHTML).toContain('Review');
		expect(filterItems[1].innerHTML).toContain('Update');
	});

	it('Be rendered with active option "Update"', () => {
		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('Update');
	});
});
