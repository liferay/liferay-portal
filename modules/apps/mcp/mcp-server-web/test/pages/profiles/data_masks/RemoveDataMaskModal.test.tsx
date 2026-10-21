/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import RemoveDataMaskModal from '../../../../src/main/resources/META-INF/resources/js/pages/profiles/data_masks/RemoveDataMaskModal';

import type {ProfileDataMaskRow} from '../../../../src/main/resources/META-INF/resources/js/types/ProfileDataMask';

const row: ProfileDataMaskRow = {
	dataMaskExternalReferenceCode: 'SYSTEM_EMAIL',
	description: 'Masks email addresses',
	executionOrder: 0,
	externalReferenceCode: 'PROFILE_DATA_MASK_ERC',
	id: 42,
	name: 'Email Address',
	type: 'System',
};

function renderModal({onClose = jest.fn(), onRemoved = jest.fn()} = {}) {
	const {container} = render(
		<RemoveDataMaskModal
			onClose={onClose}
			onRemoved={onRemoved}
			row={row}
		/>
	);

	return {container, onClose, onRemoved};
}

function removeButton() {
	return screen.findByRole('button', {name: 'remove'});
}

async function typeReason(reason: string) {
	await userEvent.type(
		await screen.findByRole('textbox', {name: /delete-reason/}),
		reason
	);
}

function requests() {
	return fetch.mock.calls.map(([url, request]) => ({
		body: request?.body ? JSON.parse(String(request.body)) : undefined,
		method: request?.method,
		url: String(url),
	}));
}

describe('RemoveDataMaskModal', () => {
	it('keeps remove disabled until a reason is typed', async () => {
		renderModal();

		expect(await removeButton()).toBeDisabled();

		await typeReason('   ');

		expect(await removeButton()).toBeDisabled();

		await typeReason('No longer needed');

		expect(await removeButton()).toBeEnabled();
	});

	it('closes without a request when cancelled', async () => {
		const {onClose, onRemoved} = renderModal();

		await userEvent.click(
			await screen.findByRole('button', {name: 'cancel'})
		);

		expect(onClose).toHaveBeenCalledTimes(1);
		expect(onRemoved).not.toHaveBeenCalled();
		expect(fetch).not.toHaveBeenCalled();
	});

	it('records the reason, deletes the association and closes', async () => {
		fetch.mockResponse(JSON.stringify({}));

		const {onClose, onRemoved} = renderModal();

		await typeReason('No longer needed');

		await userEvent.click(await removeButton());

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));

		expect(onRemoved).toHaveBeenCalledTimes(1);
		expect(requests()).toEqual([
			{
				body: {deleteReason: 'No longer needed'},
				method: 'PATCH',
				url: '/o/mcp/server-profile-data-masks/42',
			},
			{
				body: undefined,
				method: 'DELETE',
				url: '/o/mcp/server-profile-data-masks/42',
			},
		]);
		expect(
			await screen.findByText('Email Address-was-removed-successfully')
		).toBeVisible();
	});

	it('stays open with an error toast when the reason cannot be saved', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({title: 'Unable to update the association'}),
			{status: 400}
		);

		const {onClose, onRemoved} = renderModal();

		await typeReason('No longer needed');

		await userEvent.click(await removeButton());

		expect(
			await screen.findByText('Unable to update the association')
		).toBeVisible();
		expect(requests().map((request) => request.method)).toEqual(['PATCH']);
		expect(onRemoved).not.toHaveBeenCalled();
		expect(onClose).not.toHaveBeenCalled();
		expect(await removeButton()).toBeEnabled();
	});

	it('stays open with an error toast when the association cannot be deleted', async () => {
		fetch.mockResponseOnce(JSON.stringify({}));
		fetch.mockResponseOnce(
			JSON.stringify({title: 'Unable to delete the association'}),
			{status: 400}
		);

		const {onClose, onRemoved} = renderModal();

		await typeReason('No longer needed');

		await userEvent.click(await removeButton());

		expect(
			await screen.findByText('Unable to delete the association')
		).toBeVisible();
		expect(requests().map((request) => request.method)).toEqual([
			'PATCH',
			'DELETE',
		]);
		expect(onRemoved).not.toHaveBeenCalled();
		expect(onClose).not.toHaveBeenCalled();
		expect(await removeButton()).toBeEnabled();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderModal();

		await checkAccessibility({bestPractices: true, context: container});
	});
});
