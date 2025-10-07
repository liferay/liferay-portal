/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import {ItemSelector} from '../src/main/resources/META-INF/resources';

type TestItem = {
	id: number;
	name: string;
};

const mockFirstItemName = 'First Item Name';
const mockSecondItemName = 'Second Item Name';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(() => {
		const headers = new Headers();
		headers.set('Content-Type', 'application/json');

		return Promise.resolve({
			headers,
			json: () =>
				Promise.resolve({
					items: [
						{
							id: 1,
							name: mockFirstItemName,
						},
						{
							id: 2,
							name: mockSecondItemName,
						},
					],
					lastPage: 1,
					page: 1,
				}),
			ok: true,
			status: 200,
		});
	}),
}));

const mockedFetch = fetch as any;

describe('ItemSelector component', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	afterAll(() => {
		jest.restoreAllMocks();
		mockedFetch.mockReset();
		window.ResizeObserver = ResizeObserverOriginal;
	});

	it('renders an item selector', async () => {
		const {findByRole, queryByRole} = render(
			<ItemSelector<TestItem>
				apiURL={`${location.origin}/o/headless-delivery/v1.0/test-api-url`}
			>
				{(item) => (
					<ItemSelector.Item key={item.id} textValue={item.name}>
						{item.name}
					</ItemSelector.Item>
				)}
			</ItemSelector>
		);

		expect(mockedFetch).toHaveBeenCalledTimes(1);

		const input = await findByRole('combobox');

		await userEvent.click(input);

		const menu = await findByRole('listbox');

		expect(menu).toBeVisible();

		const listItem = await findByRole('option', {
			name: mockFirstItemName,
		});

		expect(listItem).toBeTruthy();

		await userEvent.click(listItem);

		await waitFor(() => {
			expect(queryByRole('listbox')).not.toBeInTheDocument();
		});
	});

	it('renders a controlled item selector', async () => {
		const mockSetItem = jest.fn();

		const {findByRole} = render(
			<ItemSelector<TestItem>
				apiURL={`${location.origin}/o/headless-delivery/v1.0/test-api-url`}
				items={[]}
				onItemsChange={(items: Array<TestItem>) => {
					if (items.length) {
						mockSetItem(items[0]);
					}
					else {
						mockSetItem(undefined);
					}
				}}
			>
				{(item) => (
					<ItemSelector.Item key={item.id} textValue={item.name}>
						{item.name}
					</ItemSelector.Item>
				)}
			</ItemSelector>
		);

		const input = await findByRole('combobox');

		await userEvent.click(input);

		const menu = await findByRole('listbox');

		expect(menu).toBeVisible();

		const listItem = await findByRole('option', {
			name: mockSecondItemName,
		});

		expect(listItem).toBeTruthy();

		await userEvent.click(listItem);

		expect(mockSetItem).toHaveBeenCalledTimes(1);

		expect(mockSetItem).toHaveBeenCalledWith({
			_key: '2',
			id: 2,
			name: mockSecondItemName,
		});
	});
});
