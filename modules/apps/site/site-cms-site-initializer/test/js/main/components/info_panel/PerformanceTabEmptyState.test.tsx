/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import { getEmptyState } from '../../../../../src/main/resources/META-INF/resources/js/main/components/info_panel/EmptyState';


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

		expect(screen.getByText('no-sites-are-connected-yet')).toBeInTheDocument();
		expect(screen.getByText('connect')).toBeInTheDocument();
	});

	it('renders site not connected (non-admin)', () => {
		renderEmptyState({
			connectedToSpace: false,
			isAdmin: false,
		});

		expect(
			screen.getByText('please-contact-an-administrator-to-sync-sites-to-this-space')
		).toBeInTheDocument();
	});

	it('renders not connected to analytics cloud (admin)', () => {
		renderEmptyState({
			connectedToSpace: true,
			connectedToAnalyticsCloud: false,
			isAdmin: true,
			analyticsSettingsPortletURL: '/mock-url',
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
			connectedToSpace: true,
			connectedToAnalyticsCloud: false,
			isAdmin: false,
		});

		expect(
			screen.getByText('please-contact-a-dxp-instance-administrator-to-connect-your-dxp-instance-to-analytics-cloud')
		).toBeInTheDocument();
	});

	it('renders site not synced to analytics cloud (admin)', () => {
		renderEmptyState({
			connectedToSpace: true,
			connectedToAnalyticsCloud: true,
			siteSyncedToAnalyticsCloud: false,
			isAdmin: true,
			analyticsSettingsPortletURL: '/mock-url',
		});

		expect(screen.getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(screen.getByRole('link', {name: 'sync'})).toHaveAttribute(
			'href',
			'/mock-url&currentPage=PROPERTIES'
		);
	});

	it('renders site not synced to analytics cloud (non-admin)', () => {
		renderEmptyState({
			connectedToSpace: true,
			connectedToAnalyticsCloud: true,
			siteSyncedToAnalyticsCloud: false,
			isAdmin: false,
		});

		expect(
			screen.getByText('please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud')
		).toBeInTheDocument();
	});

	it('returns null when all conditions are satisfied', () => {
		const result = getEmptyState({
            connectedToSpace: true,
            connectedToAnalyticsCloud: true,
            siteSyncedToAnalyticsCloud: true,
            isAdmin: true,
            analyticsSettingsPortletURL: '/mock-url',
            siteEditDepotEntryDepotAdminPortletURL: '/mock-url'
        });

		expect(result).toBeNull();
	});
});
