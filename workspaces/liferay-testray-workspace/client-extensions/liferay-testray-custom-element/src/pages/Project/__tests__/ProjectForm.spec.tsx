/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import {Mock, MockInstance, vi} from 'vitest';

import PageWrapper from '../../../test/PageWrapper';
import ProjectForm from '../ProjectForm';

// const fetch = window.fetch as FetchMock;

describe('ProjectForm', () => {
	let baseFetch: Mock;
	let fetchMock: MockInstance;

	beforeAll(() => {
		vi.resetAllMocks();
		vi.useFakeTimers();
		fetchMock = vi.spyOn(window, 'fetch');
	});

	beforeEach(() => {
		baseFetch = vi.fn().mockImplementationOnce(() => ({
			items: [
				{description: 'DXP Version', id: 1, name: 'Liferay Portal 7.4'},
			],
			lastPage: true,
			pageIndex: 1,
			pageSize: 20,
			totalCount: 100,
		}));

		vi.useFakeTimers();
	});

	afterEach(() => {
		cleanup();
		vi.clearAllTimers();
		vi.restoreAllMocks();
	});

	afterAll(() => {
		vi.useRealTimers();
	});

	it('render ProjectForm with success', async () => {
		const {asFragment} = render(<ProjectForm />, {
			wrapper: ({children}) => (
				<PageWrapper fetcher={baseFetch}>{children}</PageWrapper>
			),
		});

		await act(async () => {
			await vi.runAllTimers();
		});

		expect(asFragment()).toMatchSnapshot();
	});

	it('Fill ProjectForm inputs and hit actions', async () => {
		fetchMock.mockResolvedValueOnce({
			json: async () => ({success: true}),
			ok: true,
		});

		const {container, queryByText} = render(<ProjectForm />, {
			wrapper: ({children}) => (
				<PageWrapper fetcher={baseFetch}>{children}</PageWrapper>
			),
		});

		const descriptionInput = container.querySelector('#description');
		const nameInput = container.querySelector('#name');
		const saveButton = queryByText('Save');

		expect(descriptionInput).toBeEmptyDOMElement();
		expect(nameInput).toBeEmptyDOMElement();

		await act(async () => {
			await fireEvent.click(saveButton as HTMLButtonElement);
		});

		expect(queryByText('name is a required field')).toBeTruthy();

		fireEvent.input(nameInput as HTMLInputElement, {
			target: {
				value: 'Liferay 7.4',
			},
		});

		fireEvent.input(descriptionInput as HTMLInputElement, {
			target: {
				value: 'Liferay 7.4 Description',
			},
		});

		await act(async () => {
			await fireEvent.click(saveButton as HTMLButtonElement);
		});

		expect(queryByText('name is a required field')).toBeFalsy();
		expect(fetchMock.mock.calls).toHaveLength(1);
		expect(fetchMock.mock.calls[0][1]).toMatchObject({
			body: JSON.stringify({
				description: 'Liferay 7.4 Description',
				name: 'Liferay 7.4',
			}),
			method: 'POST',
		});
	});
});
