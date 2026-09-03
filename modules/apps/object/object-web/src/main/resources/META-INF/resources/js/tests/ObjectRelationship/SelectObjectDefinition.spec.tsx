/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import SelectObjectDefinition from '../../components/ObjectRelationship/SelectObjectDefinition';

const OBJECT_DEFINITION = {
	defaultLanguageId: 'en_US',
	externalReferenceCode: 'ERC_ALPHA',
	id: 1,
	label: {en_US: 'Alpha'},
	modifiable: true,
	name: 'C_Alpha',
	system: false,
};

const SECOND_OBJECT_DEFINITION = {
	defaultLanguageId: 'en_US',
	externalReferenceCode: 'ERC_BETA',
	id: 2,
	label: {en_US: 'Beta'},
	modifiable: true,
	name: 'C_Beta',
	system: false,
};

const FILTERED_OBJECT_DEFINITION = {
	defaultLanguageId: 'en_US',
	externalReferenceCode: 'ERC_GAMMA',
	id: 3,
	label: {en_US: 'Gamma'},
	modifiable: true,
	name: 'C_Gamma',
	parameterRequired: true,
	system: true,
};

const originalLanguageGet = (
	Liferay.Language.get as jest.Mock
).getMockImplementation() as (key: string) => string;

function mockPage({
	items = [OBJECT_DEFINITION],
	lastPage = 1,
	totalCount = 1,
	once = true,
}) {
	const response = {json: async () => ({items, lastPage, totalCount})};

	if (once) {
		(global.fetch as jest.Mock).mockResolvedValueOnce(response);
	}
	else {
		(global.fetch as jest.Mock).mockResolvedValue(response);
	}
}

function getRequestedURL(index: number) {
	return (global.fetch as jest.Mock).mock.calls[index][0];
}

async function openMenu() {
	await userEvent.click(
		screen.getByPlaceholderText('search-for-an-object-definition')
	);
}

function renderSelectObjectDefinition() {
	return render(
		<SelectObjectDefinition reverseOrder={false} setValues={() => {}} />
	);
}

describe('SelectObjectDefinition', () => {
	const {ResizeObserver} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		})) as unknown as typeof window.ResizeObserver;
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserver;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'showing-x-of-x-items'
				? 'Showing {0} of {1} Items'
				: originalLanguageGet(key)
		);
	});

	afterEach(() => {
		(Liferay.Language.get as jest.Mock).mockImplementation(
			originalLanguageGet
		);
	});

	it('appends the next page when the menu asks for more', async () => {
		mockPage({items: [OBJECT_DEFINITION], lastPage: 2, totalCount: 2});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));

		mockPage({
			items: [SECOND_OBJECT_DEFINITION],
			lastPage: 2,
			totalCount: 2,
		});

		await openMenu();

		await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2));

		expect(getRequestedURL(1)).toContain('page=2');

		expect(await screen.findByText('Beta')).toBeInTheDocument();
	});

	it('discounts filtered object definitions from the hint', async () => {
		mockPage({
			items: [OBJECT_DEFINITION, FILTERED_OBJECT_DEFINITION],
			lastPage: 1,
			once: false,
			totalCount: 2,
		});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalled());

		await openMenu();

		expect(
			await screen.findByText('Showing 1 of 1 Items')
		).toBeInTheDocument();

		expect(screen.queryByText('Gamma')).not.toBeInTheDocument();
	});

	it('does not request more pages once the last page is loaded', async () => {
		mockPage({lastPage: 1, totalCount: 1});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));

		await openMenu();

		expect(global.fetch).toHaveBeenCalledTimes(1);
	});

	it('keeps the hint once every object definition is loaded', async () => {
		mockPage({lastPage: 1, once: false, totalCount: 1});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalled());

		await openMenu();

		expect(
			await screen.findByText('Showing 1 of 1 Items')
		).toBeInTheDocument();
	});

	it('renders the hint while more object definitions can be loaded', async () => {
		mockPage({lastPage: 2, once: false, totalCount: 200});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalled());

		await openMenu();

		expect(
			await screen.findByText(/Showing \d+ of 200 Items/)
		).toBeInTheDocument();
	});

	it('requests object definitions with the context path prefixed', async () => {
		(Liferay.ThemeDisplay.getPathContext as jest.Mock).mockReturnValueOnce(
			'/myportal'
		);

		mockPage({});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalled());

		expect(getRequestedURL(0)).toBe(
			'http://localhost:8080/myportal/o/object-admin/v1.0/object-definitions?page=1&search=&sort=label%3Aasc'
		);
	});

	it('requests object definitions without a prefix at the root context', async () => {
		(Liferay.ThemeDisplay.getPathContext as jest.Mock).mockReturnValueOnce(
			''
		);

		mockPage({});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalled());

		expect(getRequestedURL(0)).toBe(
			'http://localhost:8080/o/object-admin/v1.0/object-definitions?page=1&search=&sort=label%3Aasc'
		);
	});

	it('starts from the first page each time it is mounted', async () => {
		mockPage({items: [OBJECT_DEFINITION], lastPage: 2, totalCount: 2});

		const {unmount} = renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(1));

		mockPage({
			items: [SECOND_OBJECT_DEFINITION],
			lastPage: 2,
			totalCount: 2,
		});

		await openMenu();

		await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(2));

		unmount();

		mockPage({items: [OBJECT_DEFINITION], lastPage: 2, totalCount: 2});

		renderSelectObjectDefinition();

		await waitFor(() => expect(global.fetch).toHaveBeenCalledTimes(3));

		expect(getRequestedURL(2)).toContain('page=1');
	});

	it('updates the hint when the search is cleared', async () => {
		mockPage({
			items: [OBJECT_DEFINITION],
			lastPage: 1,
			once: false,
			totalCount: 1,
		});

		renderSelectObjectDefinition();

		fireEvent.change(
			screen.getByPlaceholderText('search-for-an-object-definition'),
			{target: {value: 'Alpha'}}
		);

		expect(
			await screen.findByText('Showing 1 of 1 Items')
		).toBeInTheDocument();

		mockPage({
			items: [OBJECT_DEFINITION, SECOND_OBJECT_DEFINITION],
			lastPage: 1,
			once: false,
			totalCount: 2,
		});

		fireEvent.change(
			screen.getByPlaceholderText('search-for-an-object-definition'),
			{target: {value: ''}}
		);

		expect(
			await screen.findByText('Showing 2 of 2 Items')
		).toBeInTheDocument();
	});
});
