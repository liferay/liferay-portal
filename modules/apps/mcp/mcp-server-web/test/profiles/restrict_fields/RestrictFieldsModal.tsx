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

import RestrictFieldsModal from '../../../src/main/resources/META-INF/resources/js/profiles/restrict_fields/RestrictFieldsModal';
import {mockPageTool} from '../../mocks/mockPageTool';
import {mockTool} from '../../mocks/mockTool';

const profileTool = {
	externalReferenceCode: 'PROFILE_TOOL_ERC',
	toolName: 'getMCPServerPrompt',
	toolSetName: 'mcp-server-prompts',
};

function renderModal({
	onClose = jest.fn(),
	onSaved = jest.fn(),
	restrictFields,
}: {
	onClose?: jest.Mock;
	onSaved?: jest.Mock;
	restrictFields?: string;
} = {}) {
	const {container} = render(
		<RestrictFieldsModal
			onClose={onClose}
			onSaved={onSaved}
			profileTool={{...profileTool, restrictFields}}
		/>
	);

	return {container, onClose, onSaved};
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
	beforeAll(() => {
		Liferay.Util.escapeHTML = jest.fn((value: string) => value);
	});

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

	it('counts every checked field, descendants included', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		expect(screen.getByRole('status')).toHaveTextContent(
			'nothing-selected'
		);

		await userEvent.click(checkbox('description'));

		expect(screen.getByRole('status')).toHaveTextContent('1-item-selected');

		await userEvent.click(checkbox('modifiedBy'));

		expect(screen.getByRole('status')).toHaveTextContent(
			'7-items-selected'
		);
	});

	it('clears the selection with the deselect all action', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		await userEvent.click(
			screen.getByRole('button', {name: 'deselect-all'})
		);

		expect(checkbox('description')).not.toBeChecked();
		expect(screen.getByRole('status')).toHaveTextContent(
			'nothing-selected'
		);
		expect(screen.queryByRole('button', {name: 'deselect-all'})).toBeNull();
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

	it('closes with an error toast when the tool cannot be loaded', async () => {
		fetch.mockResponseOnce(JSON.stringify({title: 'Tool not found'}), {
			status: 404,
		});

		const {onClose} = renderModal();

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));

		expect(await screen.findByText('Tool not found')).toBeVisible();
	});

	it('stays open with an error toast when saving is rejected', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		const {onClose, onSaved} = renderModal();

		await userEvent.click(await findCheckbox('description'));

		fetch.mockResponseOnce(
			JSON.stringify({title: 'Unable to restrict field "description"'}),
			{status: 400}
		);

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		expect(
			await screen.findByText('Unable to restrict field "description"')
		).toBeVisible();
		expect(onSaved).not.toHaveBeenCalled();
		expect(onClose).not.toHaveBeenCalled();
		expect(screen.getByRole('button', {name: 'save'})).toBeEnabled();
	});

	it('has no accessibility violations', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		const {container} = renderModal({
			restrictFields: 'modifiedBy.userGroupBriefs',
		});

		await findCheckbox('modifiedBy');

		await checkAccessibility({
			bestPractices: true,
			context: container,
		});
	});

	it('closes when cancel is clicked', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		const {onClose} = renderModal();

		await findCheckbox('modifiedBy');

		await userEvent.click(screen.getByRole('button', {name: 'cancel'}));

		expect(onClose).toHaveBeenCalledTimes(1);
	});

	it('preselects the restricted fields of the profile tool with their descendants', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal({restrictFields: 'description,modifiedBy.userGroupBriefs'});

		expect(await findCheckbox('description')).toBeChecked();
		expect(checkbox('modifiedBy')).toBePartiallyChecked();
		expect(screen.getByRole('status')).toHaveTextContent(
			'4-items-selected'
		);
		expect(checkbox('userGroupBriefs')).toBeChecked();
		expect(
			screen.getAllByRole('checkbox', {name: 'id'})[0]
		).not.toBeChecked();
	});

	it('saves the top-most checked fields on the profile tool', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		const {onClose, onSaved} = renderModal();

		await userEvent.click(await findCheckbox('modifiedBy'));
		await userEvent.click(checkbox('description'));

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => expect(onClose).toHaveBeenCalledTimes(1));

		expect(fetch).toHaveBeenLastCalledWith(
			'/o/mcp/server-profile-tools/by-external-reference-code/PROFILE_TOOL_ERC',
			expect.objectContaining({
				body: JSON.stringify({
					restrictFields: 'description,modifiedBy',
				}),
				method: 'PATCH',
			})
		);
		expect(onSaved).toHaveBeenCalledTimes(1);
	});

	it('saves the item field names of a page tool', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockPageTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		fetch.mockResponseOnce(JSON.stringify({}));

		await userEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() =>
			expect(fetch).toHaveBeenLastCalledWith(
				'/o/mcp/server-profile-tools/by-external-reference-code/PROFILE_TOOL_ERC',
				expect.objectContaining({
					body: JSON.stringify({restrictFields: 'description'}),
					method: 'PATCH',
				})
			)
		);
	});
});
