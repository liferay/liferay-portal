/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	hideProductMenuIfPresent,
	openConfirmModal,
	useMediaQuery,
} from '@liferay/layout-js-components-web';
import {render, screen, waitFor, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {openToast} from 'frontend-js-components-web';
import {fetch} from 'frontend-js-web';
import React from 'react';

import '@testing-library/jest-dom';

import VersionHistory from '../../../src/main/resources/META-INF/resources/js/components/VersionHistory';

jest.mock('@liferay/layout-js-components-web', () => {
	const react = require('react');

	return {
		SearchForm: ({onChange}: {onChange: (search: string) => void}) =>
			react.createElement('input', {
				'aria-label': 'search-form',
				'onChange': (event: {target: {value: string}}) =>
					onChange(event.target.value),
			}),
		hideProductMenuIfPresent: jest.fn(),
		openConfirmModal: jest.fn(),
		preventIframeNavigation: jest.fn(),
		useMediaQuery: jest.fn(),
	};
});

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(),
}));

const VERSIONS = [
	{
		creator: {
			externalReferenceCode: 'MARIA_ARCE',
			image: '/image/user_portrait?img_id=1',
			name: 'María Arce',
		},
		dateCreated: '2020-04-12T12:40:00Z',
		dateModified: '2020-04-12T12:40:00Z',
		externalReferenceCode: 'HOME_V_2',
		name: 'Home Halloween',
		status: 'Draft',
		statusDate: '2020-04-12T12:40:00Z',
		version: 2,
	},
	{
		creator: {
			externalReferenceCode: 'CAROLINA_RODRIGUEZ',
			image: '/image/user_portrait?img_id=0',
			name: 'Carolina Rodriguez',
		},
		dateCreated: '2020-03-01T15:40:00Z',
		dateModified: '2020-03-01T15:40:00Z',
		externalReferenceCode: 'HOME_V_1',
		name: 'Home',
		status: 'Approved',
		statusDate: '2020-03-01T15:40:00Z',
		version: 1,
	},
];

const DELETABLE_VERSIONS = [
	{
		...VERSIONS[0],
		actions: {delete: {href: '/delete/HOME_V_2', method: 'DELETE'}},
	},
	VERSIONS[1],
];

const RESTORABLE_VERSIONS = [
	{
		...VERSIONS[0],
		actions: {restore: {href: '/restore/HOME_V_2', method: 'POST'}},
	},
	VERSIONS[1],
];

const RESTORABLE_DELETABLE_VERSIONS = [
	{
		...VERSIONS[0],
		actions: {
			delete: {href: '/delete/HOME_V_2', method: 'DELETE'},
			restore: {href: '/restore/HOME_V_2', method: 'POST'},
		},
	},
	VERSIONS[1],
];

const mockFetch = fetch as jest.Mock;
const mockHideProductMenu = hideProductMenuIfPresent as jest.Mock;
const mockOpenConfirmModal = openConfirmModal as jest.Mock;
const mockOpenToast = openToast as jest.Mock;
const mockUseMediaQuery = useMediaQuery as jest.Mock;

function mockLargeScreen() {
	mockUseMediaQuery.mockReturnValue(true);
}

function mockSmallScreen() {
	mockUseMediaQuery.mockReturnValue(false);
}

function mockVersions(versions: typeof VERSIONS) {
	mockFetch.mockImplementation((url: string) =>
		Promise.resolve({
			json: () =>
				Promise.resolve({
					items: url.startsWith('/page-experiences/') ? [] : versions,
				}),
			ok: true,
		})
	);
}

function queryCurrentItem() {
	return document.querySelector('.lexicon-icon-sheets')?.closest('li');
}

async function openActions(item: HTMLElement) {
	await userEvent.click(
		within(item).getByRole('button', {name: 'show-options'})
	);
}

function renderComponent({hasDraft = false} = {}) {
	return render(
		<VersionHistory
			config={{
				availableLanguages: {},
				availableSegmentsExperiences: [],
				defaultLanguageId: 'en_US',
				defaultUserImageSrc: '/image/user_portrait?img_id=0',
				getPagePreviewURL: '/c/portal/get_page_preview',
				getPageVersionPreviewURL: '/c/portal/get_page_version_preview',
				layout: {
					name: 'Home',
					status: hasDraft ? 'draft' : 'approved',
				},
				pageSpecificationVersionPageExperiencesURL:
					'/page-experiences/{pageSpecificationVersionExternalReferenceCode}',
				pageSpecificationVersionsURL: 'url',
			}}
		/>
	);
}

describe('VersionHistory', () => {
	const {location} = window;

	beforeAll(() => {
		delete (window as any).location;

		(window as any).location = {...location, reload: jest.fn()};
	});

	afterAll(() => {
		(window as any).location = location;
	});

	beforeEach(() => {
		mockVersions([]);

		mockOpenConfirmModal.mockResolvedValue(true);

		mockHideProductMenu.mockImplementation(
			({onHide}: {onHide: () => void}) => onHide()
		);

		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'modified-by-x,-x' ? 'Modified by {0}, {1}' : key
		);
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('does not show the open button on large screens', async () => {
		mockLargeScreen();

		renderComponent();

		expect(screen.getByText('version-history')).toBeInTheDocument();

		expect(
			screen.queryByRole('button', {name: 'open-version-history-panel'})
		).not.toBeInTheDocument();
	});

	it('reveals the open button after the panel is closed on small screens', async () => {
		mockSmallScreen();

		renderComponent();

		expect(
			screen.queryByRole('button', {name: 'open-version-history-panel'})
		).not.toBeInTheDocument();

		await userEvent.click(screen.getByRole('button', {name: 'close'}));

		expect(
			screen.getByRole('button', {name: 'open-version-history-panel'})
		).toBeInTheDocument();
	});

	it('hides the product menu on mount', async () => {
		mockLargeScreen();

		renderComponent();

		expect(mockHideProductMenu).toHaveBeenCalledTimes(1);
	});

	it('leaves the panel closed until the product menu is hidden', async () => {
		mockSmallScreen();
		mockHideProductMenu.mockImplementation(() => {});

		renderComponent();

		expect(
			screen.getByRole('button', {name: 'open-version-history-panel'})
		).toBeInTheDocument();
	});

	it('only shows the current page item when there are no versions', async () => {
		mockLargeScreen();

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(1)
		);

		expect(screen.getByRole('option')).toBe(queryCurrentItem());

		expect(
			screen.queryByText('there-are-no-results')
		).not.toBeInTheDocument();
	});

	it('shows the search empty state when nothing matches the search', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		await userEvent.type(screen.getByLabelText('search-form'), 'zzz');

		expect(screen.getByText('no-results-found')).toBeInTheDocument();
		expect(
			screen.getByText('try-again-with-a-different-search')
		).toBeInTheDocument();
		expect(
			screen.queryByText('there-are-no-results')
		).not.toBeInTheDocument();
	});

	it('renders one item per version', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		expect(screen.getByText('Home Halloween')).toBeInTheDocument();
		expect(screen.getAllByText('Home')).toHaveLength(2);
	});

	it('renders the current page item as published when there is no draft', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [first] = screen.getAllByRole('option');

		expect(first).toBe(queryCurrentItem());
		expect(first).toHaveTextContent('Home');
		expect(first).toHaveTextContent('current-page');
		expect(first).toHaveTextContent('published');
	});

	it('renders the current page item as draft when there is a draft', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [first] = screen.getAllByRole('option');

		expect(first).toBe(queryCurrentItem());
		expect(first).toHaveTextContent('Home');
		expect(first).toHaveTextContent('current-page');
		expect(first).toHaveTextContent('draft');
	});

	it('filters out the current page item when it does not match the search', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		await userEvent.type(screen.getByLabelText('search-form'), 'Halloween');

		expect(screen.getAllByRole('option')).toHaveLength(1);
		expect(queryCurrentItem()).toBeUndefined();
	});

	it('selects the current page item by default', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [current, ...rest] = screen.getAllByRole('option');

		expect(current).toHaveClass('active');

		for (const item of rest) {
			expect(item).not.toHaveClass('active');
		}
	});

	it('selects an item when it is clicked', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [current, version] = screen.getAllByRole('option');

		await userEvent.click(version);

		expect(version).toHaveClass('active');
		expect(version).toHaveAttribute('aria-selected', 'true');
		expect(current).not.toHaveClass('active');

		await userEvent.click(current);

		expect(current).toHaveClass('active');
		expect(version).not.toHaveClass('active');
	});

	it('only keeps the navigation target in the tab order', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const items = screen.getAllByRole('option');

		expect(items[0]).toHaveAttribute('tabindex', '0');
		expect(items[1]).toHaveAttribute('tabindex', '-1');
		expect(items[2]).toHaveAttribute('tabindex', '-1');

		expect(items.every((item) => !within(item).queryByRole('button'))).toBe(
			true
		);
	});

	it('walks the list with the arrow keys and selects with Enter', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [current, second, third] = screen.getAllByRole('option');

		current.focus();

		expect(current).toHaveFocus();

		await userEvent.keyboard('{ArrowDown}');

		expect(second).toHaveFocus();
		expect(second).toHaveAttribute('tabindex', '0');

		await userEvent.keyboard('{ArrowDown}');

		expect(third).toHaveFocus();

		await userEvent.keyboard('{Enter}');

		expect(third).toHaveClass('active');

		await userEvent.keyboard('{ArrowUp}');

		expect(second).toHaveFocus();
		expect(third).toHaveClass('active');
	});

	it('stops at the ends of the list', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const items = screen.getAllByRole('option');

		items[0].focus();

		await userEvent.keyboard('{ArrowUp}');

		expect(items[0]).toHaveFocus();

		items[2].focus();

		await userEvent.keyboard('{ArrowDown}');

		expect(items[2]).toHaveFocus();
	});

	it('renders the portrait of the user who modified every version', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		expect(
			await screen.findByRole('img', {name: 'María Arce'})
		).toHaveAttribute('src', '/image/user_portrait?img_id=1');

		expect(
			screen.getByRole('img', {name: 'Carolina Rodriguez'})
		).toHaveAttribute('src', '/image/user_portrait?img_id=0');
	});

	it('renders the modifier, the date and the status of every version', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		expect(
			await screen.findByText(
				'Modified by María Arce, 04/12/2020 12:40 PM'
			)
		).toBeInTheDocument();

		expect(
			screen.getByText(
				'Modified by Carolina Rodriguez, 03/01/2020 3:40 PM'
			)
		).toBeInTheDocument();

		expect(screen.getByText('draft')).toBeInTheDocument();
		expect(screen.getAllByText('published')).toHaveLength(2);
	});

	it('filters the versions by name and by modifier', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const search = screen.getByLabelText('search-form');

		await userEvent.type(search, 'Halloween');

		expect(screen.getAllByRole('option')).toHaveLength(1);
		expect(screen.getByText('Home Halloween')).toBeInTheDocument();

		await userEvent.clear(search);
		await userEvent.type(search, 'Carolina');

		expect(screen.getAllByRole('option')).toHaveLength(1);
		expect(screen.getByText('Home')).toBeInTheDocument();

		await userEvent.clear(search);
		await userEvent.type(search, 'zzz');

		expect(screen.queryAllByRole('option')).toHaveLength(0);
		expect(screen.getByText('no-results-found')).toBeInTheDocument();
	});

	it('filters the versions by version number', async () => {
		mockLargeScreen();
		mockVersions(VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		await userEvent.type(screen.getByLabelText('search-form'), '2');

		expect(screen.getAllByRole('option')).toHaveLength(1);
		expect(screen.getByText('Home Halloween')).toBeInTheDocument();
	});

	it('only renders the actions menu for versions with a delete action', async () => {
		mockLargeScreen();
		mockVersions(DELETABLE_VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [current, deletable, notDeletable] =
			screen.getAllByRole('option');

		expect(
			within(deletable).getByRole('button', {name: 'show-options'})
		).toBeInTheDocument();

		expect(
			within(current).queryByRole('button', {name: 'show-options'})
		).not.toBeInTheDocument();

		expect(
			within(notDeletable).queryByRole('button', {name: 'show-options'})
		).not.toBeInTheDocument();
	});

	it('selects the first item after deleting the selected version', async () => {
		mockLargeScreen();
		mockVersions(DELETABLE_VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [current, deletable] = screen.getAllByRole('option');

		await userEvent.click(deletable);

		expect(deletable).toHaveClass('active');
		expect(current).not.toHaveClass('active');

		await userEvent.click(
			within(deletable).getByRole('button', {name: 'show-options'})
		);

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'delete-version'})
		);

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(2)
		);

		expect(screen.queryByText('Home Halloween')).not.toBeInTheDocument();

		const [first] = screen.getAllByRole('option');

		expect(first).toBe(queryCurrentItem());
		expect(first).toHaveClass('active');
	});

	it('only offers the restore action for versions with a restore action', async () => {
		mockLargeScreen();
		mockVersions(RESTORABLE_VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [current, restorable, notRestorable] =
			screen.getAllByRole('option');

		expect(
			within(current).queryByRole('button', {name: 'show-options'})
		).not.toBeInTheDocument();

		expect(
			within(notRestorable).queryByRole('button', {name: 'show-options'})
		).not.toBeInTheDocument();

		await openActions(restorable);

		expect(
			screen.getByRole('menuitem', {name: 'restore-version'})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: 'delete-version'})
		).not.toBeInTheDocument();
	});

	it('offers the restore action before the delete action, split by a divider', async () => {
		mockLargeScreen();
		mockVersions(RESTORABLE_DELETABLE_VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [, version] = screen.getAllByRole('option');

		await openActions(version);

		const [restore, remove] = screen.getAllByRole('menuitem');

		expect(restore).toHaveTextContent('restore-version');
		expect(remove).toHaveTextContent('delete-version');

		expect(
			restore.querySelector('.lexicon-icon-restore')
		).toBeInTheDocument();

		expect(screen.getByRole('separator')).toBeInTheDocument();
	});

	it('restores the version once the confirmation is accepted', async () => {
		mockLargeScreen();
		mockVersions(RESTORABLE_VERSIONS);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [, restorable] = screen.getAllByRole('option');

		await openActions(restorable);

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'restore-version'})
		);

		expect(mockOpenConfirmModal).toHaveBeenCalledTimes(1);

		await waitFor(() =>
			expect(mockFetch).toHaveBeenCalledWith(
				'/restore/HOME_V_2',
				expect.objectContaining({method: 'POST'})
			)
		);
	});

	it('restores the version without confirmation when there is no draft', async () => {
		mockLargeScreen();
		mockVersions(RESTORABLE_VERSIONS);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [, restorable] = screen.getAllByRole('option');

		await openActions(restorable);

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'restore-version'})
		);

		await waitFor(() =>
			expect(mockFetch).toHaveBeenCalledWith(
				'/restore/HOME_V_2',
				expect.objectContaining({method: 'POST'})
			)
		);

		expect(mockOpenConfirmModal).not.toHaveBeenCalled();
	});

	it('does not restore the version when the confirmation is dismissed', async () => {
		mockLargeScreen();
		mockVersions(RESTORABLE_VERSIONS);
		mockOpenConfirmModal.mockResolvedValue(false);

		renderComponent({hasDraft: true});

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [, restorable] = screen.getAllByRole('option');

		await openActions(restorable);

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'restore-version'})
		);

		await waitFor(() =>
			expect(mockOpenConfirmModal).toHaveBeenCalledTimes(1)
		);

		expect(mockFetch).not.toHaveBeenCalledWith(
			'/restore/HOME_V_2',
			expect.anything()
		);
	});

	it('shows an error toast when the restore fails', async () => {
		mockLargeScreen();

		mockFetch.mockImplementation((url: string) =>
			Promise.resolve(
				url === '/restore/HOME_V_2'
					? {
							json: () =>
								Promise.resolve({title: 'restore-failed'}),
							ok: false,
						}
					: {
							json: () =>
								Promise.resolve({items: RESTORABLE_VERSIONS}),
							ok: true,
						}
			)
		);

		renderComponent();

		await waitFor(() =>
			expect(screen.getAllByRole('option')).toHaveLength(3)
		);

		const [, restorable] = screen.getAllByRole('option');

		await openActions(restorable);

		await userEvent.click(
			screen.getByRole('menuitem', {name: 'restore-version'})
		);

		await waitFor(() =>
			expect(mockOpenToast).toHaveBeenCalledWith({
				message: 'restore-failed',
				type: 'danger',
			})
		);
	});
});
