/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';

import FrontendDataSetContext from '../../../../src/main/resources/META-INF/resources/FrontendDataSetContext';
import SnapshotsControls from '../../../../src/main/resources/META-INF/resources/management_bar/controls/snapshots/SnapshotsControls';
import ViewsContext from '../../../../src/main/resources/META-INF/resources/views/ViewsContext';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/management_bar/controls/snapshots/shareSnapshotAction',
	() => jest.fn()
);

global.Liferay = {
	Language: {
		get: (key: string) => key,
	},
} as any;

const mockFDSContext = {
	globalFDSState: {filters: []},
	id: 'testFDS',
	namespace: 'testNamespace_',
	onSnapshotChange: jest.fn(),
	portletId: 'testPortlet',
};

const ownedSnapshot = {erc: 'owned-erc', id: 1, label: 'Owned View'};
const sharedSnapshot = {erc: 'shared-erc', id: 2, label: 'Shared View'};

const renderSnapshotsControls = (viewsState: any) =>
	render(
		<FrontendDataSetContext.Provider value={mockFDSContext as any}>
			<ViewsContext.Provider value={[viewsState, jest.fn()] as any}>
				<SnapshotsControls />
			</ViewsContext.Provider>
		</FrontendDataSetContext.Provider>
	);

const openActionsDropdown = async () => {
	await userEvent.click(
		screen.getByRole('button', {name: 'show-view-actions'})
	);
};

const openViewsDropdown = async () => {
	await userEvent.click(screen.getByRole('button', {name: 'views'}));
};

const getViewLabels = () =>
	screen.queryAllByRole('menuitem').map((item) => item.textContent);

describe('SnapshotsControls action gating', () => {
	describe('when the active snapshot is owned by the current user', () => {
		beforeEach(() => {
			renderSnapshotsControls({
				activeSnapshotERC: ownedSnapshot.erc,
				activeView: null,
				defaultSnapshot: {},
				paginationDelta: null,
				snapshotUpdated: false,
				snapshots: [{headerVisible: false, items: [ownedSnapshot]}],
				sorts: [],
				visibleFieldNames: {},
			});
		});

		it('shows every owner action plus "Save View As"', async () => {
			await openActionsDropdown();

			expect(await screen.findByText('save-view')).toBeInTheDocument();
			expect(screen.getByText('save-view-as')).toBeInTheDocument();
			expect(screen.getByText('rename-view')).toBeInTheDocument();
			expect(screen.getByText('share-view')).toBeInTheDocument();
			expect(screen.getByText('delete-view')).toBeInTheDocument();
		});
	});

	describe('when the active snapshot is shared with the current user', () => {
		beforeEach(() => {
			renderSnapshotsControls({
				activeSnapshotERC: sharedSnapshot.erc,
				activeView: null,
				defaultSnapshot: {},
				paginationDelta: null,
				snapshotUpdated: false,
				snapshots: [
					{headerVisible: false, items: []},
					{
						headerVisible: true,
						items: [sharedSnapshot],
						label: 'shared-with-me',
					},
				],
				sorts: [],
				visibleFieldNames: {},
			});
		});

		it('only offers "Save View As" and hides every owner action', async () => {
			await openActionsDropdown();

			expect(await screen.findByText('save-view-as')).toBeInTheDocument();
			expect(screen.queryByText('save-view')).not.toBeInTheDocument();
			expect(screen.queryByText('rename-view')).not.toBeInTheDocument();
			expect(screen.queryByText('share-view')).not.toBeInTheDocument();
			expect(screen.queryByText('delete-view')).not.toBeInTheDocument();
		});
	});
});

describe('SnapshotsControls views search', () => {
	beforeEach(() => {
		renderSnapshotsControls({
			activeSnapshotERC: null,
			activeView: null,
			defaultSnapshot: {},
			paginationDelta: null,
			snapshotUpdated: false,
			snapshots: [
				{
					headerVisible: false,
					items: [
						{erc: 'erc-1', id: 1, label: 'Active Orders'},
						{erc: 'erc-2', id: 2, label: 'Archived Orders'},
						{erc: 'erc-3', id: 3, label: 'Pending Invoices'},
					],
				},
				{
					headerVisible: true,
					items: [{erc: 'erc-4', id: 4, label: 'Team Orders'}],
					label: 'shared-with-me',
				},
			],
			sorts: [],
			visibleFieldNames: {},
		});
	});

	const searchViews = async (query: string) =>
		fireEvent.change(await screen.findByPlaceholderText('search'), {
			target: {value: query},
		});

	it('shows a search input inside the views dropdown', async () => {
		await openViewsDropdown();

		expect(
			await screen.findByPlaceholderText('search')
		).toBeInTheDocument();
	});

	it('filters the views in real time as the user types', async () => {
		await openViewsDropdown();

		await searchViews('archived');

		await waitFor(() =>
			expect(getViewLabels()).toEqual(['Archived Orders'])
		);
	});

	it('matches views case-insensitively', async () => {
		await openViewsDropdown();

		await searchViews('PENDING');

		await waitFor(() =>
			expect(getViewLabels()).toEqual(['Pending Invoices'])
		);
	});

	it('filters across all sections, including shared views', async () => {
		await openViewsDropdown();

		await searchViews('orders');

		await waitFor(() =>
			expect(getViewLabels()).toEqual([
				'Active Orders',
				'Archived Orders',
				'Team Orders',
			])
		);
	});

	it('communicates an empty state when no view matches', async () => {
		await openViewsDropdown();

		await searchViews('nonexistent');

		await waitFor(() =>
			expect(screen.queryAllByRole('menuitem')).toHaveLength(0)
		);
		expect(screen.getByText('no-results-found')).toBeInTheDocument();
	});

	it('shows a clear button only once a search term is typed', async () => {
		await openViewsDropdown();

		await screen.findByPlaceholderText('search');

		expect(
			screen.queryByRole('button', {name: 'clear'})
		).not.toBeInTheDocument();

		await searchViews('arch');

		expect(
			await screen.findByRole('button', {name: 'clear'})
		).toBeInTheDocument();
	});

	it('restores the full list when the clear button is clicked', async () => {
		await openViewsDropdown();

		await searchViews('archived');

		await waitFor(() =>
			expect(getViewLabels()).toEqual(['Archived Orders'])
		);

		await userEvent.click(screen.getByRole('button', {name: 'clear'}));

		await waitFor(() =>
			expect(getViewLabels()).toEqual([
				'default-view',
				'Active Orders',
				'Archived Orders',
				'Pending Invoices',
				'Team Orders',
			])
		);
	});
});
