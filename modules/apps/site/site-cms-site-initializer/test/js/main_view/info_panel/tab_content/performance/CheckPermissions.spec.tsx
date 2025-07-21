/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen} from '@testing-library/react';
import React from 'react';

import {EmptyStates} from '../../../../../../src/main/resources/META-INF/resources/js/main_view/info_panel/tab_content/performance/CheckPermissions';

jest.mock('frontend-js-web', () => ({
	createRenderURL: jest.fn(() => ({
		href: '/mock-url',
	})),
}));

/**
 * TODO: Once request to get permissions is done, use
 * CheckPermissions component instead of EmptyStates.
 * 
 * it('renders performance tab', async () => {
	global.fetch = jest.fn().mockResolvedValue({
		connectedToAnalyticsCloud: true,
		connectedToSpace: true,
		isAdmin: true,
		siteSyncedToAnalyticsCloud: true,
	});

	render(<CheckPermissions>tab rendered</CheckPermissions>);

	await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

	expect(screen.getByText('tab rendered')).toBeInTheDocument();
	});
 */

describe('CheckPermissions', () => {
	it('renders performance tab', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud
				connectedToSpace
				isAdmin
				siteSyncedToAnalyticsCloud
			>
				tab rendered
			</EmptyStates>
		);

		expect(screen.getByText('tab rendered')).toBeInTheDocument();
	});

	it('renders empty state if there is no space connected to the site (admin)', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud
				connectedToSpace={false}
				isAdmin
				siteSyncedToAnalyticsCloud
			>
				tab rendered
			</EmptyStates>
		);

		expect(
			screen.getByText('no-sites-are-connected-yet')
		).toBeInTheDocument();
		expect(
			screen.getByText('connect-sites-within-this-space')
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'connect'})
		).toBeInTheDocument();
	});

	it('renders empty state if there is no space connected to the site (non-admin)', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud
				connectedToSpace={false}
				isAdmin={false}
				siteSyncedToAnalyticsCloud
			>
				tab rendered
			</EmptyStates>
		);

		expect(
			screen.getByText('no-sites-are-connected-yet')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'please-contact-an-administrator-to-sync-sites-to-this-space'
			)
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'connect'})
		).not.toBeInTheDocument();
	});

	it('renders empty state if there is no site connected to the Analytics Cloud (admin)', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud={false}
				connectedToSpace={true}
				isAdmin
				siteSyncedToAnalyticsCloud
			>
				tab rendered
			</EmptyStates>
		);

		expect(
			screen.getByText('connect-to-liferay-analytics-cloud')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'in-order-to-view-asset-performance,-your-liferay-dxp-instance-has-to-be-connected-with-liferay-analytics-cloud'
			)
		).toBeInTheDocument();
		expect(screen.getByText('connect')).toBeInTheDocument();
	});

	it('renders empty state if there is no site connected to the Analytics Cloud (non-admin)', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud={false}
				connectedToSpace
				isAdmin={false}
				siteSyncedToAnalyticsCloud
			>
				tab rendered
			</EmptyStates>
		);

		expect(
			screen.getByText('connect-to-liferay-analytics-cloud')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'please-contact-a-dxp-instance-administrator-to-connect-your-dxp-instance-to-analytics-cloud'
			)
		).toBeInTheDocument();
		expect(screen.queryByText('connect')).not.toBeInTheDocument();
	});

	it('renders empty state if there is no site synced the Analytics Cloud (admin)', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud
				connectedToSpace
				isAdmin
				siteSyncedToAnalyticsCloud={false}
			>
				tab rendered
			</EmptyStates>
		);

		expect(screen.getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(
			screen.getByText(
				'in-order-to-view-asset-performance,-your-sites-have-to-be-synced-to-liferay-analytics-cloud'
			)
		).toBeInTheDocument();
		expect(screen.getByText('sync')).toBeInTheDocument();
	});

	it('renders empty state if there is no site synced the Analytics Cloud (non-admin)', async () => {
		render(
			<EmptyStates
				connectedToAnalyticsCloud
				connectedToSpace
				isAdmin={false}
				siteSyncedToAnalyticsCloud={false}
			>
				tab rendered
			</EmptyStates>
		);

		expect(screen.getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(
			screen.getByText(
				'please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud'
			)
		).toBeInTheDocument();
		expect(screen.queryByText('sync')).not.toBeInTheDocument();
	});
});
