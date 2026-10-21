/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import RestrictFieldsModal from '../../../../src/main/resources/META-INF/resources/js/pages/profiles/tools/RestrictFieldsModal';
import {mockPageTool} from '../../../mocks/mockPageTool';
import {mockTool} from '../../../mocks/mockTool';

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

function row(name: string) {
	return screen.getByRole('treeitem', {name});
}

function search(value: string) {
	return userEvent.type(searchBox(), value);
}

function searchBox() {
	return screen.getByRole('searchbox', {name: 'search-fields'});
}

describe('RestrictFieldsModal', () => {
	beforeAll(() => {
		const style = document.createElement('style');

		style.textContent = '.d-none { display: none !important; }';

		document.head.appendChild(style);
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

		expect(screen.getByText('nothing-selected')).toBeInTheDocument();

		await userEvent.click(checkbox('description'));

		expect(screen.getByText('1-item-selected')).toBeInTheDocument();

		await userEvent.click(checkbox('modifiedBy'));

		expect(screen.getByText('7-items-selected')).toBeInTheDocument();
	});

	it('shows next to each group how many fields selecting it adds', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		expect(within(row('modifiedBy')).getByText('6')).toBeVisible();
		expect(within(row('description')).queryByText('1')).toBeNull();

		await expand('auditEvents');

		expect(within(row('creator')).getByText('2')).toBeVisible();

		await userEvent.click(checkbox('modifiedBy'));

		expect(screen.getByText('6-items-selected')).toBeInTheDocument();
	});

	it('describes each group to assistive technology with its field count', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		expect(checkbox('modifiedBy')).toHaveAccessibleDescription('6-items');
		expect(row('modifiedBy')).toHaveAccessibleDescription('6-items');
		expect(checkbox('description')).not.toHaveAccessibleDescription();
	});

	it('highlights the count of a group once every field in it is selected', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		const badge = () =>
			within(row('modifiedBy')).getByText('6').closest('.badge');

		expect(badge()).toHaveClass('badge-secondary');

		await userEvent.click(checkbox('modifiedBy'));

		expect(badge()).toHaveClass('badge-primary');

		await expand('modifiedBy');

		expect(
			within(row('userGroupBriefs')).getByText('3').closest('.badge')
		).toHaveClass('badge-primary');

		await userEvent.click(checkbox('userGroupBriefs'));

		expect(badge()).toHaveClass('badge-secondary');
	});

	it('clears the selection with the clear action', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		await userEvent.click(screen.getByRole('button', {name: 'clear'}));

		expect(checkbox('description')).not.toBeChecked();
		expect(screen.getByText('nothing-selected')).toBeInTheDocument();
		expect(screen.queryByRole('button', {name: 'clear'})).toBeNull();
	});

	it('clears nested indeterminate parents with the clear action', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('modifiedBy'));

		await expand('modifiedBy');
		await expand('userGroupBriefs');

		await userEvent.click(screen.getAllByRole('checkbox', {name: 'id'})[1]);

		expect(checkbox('modifiedBy')).toBePartiallyChecked();
		expect(checkbox('userGroupBriefs')).toBePartiallyChecked();

		await userEvent.click(screen.getByRole('button', {name: 'clear'}));

		expect(checkbox('modifiedBy')).not.toBePartiallyChecked();
		expect(checkbox('modifiedBy')).not.toBeChecked();
		expect(checkbox('userGroupBriefs')).not.toBePartiallyChecked();
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

		expect(await screen.findByText('no-fields-were-found')).toBeVisible();
		expect(screen.getByRole('button', {name: 'save'})).toBeDisabled();
		expect(
			screen.queryByRole('searchbox', {name: 'search-fields'})
		).toBeNull();
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

		expect(checkbox('userGroupBriefs')).toBeVisible();

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

	it('moves the focus to the first field when clear removes its button', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		await userEvent.click(screen.getByRole('button', {name: 'clear'}));

		expect(screen.getAllByRole('treeitem')[0]).toHaveFocus();
	});

	it('preselects the restricted fields of the profile tool with their descendants', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal({restrictFields: 'description,modifiedBy.userGroupBriefs'});

		expect(await findCheckbox('description')).toBeChecked();
		expect(checkbox('modifiedBy')).toBePartiallyChecked();
		expect(screen.getByText('4-items-selected')).toBeInTheDocument();
		expect(checkbox('userGroupBriefs')).toBeChecked();
		expect(checkbox('id')).not.toBeChecked();
		expect(screen.queryByRole('checkbox', {name: 'key'})).toBeNull();
	});

	it('keeps the preselected parents indeterminate after the first click', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal({restrictFields: 'modifiedBy.userGroupBriefs'});

		await userEvent.click(await findCheckbox('description'));

		expect(checkbox('modifiedBy')).toBePartiallyChecked();
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

	it('filters the fields by name across every level and expands the path to each match', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('description');

		await search('label');

		expect(checkbox('label')).toBeVisible();
		expect(checkbox('scope')).toBeVisible();
		expect(checkbox('taxonomyCategoryBriefs')).toBeVisible();
		expect(
			screen.queryByRole('checkbox', {name: 'description'})
		).toBeNull();
		expect(screen.queryByRole('checkbox', {name: 'modifiedBy'})).toBeNull();
		expect(screen.getAllByText('label', {selector: 'mark'})).toHaveLength(
			1
		);
	});

	it('hides the parents that neither match nor lead to a match', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('auditEvents');

		await search('eventType');

		expect(checkbox('auditEvents')).toBeVisible();
		expect(checkbox('eventType')).toBeVisible();
		expect(screen.queryByRole('checkbox', {name: 'creator'})).toBeNull();
		expect(
			screen.getAllByText('eventType', {selector: 'mark'})
		).toHaveLength(1);
	});

	it('keeps the selected count while the query changes', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		expect(screen.getByText('1-item-selected')).toBeInTheDocument();

		await search('label');

		expect(
			screen.queryByRole('checkbox', {name: 'description'})
		).toBeNull();
		expect(screen.getByText('1-item-selected')).toBeInTheDocument();
	});

	it('covers the hidden descendants when a parent is checked under a filter', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		await search('userGroupBriefs');

		expect(checkbox('modifiedBy')).toBeVisible();
		expect(checkbox('userGroupBriefs')).toBeVisible();
		expect(screen.queryByRole('checkbox', {name: 'id'})).toBeNull();

		await userEvent.click(checkbox('modifiedBy'));

		expect(screen.getByText('6-items-selected')).toBeInTheDocument();

		await userEvent.clear(searchBox());
		await expand('modifiedBy');

		expect(checkbox('id')).toBeChecked();
		expect(checkbox('id')).toBeVisible();
		expect(screen.getByText('6-items-selected')).toBeInTheDocument();
	});

	it('shows the empty message when no field matches the query', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('description');

		await search('nothing-like-this');

		expect(screen.getByText('no-results-found')).toBeInTheDocument();
		expect(
			screen.queryByRole('checkbox', {name: 'description'})
		).toBeNull();
		expect(screen.queryByText('no-fields-were-found')).toBeNull();
	});

	it('restores the expansion from before the filter when the query is cleared', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		await expand('modifiedBy');

		await search('label');

		expect(checkbox('label')).toBeInTheDocument();
		expect(
			screen.queryByRole('checkbox', {
				hidden: true,
				name: 'userGroupBriefs',
			})
		).toBeNull();

		await userEvent.clear(searchBox());

		await waitFor(() =>
			expect(screen.queryByRole('checkbox', {name: 'label'})).toBeNull()
		);
		expect(checkbox('userGroupBriefs')).toBeInTheDocument();
		expect(checkbox('description')).toBeVisible();
	});

	it('discards the expansions toggled under a filter when the query is cleared', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		await expand('modifiedBy');

		await search('userGroupBriefs');

		await userEvent.click(
			screen.getByRole('button', {expanded: true, name: 'modifiedBy'})
		);

		expect(
			screen.getByRole('button', {expanded: false, name: 'modifiedBy'})
		).toBeInTheDocument();

		await userEvent.clear(searchBox());

		expect(
			screen.getByRole('button', {expanded: true, name: 'modifiedBy'})
		).toBeInTheDocument();
	});

	it('moves the focus to the first visible field when clear removes its button under a filter', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		await search('label');

		await userEvent.click(screen.getByRole('button', {name: 'clear'}));

		expect(
			checkbox('taxonomyCategoryBriefs').closest('[role="treeitem"]')
		).toHaveFocus();
	});

	it('has no accessibility violations under a filter', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		const {container} = renderModal();

		await findCheckbox('description');

		await search('label');

		await checkAccessibility({
			bestPractices: true,
			context: container,
		});
	});

	it('does not filter with a whitespace only query', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('description');

		await search('   ');

		expect(checkbox('description')).toBeVisible();
		expect(checkbox('modifiedBy')).toBeVisible();
		expect(screen.queryByText('no-results-found')).toBeNull();
	});

	it('disables the expander of a match whose fields are all hidden', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		await search('userGroupBriefs');

		expect(
			screen.getByRole('button', {name: 'userGroupBriefs'})
		).toBeDisabled();
		expect(screen.getByRole('button', {name: 'modifiedBy'})).toBeEnabled();
	});

	it('ignores the right arrow on a match whose fields are all hidden', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('modifiedBy');

		await search('userGroupBriefs');

		row('userGroupBriefs').focus();

		await userEvent.keyboard('{ArrowRight}');

		expect(row('userGroupBriefs')).toHaveAttribute(
			'aria-expanded',
			'false'
		);
	});

	it('announces the number of matches while fields are visible', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('description');

		await search('label');

		const status = screen.getByText('1-result-found');

		expect(status).toHaveAttribute('role', 'status');
		expect(status).toHaveClass('sr-only');
	});

	it('moves the focus to the search box when clear leaves no visible field', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await userEvent.click(await findCheckbox('description'));

		await search('nothing-like-this');

		await userEvent.click(screen.getByRole('button', {name: 'clear'}));

		expect(searchBox()).toHaveFocus();
	});

	it('names the field tree', async () => {
		fetch.mockResponseOnce(JSON.stringify(mockTool));

		renderModal();

		await findCheckbox('description');

		expect(screen.getByRole('tree', {name: 'fields'})).toBeInTheDocument();
	});
});
