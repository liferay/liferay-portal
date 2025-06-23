/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import {
	SelectOptions,
	SpaceMembersInputWithSelect,
} from '../../../../src/main/resources/META-INF/resources/js/main/spaces/SpaceMembersInputWithSelect';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	navigate: jest.fn(),
}));

describe('SpaceMembersInputWithSelect', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	let fetchSpier: jest.SpyInstance;

	beforeEach(() => {
		fetchSpier = jest.spyOn(window, 'fetch');
	});

	afterEach(() => {
		jest.clearAllMocks();
		fetchSpier.mockClear();
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
		jest.restoreAllMocks();
	});

	it('accepts a custom className', () => {
		const customClass = 'custom-class';

		const {container} = render(
			<SpaceMembersInputWithSelect className={customClass} />
		);

		expect(container.getElementsByClassName(customClass)).toHaveLength(1);
	});

	it('renders with initial value for select', () => {
		const selectValue = SelectOptions.USERS;

		render(<SpaceMembersInputWithSelect selectValue={selectValue} />);

		const typeSelect = screen.getByRole('combobox', {
			name: 'add-people-to-collaborate',
		});
		expect(typeSelect).toBeInTheDocument();
		expect(typeSelect).toHaveValue(selectValue);
	});

	it('calls "onSelectChange" callback when changing value for input', async () => {
		const onSelectChange = jest.fn();

		render(<SpaceMembersInputWithSelect onSelectChange={onSelectChange} />);

		expect(onSelectChange).not.toHaveBeenCalled();

		await userEvent.selectOptions(
			screen.getByRole('combobox', {name: 'add-people-to-collaborate'}),
			SelectOptions.GROUPS
		);

		expect(onSelectChange).toHaveBeenCalledTimes(1);
		expect(onSelectChange).toHaveBeenCalledWith(SelectOptions.GROUPS);
	});

	it('displays a list of users when the select value is "users"', async () => {
		fetchSpier.mockResolvedValue({
			json: async () => ({
				items: [
					{
						emailAddress: 'john.doe@example.com',
						id: '1',
						image: '/image/user_portrait',
						name: 'John Doe',
					},
					{
						emailAddress: 'jane.smith@example.com',
						id: '2',
						image: '/image/user_portrait',
						name: 'Jane Smith',
					},
				],
			}),
		});

		render(
			<SpaceMembersInputWithSelect selectValue={SelectOptions.USERS} />
		);

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			expect(
				screen.getByRole('option', {name: /John Doe \(john.doe\)/})
			).toBeInTheDocument();
			expect(
				screen.getByRole('option', {name: /Jane Smith \(jane.smith\)/})
			).toBeInTheDocument();
		});

		expect(fetchSpier).toHaveBeenCalledWith(
			'http://localhost/o/headless-admin-user/v1.0/user-accounts?search=',
			expect.any(Object)
		);
	});

	it('displays a list of groups when the select value is "groups"', async () => {
		fetchSpier.mockResolvedValue({
			json: async () => ({
				items: [
					{
						id: '1',
						name: 'Group 1',
					},
					{
						id: '2',
						name: 'Group 2',
					},
				],
			}),
		});

		render(
			<SpaceMembersInputWithSelect selectValue={SelectOptions.GROUPS} />
		);

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			expect(
				screen.getByRole('option', {name: /Group 1/})
			).toBeInTheDocument();
			expect(
				screen.getByRole('option', {name: /Group 2/})
			).toBeInTheDocument();
		});

		expect(fetchSpier).toHaveBeenCalledWith(
			'http://localhost/o/headless-admin-user/v1.0/user-groups?search=',
			expect.any(Object)
		);
	});

	it('calls "onAutocompleteItemSelected" callback when an item is selected', async () => {
		fetchSpier.mockResolvedValue({
			json: async () => ({
				items: [
					{
						emailAddress: 'john.doe@example.com',
						id: '1',
						image: '/image/user_portrait',
						name: 'John Doe',
					},
				],
			}),
		});

		const onAutocompleteItemSelected = jest.fn();

		render(
			<SpaceMembersInputWithSelect
				onAutocompleteItemSelected={onAutocompleteItemSelected}
				selectValue={SelectOptions.USERS}
			/>
		);

		await userEvent.click(
			screen.getByPlaceholderText('enter-name-or-email')
		);

		await waitFor(() => {
			expect(
				screen.getByRole('option', {name: /John Doe \(john.doe\)/})
			).toBeInTheDocument();
		});

		await userEvent.click(
			screen.getByRole('option', {name: /John Doe \(john.doe\)/})
		);

		expect(onAutocompleteItemSelected).toHaveBeenCalledTimes(1);
		expect(onAutocompleteItemSelected).toHaveBeenCalledWith({
			_key: '1',
			emailAddress: 'john.doe@example.com',
			id: '1',
			image: '/image/user_portrait',
			name: 'John Doe',
		});
	});
});
