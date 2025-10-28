/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import ProcessStatusFilter from '../../../src/main/resources/META-INF/resources/js/components/filter/ProcessStatusFilter.es';
import {MockRouter} from '../../mock/MockRouter.es';

const query = '?filters.statuses%5B0%5D=Completed';

const wrapper = ({children}) => (
	<MockRouter query={query}>{children}</MockRouter>
);

describe('The process status filter component should', () => {
	afterEach(cleanup);

	beforeEach(() => {
		render(<ProcessStatusFilter processId={12345} />, {
			wrapper,
		});
	});

	test('Be rendered with filter item names', () => {
		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0].innerHTML).toContain('completed');
		expect(filterItems[1].innerHTML).toContain('pending');
	});

	test('Be rendered with active option "Completed"', async () => {
		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('completed');
	});
});
