/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {waitFor} from '@testing-library/react';
import {openConfirmModal, openToast} from 'frontend-js-components-web';
import {navigate} from 'frontend-js-web';

import AudiencesFDSPropsTransformer from '../../../src/main/resources/META-INF/resources/js/props_transformer/AudiencesFDSPropsTransformer';

jest.mock('frontend-js-components-web', () => ({
	openConfirmModal: jest.fn(),
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	navigate: jest.fn(),
}));

const DELETE_URL = '/delete';

const fetchMock = fetch as any;

const GET_AUDIENCES_ENTRY_USAGES_URL = '/get-audiences-entry-usages';

function clickDelete() {
	const {onActionDropdownItemClick} = AudiencesFDSPropsTransformer({
		id: 'audiences',
	} as any);

	onActionDropdownItemClick({
		action: {
			data: {
				deleteURL: DELETE_URL,
				getAudiencesEntryUsagesURL: GET_AUDIENCES_ENTRY_USAGES_URL,
				id: 'delete',
			},
		},
	});
}

async function getConfirmModalProps() {
	await waitFor(() => expect(openConfirmModal).toHaveBeenCalled());

	return (openConfirmModal as jest.Mock).mock.calls[0][0];
}

describe('AudiencesFDSPropsTransformer', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('asks for the regular confirmation when no variation uses the audience', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				collectionVariationsCount: 0,
				elementVariationsCount: 0,
			})
		);

		clickDelete();

		const {message} = await getConfirmModalProps();

		expect(fetchMock).toHaveBeenCalledWith(
			GET_AUDIENCES_ENTRY_USAGES_URL,
			expect.anything()
		);
		expect(message).toBe('are-you-sure-you-want-to-delete-this');
	});

	it('warns that deleting the audience removes it from one element variation', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				collectionVariationsCount: 0,
				elementVariationsCount: 1,
			})
		);

		clickDelete();

		const {message, status} = await getConfirmModalProps();

		expect(message).toBe(
			'this-audience-is-used-in-one-element-variation.-deleting-it-will-remove-it-from-that-variation.-are-you-sure-you-want-to-delete-it'
		);
		expect(status).toBe('warning');
	});

	it('warns that deleting the audience removes it from several element variations', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				collectionVariationsCount: 0,
				elementVariationsCount: 3,
			})
		);

		clickDelete();

		const {message, status} = await getConfirmModalProps();

		expect(message).toBe(
			'this-audience-is-used-in-x-element-variations.-deleting-it-will-remove-it-from-those-variations.-are-you-sure-you-want-to-delete-it'
		);
		expect(status).toBe('warning');
	});

	it('warns that the audience is used in both collection and element variations', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				collectionVariationsCount: 1,
				elementVariationsCount: 1,
			})
		);

		clickDelete();

		const {message, status} = await getConfirmModalProps();

		expect(message).toBe(
			'this-audience-is-used-in-element-variations-and-collection-variations.-deleting-it-will-remove-it-from-the-element-variations-and-delete-the-collection-variations.-are-you-sure-you-want-to-delete-it'
		);
		expect(status).toBe('warning');
	});

	it('warns that deleting the audience deletes the collection variations that use it', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				collectionVariationsCount: 2,
				elementVariationsCount: 0,
			})
		);

		clickDelete();

		const {message, status} = await getConfirmModalProps();

		expect(message).toBe(
			'this-audience-is-used-in-one-or-more-collection-variations.-deleting-it-will-delete-those-variations.-are-you-sure-you-want-to-delete-it'
		);
		expect(status).toBe('warning');
	});

	it('deletes the audience only when the deletion is confirmed', async () => {
		fetchMock.mockResponseOnce(
			JSON.stringify({
				collectionVariationsCount: 0,
				elementVariationsCount: 2,
			})
		);

		clickDelete();

		const {onConfirm} = await getConfirmModalProps();

		onConfirm(false);

		expect(navigate).not.toHaveBeenCalled();

		onConfirm(true);

		expect(navigate).toHaveBeenCalledWith(DELETE_URL);
	});

	it('does not delete the audience when its usages cannot be fetched', async () => {
		fetchMock.mockResponseOnce('', {status: 500});

		clickDelete();

		await waitFor(() => expect(openToast).toHaveBeenCalled());

		expect(openConfirmModal).not.toHaveBeenCalled();
		expect(navigate).not.toHaveBeenCalled();
	});
});
