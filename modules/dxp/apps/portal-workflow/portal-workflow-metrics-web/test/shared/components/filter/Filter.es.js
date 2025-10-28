/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import Filter from '../../../../src/main/resources/META-INF/resources/js/shared/components/filter/Filter.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

describe('The filter component should', () => {
	let items;

	beforeEach(() => {
		items = [
			{active: false, key: 'overdue', name: 'Overdue'},
			{active: false, key: 'onTime', name: 'OnTime'},
			{active: false, key: 'untracked', name: 'Untracked'},
		];
	});

	afterEach(cleanup);

	test('Be rendered with filter item names and default item selected', async () => {
		render(
			<MockRouter>
				<Filter
					defaultItem={items[2]}
					filterKey="statuses"
					items={items}
					multiple={false}
					name="status"
				/>
			</MockRouter>
		);

		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0]).toHaveTextContent('Overdue');
		expect(filterItems[1]).toHaveTextContent('OnTime');
		expect(filterItems[2]).toHaveTextContent('Untracked');

		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('Untracked');
	});

	test('Be rendered with other item selected', async () => {
		items[0].active = true;

		render(
			<MockRouter>
				<Filter
					defaultItem={items[2]}
					filterKey="statuses"
					items={items}
					multiple={false}
					name="status"
				/>
			</MockRouter>
		);

		const activeItem = document.querySelector('.active');

		expect(activeItem).toHaveTextContent('Overdue');
	});

	test('Be rendered with search field and filtering options', async () => {
		const mappedItems = [{active: false, key: 'overdue', name: 'Overdue'}];

		for (let i = 0; i < 12; i++) {
			mappedItems.push({
				active: false,
				key: `${i}`,
				name: `${i}test${i}`,
			});
		}

		const {container, getByPlaceholderText} = render(
			<MockRouter>
				<Filter
					defaultItem={mappedItems[0]}
					filterKey="statuses"
					items={mappedItems}
					multiple={false}
					name="status"
				/>
			</MockRouter>
		);

		const filterBtn = container.querySelectorAll('.dropdown-toggle')[0];

		fireEvent.click(filterBtn);

		const filterSearch = getByPlaceholderText('search-for');
		let filterItems = document.querySelectorAll('.dropdown-item');

		filterItems.forEach((item, index) => {
			expect(item).toHaveTextContent(mappedItems[index].name);
		});

		fireEvent.change(filterSearch, {target: {value: 'Over'}});

		filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems.length).toEqual(1);
		expect(filterItems[0].className.includes('active')).toBe(true);
		expect(filterItems[0]).toHaveTextContent('Overdue');

		fireEvent.change(filterSearch, {target: {value: 'test'}});

		filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems.length).toEqual(12);
		expect(filterItems[0]).toHaveTextContent('0test0');

		fireEvent.click(filterItems[10]);

		expect(filterItems[0].className.includes('active')).toBe(false);
	});

	test('Be rendered with multiple options', async () => {
		items.forEach((item) => {
			item.active = true;
		});

		const {container} = render(
			<MockRouter>
				<Filter
					filterKey="statuses"
					items={items}
					multiple={true}
					name="status"
					onChangeFilter={() => true}
				/>
			</MockRouter>
		);

		const filterBtn = container.querySelectorAll('.dropdown-toggle')[0];

		fireEvent.click(filterBtn);

		const filterItems = document.querySelectorAll('.dropdown-item');

		expect(filterItems[0].className.includes('active')).toBe(true);
		expect(filterItems[1].className.includes('active')).toBe(true);
		expect(filterItems[2].className.includes('active')).toBe(true);

		await fireEvent.click(filterItems[2]);

		expect(filterItems[0].className.includes('active')).toBe(true);
		expect(filterItems[1].className.includes('active')).toBe(true);
	});
});
