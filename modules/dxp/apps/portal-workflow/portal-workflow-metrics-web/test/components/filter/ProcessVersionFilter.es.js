/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, render} from '@testing-library/react';
import React from 'react';

import ProcessVersionFilter from '../../../src/main/resources/META-INF/resources/js/components/filter/ProcessVersionFilter.es';
import {MockRouter} from '../../mock/MockRouter.es';

const query = '?filters.processVersion%5B0%5D=1.0';

const items = [{name: '1.0'}, {name: '2.0'}];

const wrapper = ({children}) => (
	<MockRouter query={query}>{children}</MockRouter>
);

describe('The process version filter component should', () => {
	beforeEach(async () => {
		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items, totalCount: items.length}),
			ok: true,
		});

		render(
			<ProcessVersionFilter
				options={{
					hideControl: true,
					multiple: false,
					withAllVersions: true,
					withSelectionTitle: true,
				}}
				processId={12345}
			/>,
			{
				wrapper,
			}
		);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Render with filter item names', () => {
		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0].innerHTML).toContain('all-versions');
		expect(filterItems[1].innerHTML).toContain('1.0');
		expect(filterItems[2].innerHTML).toContain('2.0');
	});

	it('Render with active option "1.0"', () => {
		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('1.0');
	});
});
