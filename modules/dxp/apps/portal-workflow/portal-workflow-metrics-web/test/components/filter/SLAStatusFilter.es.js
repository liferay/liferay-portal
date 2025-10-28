/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import SLAStatusFilter from '../../../src/main/resources/META-INF/resources/js/components/filter/SLAStatusFilter.es';
import {MockRouter} from '../../mock/MockRouter.es';

const query = '?filters.slaStatuses%5B0%5D=Overdue';

const wrapper = ({children}) => (
	<MockRouter query={query}>{children}</MockRouter>
);

describe('The sla status filter component should', () => {
	afterEach(cleanup);

	beforeEach(() => {
		render(<SLAStatusFilter processId={12345} />, {
			wrapper,
		});
	});

	test('Be rendered with filter item names', () => {
		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0].innerHTML).toContain('on-time');
		expect(filterItems[1].innerHTML).toContain('overdue');
		expect(filterItems[2].innerHTML).toContain('untracked');
	});

	test('Be rendered with active option "Overdue"', () => {
		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('overdue');
	});
});
