/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import confirmAndDeleteEntriesAction from '../../../../src/main/resources/META-INF/resources/js/props_transformer/actions/confirmAndDeleteEntriesAction';

const mockOpenModal = jest.fn();
const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openModal: (...args: any[]) => mockOpenModal(...args),
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

const CONFIRMATION_MESSAGE = {
	bodyHTML: '<p>body</p>',
	partialSuccessMessage: '{0} of {1} entries were deleted.',
	successMessage: 'Every entry was deleted.',
	title: 'Delete entries?',
};

function buildItems(count: number) {
	return Array.from({length: count}, (_, index) => ({
		actions: {delete: {href: `/entries/${index}`, method: 'DELETE'}},
	}));
}

async function confirmDeletion(items: ReturnType<typeof buildItems>) {
	const loadData = jest.fn();

	confirmAndDeleteEntriesAction({
		confirmationMessage: CONFIRMATION_MESSAGE,
		items,
		loadData,
	});

	const [{buttons}] = mockOpenModal.mock.calls[0];

	const [, deleteButton] = buttons;

	await deleteButton.onClick({processClose: jest.fn()});

	return loadData;
}

describe('confirmAndDeleteEntriesAction', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('opens a confirmation modal before deleting anything', () => {
		confirmAndDeleteEntriesAction({
			confirmationMessage: CONFIRMATION_MESSAGE,
			items: buildItems(3),
		});

		const [{bodyHTML, title}] = mockOpenModal.mock.calls[0];

		expect(title).toBe('Delete entries?');
		expect(bodyHTML).toBe('<p>body</p>');
		expect(fetch).not.toHaveBeenCalled();
	});

	it('deletes every selected entry', async () => {
		fetch.mockResponse('{}');

		await confirmDeletion(buildItems(2));

		expect(fetch).toHaveBeenCalledTimes(2);
		expect(fetch).toHaveBeenCalledWith(
			'/entries/0',
			expect.objectContaining({method: 'DELETE'})
		);
		expect(fetch).toHaveBeenCalledWith(
			'/entries/1',
			expect.objectContaining({method: 'DELETE'})
		);
	});

	it('shows a success message when every deletion succeeds', async () => {
		fetch.mockResponse('{}');

		await confirmDeletion(buildItems(2));

		expect(mockOpenToast).toHaveBeenCalledWith({
			message: 'Every entry was deleted.',
			type: 'success',
		});
	});

	it('shows a warning message when only some deletions succeed', async () => {
		fetch.mockResponseOnce('{}', {status: 403});
		fetch.mockResponseOnce('{}');

		await confirmDeletion(buildItems(2));

		expect(mockOpenToast).toHaveBeenCalledWith({
			message: '1 of 2 entries were deleted.',
			type: 'warning',
		});
	});

	it('shows an error message when every deletion fails', async () => {
		fetch.mockResponse('{}', {status: 403});

		await confirmDeletion(buildItems(2));

		expect(mockOpenToast).toHaveBeenCalledWith({
			message: 'an-unexpected-error-occurred',
			type: 'danger',
		});
	});

	it('refreshes the data set even when some deletions fail', async () => {
		fetch.mockResponseOnce('{}', {status: 403});
		fetch.mockResponseOnce('{}');

		const loadData = await confirmDeletion(buildItems(2));

		expect(loadData).toHaveBeenCalled();
	});

	it('does not refresh the data set when every deletion fails', async () => {
		fetch.mockResponse('{}', {status: 403});

		const loadData = await confirmDeletion(buildItems(2));

		expect(loadData).not.toHaveBeenCalled();
	});
});
