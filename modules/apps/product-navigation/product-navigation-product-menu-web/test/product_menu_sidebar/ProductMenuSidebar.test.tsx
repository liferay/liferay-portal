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

const renderComponent = () =>
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
});
