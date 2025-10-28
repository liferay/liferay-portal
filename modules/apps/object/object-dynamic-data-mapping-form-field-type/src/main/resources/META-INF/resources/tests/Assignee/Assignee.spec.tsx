/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useResource} from '@clayui/data-provider';
import {
	fireEvent,
	getByText as getByTextGlobal,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import React from 'react';

import Assignee from '../../js/Assignee/Assignee';

const mockResourceWithImage = {
	items: [
		{
			externalReferenceCode: '456',
			image: '/image.jpg',
			name: 'Test Test',
			type: 'User',
		},
	],
};

const mockResourceWithoutImage = {
	items: [
		{
			externalReferenceCode: '789',
			name: 'Publications Admin',
			type: 'Role',
		},
	],
};

jest.mock('@clayui/data-provider', () => {
	const originalModule = jest.requireActual('@clayui/data-provider');

	return {
		...originalModule,
		useResource: jest.fn(),
	};
});

jest.mock('data-engine-js-components-web', () => {
	const originalModule = jest.requireActual('data-engine-js-components-web');

	return {
		...originalModule,
		useConfig: () => ({
			portletNamespace: 'portletNamespace_',
		}),
	};
});

beforeAll(() => {
	delete (window as any).ResizeObserver;

	window.ResizeObserver = jest.fn().mockImplementation(() => ({
		disconnect: jest.fn(),
		observe: jest.fn(),
		unobserve: jest.fn(),
	}));
});

afterEach(() => {
	window.ResizeObserver = ResizeObserver;
	jest.restoreAllMocks();
});

describe('Assignee object field', () => {
	it('applies the initial value', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});
		const {container} = render(
			<Assignee
				label="Assignee"
				name="assignee"
				onChange={() => {}}
				searchURL=""
				value={{
					externalReferenceCode: '123',
					name: 'Test Test',
					type: 'User',
				}}
			/>
		);

		expect(
			container.querySelector(`input[name="assignee"]`)
		).toHaveAttribute(
			'value',
			'{"externalReferenceCode":"123","name":"Test Test","type":"User"}'
		);
	});

	it('calls onChange with null value when input field is empty', () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});

		const onChange = jest.fn();

		const {getByRole} = render(
			<Assignee
				label="Assignee"
				name="assignee"
				onChange={onChange}
				searchURL=""
				value={{
					externalReferenceCode: '123',
					name: 'Test Test',
					type: 'User',
				}}
			/>
		);

		const input = getByRole('combobox');

		fireEvent.change(input, {target: {value: ''}});

		expect(onChange).toHaveBeenLastCalledWith({target: {value: null}});
	});

	it('calls onChange with the correct value when an item is selected', async () => {
		(useResource as jest.Mock).mockReturnValue({
			resource: mockResourceWithImage,
		});

		const onChange = jest.fn();

		const {getByRole} = render(
			<Assignee label="" name="" onChange={onChange} searchURL="" />
		);

		const input = getByRole('combobox');

		fireEvent.change(input, {target: {value: 'Test'}});

		const autocompleteItem = await waitFor(() =>
			getByTextGlobal(document.body, 'Test Test')
		);

		fireEvent.click(autocompleteItem);

		expect(onChange).toHaveBeenCalledWith({
			target: {
				value: {
					externalReferenceCode: '456',
					name: 'Test Test',
					type: 'User',
				},
			},
		});
	});

	it('calls the API with the correct search term', async () => {
		(useResource as jest.Mock).mockReturnValue({resource: null});

		const {getByRole} = render(
			<Assignee label="" name="" onChange={() => {}} searchURL="" />
		);

		const input = getByRole('combobox');

		fireEvent.change(input, {target: {value: 'Test'}});

		await waitFor(() => {
			expect(useResource).toHaveBeenCalledWith(
				expect.objectContaining({
					variables: {['portletNamespace_search']: 'Test'},
				})
			);
		});
	});

	it('renders the item with an image', async () => {
		(useResource as jest.Mock).mockReturnValue({
			resource: mockResourceWithImage,
		});

		const {getByRole} = render(
			<Assignee label="" name="" onChange={() => {}} searchURL="" />
		);

		const input = getByRole('combobox');

		fireEvent.change(input, {target: {value: 'Test'}});

		await waitFor(() => getByTextGlobal(document.body, 'Test Test'));

		expect(screen.queryByAltText('user-profile-image')).toHaveAttribute(
			'src',
			'/image.jpg'
		);
	});

	it(`renders the item with the initial letters of the option name`, async () => {
		(useResource as jest.Mock).mockReturnValue({
			resource: mockResourceWithoutImage,
		});

		const {getByRole, getByText} = render(
			<Assignee label="" name="" onChange={() => {}} searchURL="" />
		);

		const input = getByRole('combobox');

		fireEvent.change(input, {target: {value: 'Publications'}});

		await waitFor(() =>
			getByTextGlobal(document.body, 'Publications Admin')
		);

		expect(getByText('PA')).toBeInTheDocument();
	});
});
