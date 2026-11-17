/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import RenameLayoutPageTemplateEntryDesignLibraryModalContent from '../../src/main/resources/META-INF/resources/js/RenameLayoutPageTemplateEntryDesignLibraryModalContent';

const mockFetch = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (url: string, options: any) => mockFetch(url, options),
}));

const UPDATE_ACTION = {href: 'http://localhost/rename', method: 'PATCH'};

const renderComponent = () => {
	const closeModal = jest.fn();
	const loadData = jest.fn();

	const {container} = render(
		<RenameLayoutPageTemplateEntryDesignLibraryModalContent
			closeModal={closeModal}
			itemData={{
				actions: {update: UPDATE_ACTION},
				embedded: {name: 'Current name'},
			}}
			loadData={loadData}
			title="rename-master-page"
		/>
	);

	return {closeModal, container, loadData};
};

describe('RenameLayoutPageTemplateEntryDesignLibraryModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('renames the entry with the trimmed new name', async () => {
		mockFetch.mockImplementation(
			() =>
				new Promise((resolve) =>
					setTimeout(
						() => resolve({json: async () => ({}), ok: true}),
						50
					)
				)
		);

		const {closeModal, container, loadData} = renderComponent();

		expect(screen.getByText('rename-master-page')).toBeInTheDocument();
		expect(screen.getByRole('textbox', {name: /name/})).toHaveValue(
			'Current name'
		);

		await checkAccessibility({
			bestPractices: true,
			context: container,
		});

		await userEvent.clear(screen.getByRole('textbox', {name: /name/}));

		expect(screen.getByRole('button', {name: 'save'})).toBeDisabled();

		await userEvent.type(
			screen.getByRole('textbox', {name: /name/}),
			'{enter}'
		);

		expect(mockFetch).not.toHaveBeenCalled();

		await userEvent.type(
			screen.getByRole('textbox', {name: /name/}),
			'  New name  {enter}{enter}'
		);

		await waitFor(() => expect(closeModal).toHaveBeenCalledTimes(1));

		expect(mockFetch).toHaveBeenCalledTimes(1);

		const [url, options] = mockFetch.mock.calls[0];

		expect(url).toBe(UPDATE_ACTION.href);
		expect(options.method).toBe(UPDATE_ACTION.method);
		expect(JSON.parse(options.body)).toEqual({name: 'New name'});

		expect(loadData).toHaveBeenCalledTimes(1);
	});

	it('shows the errors and renames the entry after a retry', async () => {
		mockFetch
			.mockRejectedValueOnce(new TypeError('Failed to fetch'))
			.mockResolvedValueOnce({
				json: async () => {
					throw new SyntaxError('Unexpected token');
				},
				ok: false,
			})
			.mockResolvedValueOnce({
				json: async () => ({title: 'Duplicate name'}),
				ok: false,
			})
			.mockResolvedValueOnce({json: async () => ({}), ok: true});

		const {closeModal, loadData} = renderComponent();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(
			await screen.findByText('an-unexpected-error-occurred')
		).toBeInTheDocument();
		expect(screen.queryByText('Failed to fetch')).not.toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(
			screen.getByText('an-unexpected-error-occurred')
		).toBeInTheDocument();
		expect(screen.queryByText('Unexpected token')).not.toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(await screen.findByText('Duplicate name')).toBeInTheDocument();

		expect(closeModal).not.toHaveBeenCalled();
		expect(loadData).not.toHaveBeenCalled();

		await userEvent.type(screen.getByRole('textbox', {name: /name/}), ' 2');

		expect(screen.queryByText('Duplicate name')).not.toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => expect(closeModal).toHaveBeenCalledTimes(1));

		expect(mockFetch).toHaveBeenCalledTimes(4);
		expect(JSON.parse(mockFetch.mock.calls[3][1].body)).toEqual({
			name: 'Current name 2',
		});

		expect(loadData).toHaveBeenCalledTimes(1);
	});
});
