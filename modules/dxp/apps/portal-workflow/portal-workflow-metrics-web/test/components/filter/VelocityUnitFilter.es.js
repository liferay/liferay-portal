/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, render} from '@testing-library/react';
import React from 'react';

import VelocityUnitFilter from '../../../src/main/resources/META-INF/resources/js/components/filter/VelocityUnitFilter.es';
import {MockRouter} from '../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const query = '?filters.velocityUnit%5B0%5D=weeks';

const wrapper = ({children}) => (
	<MockRouter query={query}>{children}</MockRouter>
);

describe('The velocity unit filter component should', () => {
	afterEach(cleanup);

	beforeEach(() => {
		render(
			<VelocityUnitFilter
				processId={12345}
				timeRange={{
					dateEnd: '2019-12-10T20:19:34Z',
					dateStart: '2019-09-12T00:00:00Z',
				}}
			/>,
			{wrapper}
		);
	});

	test('Be rendered with filter item names', () => {
		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0]).toHaveTextContent('inst-day');
		expect(filterItems[1]).toHaveTextContent('inst-week');
		expect(filterItems[2]).toHaveTextContent('inst-month');
	});

	test('Be rendered with active option "Weeks"', async () => {
		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('inst-week');
	});
});
