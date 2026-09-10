/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import RestrictFieldsModal from '../../../src/main/resources/META-INF/resources/js/profiles/restrict_fields/RestrictFieldsModal';
import {mockPageTool} from '../../mocks/mockPageTool';
import {mockTool} from '../../mocks/mockTool';

function renderModal(onClose = jest.fn()) {
	render(
		<RestrictFieldsModal
			onClose={onClose}
			toolName="getMCPServerPrompt"
			toolSetName="mcp-server-prompts"
		/>
	);

	return onClose;
}

function checkbox(name: string) {
	return screen.getByRole('checkbox', {name});
}

function findCheckbox(name: string) {
	return screen.findByRole('checkbox', {name});
}

function expand(name: string) {
	return userEvent.click(screen.getByRole('button', {expanded: false, name}));
}

describe('RestrictFieldsModal', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('names the tool in the title', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		expect(await screen.findByText(/getMCPServerPrompt/)).toBeVisible();
	});

	it('requests only the output schema of the tool from its tool set', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		expect(fetch).toHaveBeenCalledWith(
			'/o/mcp-server/v1.0/tool-sets/mcp-server-prompts/tools/getMCPServerPrompt?fields=outputSchema&nestedFields=outputSchema',
			expect.objectContaining({method: 'GET'})
		);
	});

	it('shows the top level output fields as a collapsed checkbox tree', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		expect(await findCheckbox('modifiedBy')).toBeVisible();
		expect(checkbox('description')).toBeVisible();
		expect(
			screen.queryByRole('checkbox', {name: 'userGroupBriefs'})
		).toBeNull();
		expect(
			screen.queryByRole('checkbox', {name: 'taxonomyCategoryIds'})
		).toBeNull();
	});

	it('shows the item fields of a page tool instead of the page itself', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockPageTool));

		renderModal();

		expect(await findCheckbox('modifiedBy')).toBeVisible();
		expect(checkbox('description')).toBeVisible();
		expect(screen.queryByRole('checkbox', {name: 'items'})).toBeNull();
		expect(screen.queryByRole('checkbox', {name: 'totalCount'})).toBeNull();
	});

	it('reveals the nested fields when a parent is expanded', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		await expand('modifiedBy');

		expect(checkbox('userGroupBriefs')).toBeVisible();
		expect(checkbox('id')).toBeVisible();
	});

	it('checks the descendants when a parent is checked', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('modifiedBy'));

		await expand('modifiedBy');

		expect(checkbox('userGroupBriefs')).toBeChecked();
		expect(checkbox('id')).toBeChecked();
	});

	it('disables save while the tool loads', async () => {
		fetch.mockResponseOnce(() => new Promise(() => {}));

		renderModal();

		expect(
			await screen.findByRole('button', {name: 'save'})
		).toBeDisabled();
	});

	it('tells when the tool has no output schema and keeps save disabled', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({...mockTool, outputSchema: undefined})
		);

		renderModal();

		expect(await screen.findByText('no-fields-were-found')).toHaveAttribute(
			'role',
			'status'
		);
		expect(screen.getByRole('button', {name: 'save'})).toBeDisabled();
	});

	it('closes when cancel is clicked', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		const onClose = renderModal();

		await findCheckbox('modifiedBy');

		await userEvent.click(screen.getByRole('button', {name: 'cancel'}));

		expect(onClose).toHaveBeenCalledTimes(1);
	});
});
