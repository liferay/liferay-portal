/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	render,
	screen,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../js/apis/ApiHelper';
import {CheckPermissions} from '../../../js/components/cms/CheckPermissions';

jest.mock('frontend-js-web', () => ({
	createRenderURL: jest.fn(() => ({
		href: 'mocked-url',
	})),
}));

describe('CheckPermissions', () => {
	it('renders performance tab', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: true,
				connectedToAnalyticsCloud: true,
				connectedToSpace: true,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByText('tab rendered')).toBeInTheDocument();
	});

	it('renders empty state if there is no space connected to the site (admin)', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: true,
				connectedToAnalyticsCloud: true,
				connectedToSpace: false,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText('connect-sites-to-show-data')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'no-sites-connected-yet.-connect-spaces-to-sites-to-see-how-assets-perform'
			)
		).toBeInTheDocument();
		expect(
			screen.getByRole('button', {name: 'connect-sites'})
		).toBeInTheDocument();
	});

	it('renders empty state if there is no space connected to the site (non-admin)', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: false,
				connectedToAnalyticsCloud: true,
				connectedToSpace: false,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText('connect-sites-to-show-data')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'no-sites-connected-yet.-connect-spaces-to-sites-to-see-how-assets-perform'
			)
		).toBeInTheDocument();
		expect(
			screen.queryByRole('button', {name: 'connect-sites'})
		).not.toBeInTheDocument();
	});

	it('renders the connect to Analytics Cloud state before the connect sites state when neither is connected', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: true,
				connectedToAnalyticsCloud: false,
				connectedToSpace: false,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText('see-how-your-content-performs')
		).toBeInTheDocument();
		expect(
			screen.queryByText('connect-sites-to-show-data')
		).not.toBeInTheDocument();
	});

	it('renders empty state if there is no site connected to the Analytics Cloud (admin)', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: true,
				connectedToAnalyticsCloud: false,
				connectedToSpace: true,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText('see-how-your-content-performs')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'track-views,-engagement,-and-where-your-assets-are-used-across-your-sites-with-liferay-data-platform'
			)
		).toBeInTheDocument();
		expect(
			screen.getByText('learn-more-about-performance-capabilities')
		).toBeInTheDocument();
		expect(
			screen.getByText('connect-your-liferay-data-platform')
		).toBeInTheDocument();
	});

	it('renders empty state if there is no site connected to the Analytics Cloud (non-admin)', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: false,
				connectedToAnalyticsCloud: false,
				connectedToSpace: true,
				siteSyncedToAnalyticsCloud: true,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText('see-how-your-content-performs')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'track-views,-engagement,-and-where-your-assets-are-used-across-your-sites-with-liferay-data-platform'
			)
		).toBeInTheDocument();
		expect(
			screen.getByText('learn-more-about-performance-capabilities')
		).toBeInTheDocument();
		expect(
			screen.queryByText('connect-your-liferay-data-platform')
		).not.toBeInTheDocument();
	});

	it('renders empty state if there is no site synced the Analytics Cloud (admin)', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: true,
				connectedToAnalyticsCloud: true,
				connectedToSpace: true,
				siteSyncedToAnalyticsCloud: false,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(
			screen.getByText(
				'in-order-to-view-asset-performance,-your-sites-have-to-be-synced-to-liferay-analytics-cloud'
			)
		).toBeInTheDocument();
		expect(screen.getByText('sync')).toBeInTheDocument();
	});

	it('renders empty state if there is no site synced the Analytics Cloud (non-admin)', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				admin: false,
				connectedToAnalyticsCloud: true,
				connectedToSpace: true,
				siteSyncedToAnalyticsCloud: false,
			},
			error: null,
		});

		render(<CheckPermissions scopeId="123">tab rendered</CheckPermissions>);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(screen.getByText('sync-to-analytics-cloud')).toBeInTheDocument();
		expect(
			screen.getByText(
				'please-contact-a-dxp-instance-administrator-to-sync-your-sites-to-analytics-cloud'
			)
		).toBeInTheDocument();
		expect(screen.queryByText('sync')).not.toBeInTheDocument();
	});
});
