/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import AssigneeFilter from '../../../src/main/resources/META-INF/resources/js/components/filter/AssigneeFilter.es';
import {MockRouter} from '../../mock/MockRouter.es';

const query = '?filters.assigneeIds%5B0%5D=1';

const items = [
	{id: 1, name: 'User 1'},
	{id: 2, name: 'User 2'},
];

const wrapper = ({children}) => (
	<MockRouter query={query}>{children}</MockRouter>
);

describe('The assignee filter component should', () => {
	afterEach(cleanup);

	beforeEach(async () => {
		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items, totalCount: items.length}),
			ok: true,
		});

		render(<AssigneeFilter processId={12345} />, {
			wrapper,
		});

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with filter item names', () => {
		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0].innerHTML).toContain('unassigned');
		expect(filterItems[1].innerHTML).toContain('User 1');
		expect(filterItems[2].innerHTML).toContain('User 2');
	});

	it('Be rendered with active option "User 1"', () => {
		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('User 1');
	});
});
