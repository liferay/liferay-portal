/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import AddToolsModal from '../../src/main/resources/META-INF/resources/js/profiles/AddToolsModal';

const PROFILE_ERC = 'PROFILE_ERC';

const PROFILE_TOOLS_URL = '/o/mcp/server-profile-tools';

const TOOL_SETS_URL = '/o/mcp-server/v1.0/tool-sets';

type ToolsByToolSetName = Record<string, string[]>;

function page(items: unknown[]) {
	return JSON.stringify({items, lastPage: 1, totalCount: items.length});
}

function toolSetNameOf(url: string) {
	return decodeURIComponent(
		url.split(`${TOOL_SETS_URL}/`)[1].split('/tool-summaries')[0]
	);
}

function mockAPIRoutes({
	failingToolSetNames = [] as string[],
	pendingToolSetNames = [] as string[],
	profileTools = [] as {toolName: string; toolSetName: string}[],
	toolsByToolSetName = {} as ToolsByToolSetName,
}) {
	const resolvers: Record<string, () => void> = {};

	fetch.mockResponse(async (request) => {
		if (request.method === 'POST') {
			return {body: JSON.stringify({})};
		}

		if (request.url.includes('/tool-summaries')) {
			const toolSetName = toolSetNameOf(request.url);

			if (failingToolSetNames.includes(toolSetName)) {
				return {
					body: JSON.stringify({
						title: `Tool set ${toolSetName} was not found`,
					}),
					status: 404,
				};
			}

			const body = page(
				(toolsByToolSetName[toolSetName] ?? []).map((name) => ({name}))
			);

			if (pendingToolSetNames.includes(toolSetName)) {
				return new Promise<{body: string}>((resolve) => {
					resolvers[toolSetName] = () => resolve({body});
				});
			}

			return {body};
		}

		if (request.url.includes(PROFILE_TOOLS_URL)) {
			return {
				body: page(
					profileTools.map((profileTool, index) => ({
						...profileTool,
						externalReferenceCode: `PROFILE_TOOL_${index}`,
					}))
				),
			};
		}

		return {
			body: page(Object.keys(toolsByToolSetName).map((name) => ({name}))),
		};
	});

	return {
		resolve: (toolSetName: string) => resolvers[toolSetName](),
	};
}

function renderModal({onAdded = jest.fn(), onClose = jest.fn()} = {}) {
	render(
		<AddToolsModal
			onAdded={onAdded}
			onClose={onClose}
			profileERC={PROFILE_ERC}
		/>
	);

	return {onAdded, onClose};
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

function status() {
	return screen.getByRole('status');
}

function toolSummaryURLs() {
	return fetch.mock.calls
		.map(([url]) => String(url))
		.filter((url) => url.includes('/tool-summaries'));
}

describe('AddToolsModal', () => {
	beforeAll(() => {
		Liferay.Util.escapeHTML = jest.fn((value: string) => value);
	});

	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('selects every tool of a collapsed tool set by clicking its name', async () => {
		mockAPIRoutes({
			toolsByToolSetName: {
				'mcp-server-v1.0': ['getToolSetsPage', 'postToolInvoke'],
			},
		});

		renderModal();

		await findCheckbox('mcp-server-v1.0');

		await userEvent.click(screen.getByText('mcp-server-v1.0'));

		await waitFor(() =>
			expect(status()).toHaveTextContent('2-items-selected')
		);

		expect(checkbox('mcp-server-v1.0')).toBeChecked();
		expect(
			screen.queryByRole('checkbox', {name: 'getToolSetsPage'})
		).not.toBeInTheDocument();
	});

	it('selects a tool by clicking its name', async () => {
		mockAPIRoutes({
			toolsByToolSetName: {
				'mcp-server-v1.0': ['getToolSetsPage', 'postToolInvoke'],
			},
		});

		renderModal();

		await findCheckbox('mcp-server-v1.0');

		await expand('mcp-server-v1.0');

		await userEvent.click(await screen.findByText('getToolSetsPage'));

		expect(checkbox('getToolSetsPage')).toBeChecked();
		expect(checkbox('postToolInvoke')).not.toBeChecked();
		expect(status()).toHaveTextContent('1-item-selected');
	});

	it('drops the tools of a tool set deselected while they load', async () => {
		const {resolve} = mockAPIRoutes({
			pendingToolSetNames: ['organizations'],
			toolsByToolSetName: {
				'mcp-server-v1.0': ['getToolSetsPage'],
				'organizations': ['getOrganization'],
			},
		});

		renderModal();

		await findCheckbox('mcp-server-v1.0');

		await expand('mcp-server-v1.0');

		await userEvent.click(await findCheckbox('getToolSetsPage'));

		await userEvent.click(checkbox('organizations'));

		await userEvent.click(
			screen.getByRole('button', {name: 'deselect-all'})
		);

		resolve('organizations');

		await waitFor(() =>
			expect(checkbox('organizations')).not.toBeChecked()
		);

		expect(status()).toHaveTextContent('nothing-selected');
		expect(screen.getByRole('button', {name: 'add'})).toBeDisabled();
	});

	it('reports a failing tool set only when the user expands it', async () => {
		mockAPIRoutes({
			failingToolSetNames: ['organizations'],
			profileTools: [
				{toolName: 'getOrganization', toolSetName: 'organizations'},
			],
			toolsByToolSetName: {
				'mcp-server-v1.0': ['getToolSetsPage'],
				'organizations': ['getOrganization'],
			},
		});

		renderModal();

		expect(await findCheckbox('organizations')).toBeVisible();
		expect(
			screen.queryByText('Tool set organizations was not found')
		).not.toBeInTheDocument();

		await expand('organizations');

		expect(
			await screen.findByText('Tool set organizations was not found')
		).toBeVisible();
	});

	it('prefetches the tool sets the profile uses, once each', async () => {
		mockAPIRoutes({
			profileTools: [
				{toolName: 'getOrganization', toolSetName: 'organizations'},
				{toolName: 'postOrganization', toolSetName: 'organizations'},
				{toolName: 'getToolSetsPage', toolSetName: 'mcp-server-v1.0'},
			],
			toolsByToolSetName: {
				'mcp-server-v1.0': ['getToolSetsPage', 'postToolInvoke'],
				'organizations': ['getOrganization', 'postOrganization'],
				'user-management': ['getUserAccount'],
			},
		});

		renderModal();

		await findCheckbox('mcp-server-v1.0');

		const urls = toolSummaryURLs();

		expect(urls).toHaveLength(2);
		expect(
			urls.filter((url) => url.includes('/organizations/'))
		).toHaveLength(1);
		expect(
			urls.filter((url) => url.includes('/mcp-server-v1.0/'))
		).toHaveLength(1);
		expect(
			urls.filter((url) => url.includes('/user-management/'))
		).toHaveLength(0);
	});

	it('shows a loading indicator until the prefetched tool sets are read', async () => {
		const {resolve} = mockAPIRoutes({
			pendingToolSetNames: ['organizations'],
			profileTools: [
				{toolName: 'getOrganization', toolSetName: 'organizations'},
			],
			toolsByToolSetName: {
				'mcp-server-v1.0': ['getToolSetsPage'],
				'organizations': ['getOrganization', 'postOrganization'],
			},
		});

		renderModal();

		await waitFor(() =>
			expect(
				screen.getByRole('button', {name: 'add'})
			).toBeInTheDocument()
		);

		expect(screen.queryByRole('tree')).not.toBeInTheDocument();
		expect(screen.queryByRole('status')).not.toBeInTheDocument();

		resolve('organizations');

		expect(await findCheckbox('mcp-server-v1.0')).toBeVisible();
		expect(status()).toHaveTextContent('nothing-selected');
	});

	it('tells when every tool set is exhausted', async () => {
		mockAPIRoutes({
			profileTools: [
				{toolName: 'getToolSetsPage', toolSetName: 'mcp-server-v1.0'},
			],
			toolsByToolSetName: {'mcp-server-v1.0': ['getToolSetsPage']},
		});

		renderModal();

		expect(await screen.findByText('no-tools-were-found')).toHaveAttribute(
			'role',
			'status'
		);
		expect(screen.getByRole('button', {name: 'add'})).toBeDisabled();
		expect(screen.queryByRole('tree')).not.toBeInTheDocument();
	});
});
