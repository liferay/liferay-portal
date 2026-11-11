/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {configure} from '@testing-library/dom';
import {act, render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {UnifiedSidebarItem} from 'frontend-js-components-web';
import React from 'react';

import ProductMenuSidebar from '../../src/main/resources/META-INF/resources/js/product_menu_sidebar/ProductMenuSidebar';

configure({
	testIdAttribute: 'data-qa-id',
});

const ITEMS: Array<UnifiedSidebarItem> = [
	{
		id: 'site_administration.content',
		items: [
			{
				canonicalName: 'blogsCanonicalName',
				href: 'blogsHref',
				id: 'blogs',
				label: 'Blogs',
				leadingIcon: 'blogs',
			},
			{
				canonicalName: 'webContentCanonicalName',
				href: 'webContentHref',
				id: 'webContent',
				label: 'Web Content',
				leadingIcon: 'web-content',
			},
		],
		label: 'Content & Data',
	},
];

const renderComponent = (
	props: Partial<React.ComponentProps<typeof ProductMenuSidebar>> = {}
) =>
	render(
		<ProductMenuSidebar
			canonicalName="siteAdministrationCanonicalName"
			expandedKeys={['site_administration.content']}
			expandedKeysSessionKey="expandedKeysSessionKey"
			imageURL="imageURL"
			items={ITEMS}
			label="Liferay"
			navigationItemsURL="navigationItemsURL"
			selectedPortletId="blogs"
			siteItemSelectedEventName="siteItemSelectedEventName"
			siteItemSelectorURL="siteItemSelectorURL"
			visible
			visibleSessionKey="visibleSessionKey"
			{...props}
		/>
	);

describe('ProductMenuSidebar', () => {
	let overlay = false;

	afterEach(() => {
		delete (window as any).IntersectionObserver;
		delete (window as any).matchMedia;

		document.body.classList.remove(
			'open',
			'product-menu-open',
			'sidenav-transition'
		);

		jest.useRealTimers();
	});

	beforeEach(() => {
		overlay = false;

		(window as any).IntersectionObserver = class {
			disconnect() {}

			observe() {}

			unobserve() {}
		};

		(window as any).matchMedia = (query: string) => ({
			addEventListener: () => {},
			matches: overlay,
			media: query,
			removeEventListener: () => {},
		});

		document.body.classList.add('open', 'product-menu-open');

		jest.clearAllMocks();

		Liferay.Util = {
			...Liferay.Util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};
	});

	it('renders the site name and the applications by category', () => {
		renderComponent();

		expect(screen.getByText('Liferay')).toBeInTheDocument();
		expect(screen.getByText('Content & Data')).toHaveAttribute(
			'aria-expanded',
			'true'
		);
		expect(screen.getByText('Blogs')).toHaveClass('active');
		expect(screen.getByText('Web Content')).toHaveAttribute(
			'href',
			'webContentHref'
		);
	});

	it('closes from the backdrop on a narrow screen', async () => {
		overlay = true;

		renderComponent();

		const [, handleStateRequest] = (
			Liferay.on as jest.Mock
		).mock.calls.find(
			([eventName]) => eventName === 'productMenuSidebarStateRequested'
		);

		await act(() => handleStateRequest({visible: true}));

		expect(document.body).toHaveClass('product-menu-open');

		await userEvent.click(
			document.body.querySelector('.cadmin > .c-slideout-backdrop')!
		);

		await waitFor(() =>
			expect(document.body).not.toHaveClass('product-menu-open')
		);

		expect(Liferay.Util.Session.set).toHaveBeenLastCalledWith(
			'visibleSessionKey',
			'closed'
		);
	});

	it('does not show a backdrop on a wide screen', () => {
		renderComponent();

		expect(document.body).toHaveClass('product-menu-open');
		expect(document.querySelector('.c-slideout-backdrop')).toBeNull();
	});

	it('has no accessibility violations', async () => {
		const {container} = renderComponent();

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('saves the expanded categories', async () => {
		renderComponent();

		await userEvent.click(screen.getByText('Content & Data'));

		expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
			'expandedKeysSessionKey',
			''
		);
	});

	it('starts closed on a narrow screen without saving the state', () => {
		overlay = true;

		renderComponent();

		expect(document.body).not.toHaveClass('product-menu-open');
		expect(Liferay.fire).toHaveBeenCalledWith(
			'productMenuSidebarStateChanged',
			{visible: false}
		);
		expect(Liferay.Util.Session.set).not.toHaveBeenCalled();
	});

	it('stops animating the page when no transition ends', async () => {
		jest.useFakeTimers();

		renderComponent();

		const user = userEvent.setup({
			advanceTimers: jest.advanceTimersByTime,
		});

		await user.click(screen.getByLabelText('close-product-menu'));

		await waitFor(() =>
			expect(document.body).toHaveClass('sidenav-transition')
		);

		act(() => {
			jest.advanceTimersByTime(300);
		});

		expect(document.body).not.toHaveClass('sidenav-transition');
	});

	it('links to the other environment from the staging toggle', () => {
		renderComponent({
			staging: {
				current: 'staging',
				liveURL: 'liveURL',
				remote: false,
				stagingURL: '',
			},
		});

		expect(screen.getByRole('link', {name: 'live'})).toHaveAttribute(
			'href',
			'liveURL'
		);
		expect(screen.getByText('staging')).toHaveAttribute(
			'aria-current',
			'page'
		);
	});

	describe('with the Page Tree', () => {
		const PAGE_TREE_DATA = {
			config: {
				administrationPortletNamespace: 'administrationNamespace_',
				administrationPortletURL: 'administrationPortletURL',
				findLayoutsURL: 'http://localhost/findLayoutsURL',
				loadMoreItemsURL: 'loadMoreItemsURL',
				maxPageSize: 20,
				moveItemURL: 'moveItemURL',
				stagingEnabled: false,
			},
			hasAdministrationPortletPermission: true,
			isPrivateLayoutsTree: false,
			isSiteNavigationMenu: false,
			items: [],
			pageTypeOptions: [
				{
					items: [
						{label: 'Public Pages', value: 'public-layout'},
						{label: 'Private Pages', value: 'private-layout'},
					],
					label: 'Pages',
				},
			],
			pageTypeSelectedOption: 'public-layout',
			portletNamespace: 'productMenuNamespace_',
			selectedLayoutId: 0,
			selectedLayoutPath: [],
			showAddIcon: false,
		};

		const PAGE_TREE_ITEM = {
			id: 'pageTree',
			label: 'Page Tree',
			leadingIcon: 'pages-tree',
			pageTree: true,
		};

		let pageTreeRequests = 0;

		const renderPageTree = (visible: boolean, disabled = false) =>
			renderComponent({
				items: [{...PAGE_TREE_ITEM, disabled}, ...ITEMS],
				navigationItemsURL: 'http://localhost/navigationItemsURL',
				pageTree: {
					stateSessionKey: 'pageTreeStateSessionKey',
					url: 'http://localhost/pageTreeURL',
					visible,
				},
			});

		beforeEach(() => {
			pageTreeRequests = 0;

			(fetch as any).mockResponse((request: Request) => {
				if (request.url.endsWith('/findLayoutsURL')) {
					return Promise.resolve(
						JSON.stringify({
							layouts: [
								{
									name: 'Home',
									path: ['Welcome'],
									target: '_self',
									url: 'homeURL',
								},
							],
							totalCount: 1,
						})
					);
				}

				if (request.url.endsWith('/pageTreeURL')) {
					pageTreeRequests++;

					return Promise.resolve(JSON.stringify(PAGE_TREE_DATA));
				}

				return Promise.resolve(JSON.stringify({navigationItems: {}}));
			});
		});

		it('comes back from the Page Tree with an empty search', async () => {
			renderPageTree(false);

			await userEvent.type(
				screen.getByTestId('sideNavigationSearchInput'),
				'page'
			);

			await waitFor(() =>
				expect(
					screen.queryByText('Web Content')
				).not.toBeInTheDocument()
			);

			await userEvent.click(
				await screen.findByRole('menuitem', {name: 'Page Tree'})
			);

			expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
				'pageTreeStateSessionKey',
				expect.any(String)
			);

			await userEvent.click(screen.getByLabelText('go-back'));

			expect(screen.getByTestId('sideNavigationSearchInput')).toHaveValue(
				''
			);
			expect(screen.getByText('Web Content')).toBeInTheDocument();
			expect(Liferay.Util.Session.set).toHaveBeenLastCalledWith(
				'pageTreeStateSessionKey',
				''
			);
		});

		it('disables the Page Tree item when asked to', async () => {
			renderPageTree(false, true);

			const pageTreeItem = screen.getByRole('menuitem', {
				name: 'Page Tree',
			});

			expect(pageTreeItem).toBeDisabled();

			await userEvent.click(pageTreeItem);

			expect(screen.queryByLabelText('go-back')).not.toBeInTheDocument();
			expect(pageTreeRequests).toBe(0);
		});

		it('finds pages by keyword', async () => {
			renderPageTree(true);

			await screen.findByText('go-to-pages-administration');

			await userEvent.type(
				screen.getByTestId('sideNavigationSearchInput'),
				'home'
			);

			expect(
				await screen.findByRole('link', {name: /Home/})
			).toHaveAttribute('href', 'homeURL');
			expect(
				screen.queryByLabelText('pages-type')
			).not.toBeInTheDocument();
		});

		it('reloads the Page Tree when the page type changes', async () => {
			renderPageTree(true);

			await userEvent.click(await screen.findByLabelText('pages-type'));

			await userEvent.click(
				screen.getByRole('option', {name: 'Private Pages'})
			);

			expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
				'productMenuNamespace_PAGE_TYPE_SELECTED_OPTION',
				'private-layout'
			);

			await waitFor(() => expect(pageTreeRequests).toBe(2));
		});

		it('shows the last Page Tree right away when it mounts again', async () => {
			const {unmount} = renderPageTree(true);

			await screen.findByText('go-to-pages-administration');

			unmount();

			renderPageTree(true);

			expect(
				screen.getByText('go-to-pages-administration')
			).toBeInTheDocument();

			await waitFor(() => expect(pageTreeRequests).toBe(2));
		});

		it('shows the Page Tree with the site selector hidden', async () => {
			renderPageTree(true);

			expect(
				await screen.findByRole('link', {
					name: 'go-to-pages-administration',
				})
			).toHaveAttribute('href', 'administrationPortletURL');
			expect(screen.getByText('page-tree')).toBeInTheDocument();
			expect(
				screen.queryByLabelText('go-to-other-site')
			).not.toBeInTheDocument();
		});
	});
});
