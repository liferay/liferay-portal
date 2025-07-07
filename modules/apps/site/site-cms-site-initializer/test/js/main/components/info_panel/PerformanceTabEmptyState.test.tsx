/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import {getEmptyState} from '../../../../../src/main/resources/META-INF/resources/js/main/components/info_panel/EmptyState';

const renderEmptyState = (data: any) => {
	const Component = getEmptyState(data);

	return render(<>{Component}</>);
};

describe('getEmptyState', () => {
	it('renders site not connected (admin)', () => {
		renderEmptyState({
			connectedToSpace: false,
			isAdmin: true,
		});

		expect(
			screen.getByText('no-sites-are-connected-yet')
		).toBeInTheDocument();
		expect(screen.getByText('connect')).toBeInTheDocument();
	});

	it('renders site not connected (non-admin)', () => {
		renderEmptyState({
			connectedToSpace: false,
			isAdmin: false,
		});

		expect(
			screen.getByText(
				'please-contact-an-administrator-to-sync-sites-to-this-space'
			)
		).toBeInTheDocument();
	});

	it('renders not connected to analytics cloud (admin)', () => {
		renderEmptyState({
			analyticsSettingsPortletURL: '/mock-url',
			connectedToAnalyticsCloud: false,
			connectedToSpace: true,
			isAdmin: true,
		});

		expect(
			screen.getByText(/connect-to-liferay-analytics-cloud/i)
		).toBeInTheDocument();
		expect(screen.getByRole('link', {name: 'connect'})).toHaveAttribute(
			'href',
			'/mock-url'
		);
	});

	it('renders not connected to analytics cloud (non-admin)', () => {
		renderEmptyState({
			connectedToAnalyticsCloud: false,
			connectedToSpace: true,
			isAdmin: false,
		});

		expect(
			screen.getByText(
				'please-contact-a-dxp-instance-administrator-to-connect-your-dxp-instance-to-analytics-cloud'
			)
		).toBeInTheDocument();
	});

	it('renders site not synced to analytics cloud (admin)', () => {
		renderEmptyState({
			analyticsSettingsPortletURL: '/mock-url',
			connectedToAnalyticsCloud: true,
			connectedToSpace: true,
			isAdmin: true,
			siteSyncedToAnalyticsCloud: false,
		});

		expect(screen.getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(screen.getByRole('link', {name: 'sync'})).toHaveAttribute(
			'href',
			'/mock-url&currentPage=PROPERTIES'
		);
	});

	it('renders site not synced to analytics cloud (non-admin)', () => {
		renderEmptyState({
			connectedToAnalyticsCloud: true,
			connectedToSpace: true,
			isAdmin: false,
			siteSyncedToAnalyticsCloud: false,
		});

		expect(
			screen.getByText(
				'please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud'
			)
		).toBeInTheDocument();
	});
});

describe('EmptyState accessibility with admin actions', () => {
	it('shows connect to space with focusable button for admin', async () => {
		render(
			getEmptyState({
				analyticsSettingsPortletURL: '/mock-url',
				connectedToAnalyticsCloud: true,
				connectedToSpace: false,
				isAdmin: true,
				siteEditDepotEntryDepotAdminPortletURL: '/mock-url',
				siteSyncedToAnalyticsCloud: true,
			})
		);

		const heading = screen.getByText(/no-sites-are-connected-yet/i);
		const description = screen.getByText(
			/connect-sites-within-this-space/i
		);
		const button = screen.getByRole('button', {name: /connect/i});

		expect(heading).toBeInTheDocument();
		expect(description).toBeInTheDocument();

		await userEvent.tab();
		expect(button).toHaveFocus();
	});

	it('shows connect to Analytics Cloud with focusable ClayLink button for admin', async () => {
		render(
			getEmptyState({
				analyticsSettingsPortletURL: '/mock-url',
				connectedToAnalyticsCloud: false,
				connectedToSpace: true,
				isAdmin: true,
				siteEditDepotEntryDepotAdminPortletURL: '/mock-url',
				siteSyncedToAnalyticsCloud: true,
			})
		);

		const heading = screen.getByText(/connect-to-liferay-analytics-cloud/i);

		const description = screen.getByText(
			/in-order-to-view-asset-performance/i
		);
		const button = screen.getByRole('link', {name: /connect/i});

		expect(heading).toBeInTheDocument();
		expect(description).toBeInTheDocument();
		expect(button).toHaveAttribute('href', '/mock-url');

		await userEvent.tab();
		expect(button).toHaveFocus();
	});

	it('shows sync to Analytics Cloud with focusable ClayLink button for admin', async () => {
		render(
			getEmptyState({
				analyticsSettingsPortletURL: '/mock-url',
				connectedToAnalyticsCloud: true,
				connectedToSpace: true,
				isAdmin: true,
				siteEditDepotEntryDepotAdminPortletURL: '/mock-url',
				siteSyncedToAnalyticsCloud: false,
			})
		);

		const heading = screen.getByText(/sync-to-analytics-cloud/i);
		const description = screen.getByText(
			/in-order-to-view-asset-performance/i
		);
		const button = screen.getByRole('link', {name: /sync/i});

		expect(heading).toBeInTheDocument();
		expect(description).toBeInTheDocument();
		expect(button).toHaveAttribute(
			'href',
			'/mock-url&currentPage=PROPERTIES'
		);

		await userEvent.tab();
		expect(button).toHaveFocus();
	});
});
