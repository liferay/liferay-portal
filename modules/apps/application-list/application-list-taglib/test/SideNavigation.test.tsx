/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {configure} from '@testing-library/dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {fetch} from 'frontend-js-web';
import React from 'react';

import {SideNavigation} from '../src/main/resources/META-INF/resources/js';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	fetch: jest.fn(),
}));

configure({
	testIdAttribute: 'data-qa-id',
});

const NAVIGATION_ITEMS = {
	assets: [
		{
			canonicalName: 'categoriesCanonicalName',
			href: 'categoriesHref',
			id: 'assets_0',
			label: 'Categories',
		},
		{
			canonicalName: 'vocabulariesCanonicalName',
			href: 'vocabulariesHref',
			id: 'assets_1',
			label: 'Vocabularies',
			parentLabel: 'Categories',
		},
	],
};

const ITEMS = [
	{
		id: 'content',
		items: [
			{
				canonicalName: 'assetsCanonicalName',
				href: 'assetsHref',
				id: 'assets',
				label: 'Assets',
				leadingIcon: 'assetsIcon',
			},
			{
				canonicalName: 'dashboardCanonicalName',
				href: 'dashboardHref',
				id: 'dashboard',
				label: 'Dashboard',
				leadingIcon: 'dashboardIcon',
			},
		],
		label: 'Content',
	},
	{
		id: 'workflow',
		items: [
			{
				canonicalName: 'metricsCanonicalName',
				href: 'metricsHref',
				id: 'metrics',
				label: 'Metrics',
				leadingIcon: 'metricsIcon',
			},
		],
		label: 'Workflow',
	},
];

const renderComponent = ({expandedKeys = ['content', 'workflow']} = {}) =>
	render(
		<SideNavigation
			canonicalName="sideNavigationCanonicalName"
			categoryImageUrl="categoryImageUrl"
			colorScheme="light"
			colorSchemeSessionKey="colorSchemeSessionKey"
			expandedKeys={expandedKeys}
			expandedKeysSessionKey="expandedKeysSessionKey"
			items={ITEMS}
			label="Applications"
			navigationItemsURL="navigationItemsURL"
			selectedPortletId="assets"
			siteAdministrationItemSelectedEventName="siteAdministrationItemSelectedEventName"
			siteAdministrationItemSelectorUrl="siteAdministrationItemSelectorUrl"
			visible
			visibleSessionKey="visibleSessionKey"
		/>
	);

describe('SideNavigation', () => {
	const languageGet = Liferay.Language.get as jest.Mock;
	const languageGetImplementation = languageGet.getMockImplementation();

	afterEach(() => {
		languageGet.mockImplementation(languageGetImplementation!);
	});

	beforeEach(() => {
		Liferay.Util = {
			...Liferay.Util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};

		(fetch as jest.Mock).mockReset();
		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({navigationItems: NAVIGATION_ITEMS}),
			ok: true,
		});
	});

	it('renders the side navigation with canonical name', () => {
		const {getByRole, getByTestId} = renderComponent();

		const sideNavigation = getByTestId('sideNavigation');

		expect(sideNavigation).toBeInTheDocument();
		expect(sideNavigation).toHaveAttribute(
			'data-canonical-name',
			'sideNavigationCanonicalName'
		);

		const assetsItem = getByRole('menuitem', {name: 'Assets'});

		expect(assetsItem.parentElement).toBeInTheDocument();
		expect(assetsItem.parentElement).toHaveAttribute(
			'data-canonical-name',
			'assetsCanonicalName'
		);
	});

	it('renders the side navigation header', () => {
		renderComponent();

		const title = screen.getByText('Applications');

		expect(title).toBeInTheDocument();

		const icon = screen.getByTestId('sideNavigationProductIcon');

		expect(icon).toHaveAttribute('src', 'categoryImageUrl');
	});

	it('renders each navigation item', () => {
		renderComponent();

		const menuItems = screen.getAllByRole('menuitem');

		expect(menuItems).toHaveLength(5);

		['Content', 'Workflow'].forEach((label) => {
			expect(screen.getByText(label)).toHaveAttribute(
				'aria-expanded',
				'true'
			);
		});

		['Assets', 'Dashboard', 'Metrics'].forEach((label) => {
			expect(screen.getByText(label)).toHaveAttribute(
				'href',
				`${label.toLowerCase()}Href`
			);
		});

		expect(screen.getByText('Assets')).toHaveClass('active');
		expect(screen.getByText('Workflow')).not.toHaveClass('active');
		expect(screen.getByText('Metrics')).not.toHaveClass('active');
	});

	it('shows only the navigation items from the expanded keys', () => {
		renderComponent({expandedKeys: ['workflow']});

		const menuItems = screen.getAllByRole('menuitem');

		expect(menuItems).toHaveLength(3);

		expect(screen.getByText('Content')).toHaveAttribute(
			'aria-expanded',
			'false'
		);

		expect(screen.getByText('Workflow')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
	});

	it('hides the filter-only items until the query matches them', async () => {
		renderComponent();

		expect(screen.queryByText('Categories')).not.toBeInTheDocument();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'categories'
		);

		const categoriesItem = await screen.findByText('Categories');

		expect(categoriesItem).toHaveAttribute('href', 'categoriesHref');
		expect(screen.getByText('Assets')).toBeInTheDocument();
		expect(screen.queryByText('Dashboard')).not.toBeInTheDocument();
	});

	it('does not let an application expand into its screens without a query', async () => {
		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'categories'
		);

		await screen.findByText('Categories');

		await userEvent.clear(screen.getByTestId('sideNavigationSearchInput'));

		await waitFor(() =>
			expect(screen.queryByText('Categories')).not.toBeInTheDocument()
		);

		const assetsItem = screen.getByText('Assets');

		expect(assetsItem).not.toHaveAttribute('aria-expanded');
		expect(assetsItem).toHaveAttribute('href', 'assetsHref');

		await userEvent.click(assetsItem);

		expect(screen.queryByText('Vocabularies')).not.toBeInTheDocument();
	});

	it('keeps the filter-only items hidden when only their parent matches', async () => {
		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'assets'
		);

		await waitFor(() =>
			expect(screen.queryByText('Dashboard')).not.toBeInTheDocument()
		);

		expect(screen.getByText('Assets')).toBeInTheDocument();
		expect(screen.queryByText('Categories')).not.toBeInTheDocument();
	});

	it('clears the query with the clear button and restores the tree', async () => {
		renderComponent();

		expect(
			screen.queryByTestId('sideNavigationClearSearchButton')
		).not.toBeInTheDocument();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'categories'
		);

		await waitFor(() =>
			expect(screen.queryByText('Dashboard')).not.toBeInTheDocument()
		);

		await userEvent.click(
			screen.getByTestId('sideNavigationClearSearchButton')
		);

		await waitFor(() =>
			expect(screen.getByText('Dashboard')).toBeInTheDocument()
		);

		expect(screen.getByTestId('sideNavigationSearchInput')).toHaveValue('');
		expect(screen.queryByText('Categories')).not.toBeInTheDocument();
		expect(
			screen.queryByTestId('sideNavigationClearSearchButton')
		).not.toBeInTheDocument();
	});

	it('collapses a group while the filter is active without persisting it', async () => {
		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'categories'
		);

		await screen.findByText('Categories');

		const contentItem = screen.getByText('Content');

		expect(contentItem).toHaveAttribute('aria-expanded', 'true');

		(Liferay.Util.Session.set as jest.Mock).mockClear();

		await userEvent.click(contentItem);

		await waitFor(() =>
			expect(screen.getByText('Content')).toHaveAttribute(
				'aria-expanded',
				'false'
			)
		);

		await waitFor(() =>
			expect(screen.queryByText('Categories')).not.toBeInTheDocument()
		);

		expect(Liferay.Util.Session.set).not.toHaveBeenCalled();
	});

	it('discards a collapse made while filtering once the query changes', async () => {
		renderComponent();

		const searchInput = screen.getByTestId('sideNavigationSearchInput');

		await userEvent.type(searchInput, 'categories');

		await screen.findByText('Categories');

		await userEvent.click(screen.getByText('Content'));

		await waitFor(() =>
			expect(screen.getByText('Content')).toHaveAttribute(
				'aria-expanded',
				'false'
			)
		);

		await userEvent.clear(searchInput);

		await userEvent.type(searchInput, 'vocabularies');

		await screen.findByText('Vocabularies');

		expect(screen.getByText('Content')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
	});

	it('names the parent of a matching item that is nested below a screen', async () => {
		(Liferay.Language.get as jest.Mock).mockImplementation((key: string) =>
			key === 'in-x' ? 'In {0}' : key
		);

		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'vocabularies'
		);

		const vocabulariesItem = await screen.findByText('Vocabularies');

		expect(vocabulariesItem).toHaveAttribute('href', 'vocabulariesHref');
		expect(screen.getByText('In Categories')).toBeInTheDocument();
		expect(screen.queryByText('Categories')).not.toBeInTheDocument();
	});

	it('does not fetch the application screens before the filter is focused', () => {
		renderComponent();

		expect(fetch).not.toHaveBeenCalled();
	});

	it('fetches the application screens only once', async () => {
		renderComponent();

		const searchInput = screen.getByTestId('sideNavigationSearchInput');

		fireEvent.focus(searchInput);
		fireEvent.blur(searchInput);
		fireEvent.focus(searchInput);

		await waitFor(() => expect(fetch).toHaveBeenCalledTimes(1));

		expect(fetch).toHaveBeenCalledWith('navigationItemsURL');
	});

	it('leaves the visible navigation unchanged when the screens arrive', async () => {
		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'categories'
		);

		await screen.findByText('Categories');

		await userEvent.clear(screen.getByTestId('sideNavigationSearchInput'));

		await waitFor(() =>
			expect(screen.queryByText('Categories')).not.toBeInTheDocument()
		);

		expect(screen.getAllByRole('menuitem')).toHaveLength(5);
		expect(screen.getByText('Assets')).not.toHaveAttribute('aria-expanded');
	});

	it('shows a skeleton rather than an empty state while the screens are in flight', async () => {
		let resolveFetch!: (value: unknown) => void;

		(fetch as jest.Mock).mockReturnValue(
			new Promise((resolve) => {
				resolveFetch = resolve;
			})
		);

		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'vocabularies'
		);

		await waitFor(() =>
			expect(screen.getByRole('progressbar')).toBeInTheDocument()
		);

		expect(screen.queryByText('no-matching-items')).not.toBeInTheDocument();

		resolveFetch({
			json: () => Promise.resolve({navigationItems: NAVIGATION_ITEMS}),
			ok: true,
		});

		expect(await screen.findByText('Vocabularies')).toBeInTheDocument();
		expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
	});

	it('shows the empty state when the screens fail to load', async () => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({}),
			ok: false,
		});

		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'vocabularies'
		);

		expect(
			await screen.findByText('no-matching-items')
		).toBeInTheDocument();

		expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
	});

	it('keeps filtering the applications when the screens fail to load', async () => {
		(fetch as jest.Mock).mockResolvedValue({
			json: () => Promise.resolve({}),
			ok: false,
		});

		renderComponent();

		await userEvent.type(
			screen.getByTestId('sideNavigationSearchInput'),
			'assets'
		);

		expect(await screen.findByText('Assets')).toBeInTheDocument();
	});

	it('fetches the application screens again after a failure', async () => {
		(fetch as jest.Mock).mockRejectedValue(new Error('rejected'));

		renderComponent();

		const searchInput = screen.getByTestId('sideNavigationSearchInput');

		fireEvent.focus(searchInput);

		await waitFor(() => expect(fetch).toHaveBeenCalledTimes(1));

		fireEvent.blur(searchInput);
		fireEvent.focus(searchInput);

		await waitFor(() => expect(fetch).toHaveBeenCalledTimes(2));
	});
});
