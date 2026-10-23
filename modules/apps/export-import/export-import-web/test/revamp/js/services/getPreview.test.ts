/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import fetch from 'jest-fetch-mock';

import {getPreview} from '../../../../src/main/resources/META-INF/resources/revamp/js/services/getPreview';

const getRequestedURL = () =>
	new URL(String(fetch.mock.calls[0][0]), 'http://localhost');

describe('getPreview', () => {
	beforeEach(() => {
		fetch.resetMocks();
		fetch.mockResponse(JSON.stringify({}));
	});

	it('appends the date filter to a scoped url that already has a query string', async () => {
		await getPreview({
			dateFilter: {
				endDate: '2026-06-26T12:00:00.000Z',
				startDate: '2026-06-26T00:00:00.000Z',
				type: 'DateRange',
			},
			url: '/o/export-import/v1.0/sites/L_GUEST/export-preview?portletId=com_liferay_document_library_web_portlet_DLPortlet&sitePageExternalReferenceCode=home',
		});

		const url = getRequestedURL();

		expect(url.searchParams.get('dateFilterType')).toBe('DateRange');
		expect(url.searchParams.get('endDate')).toBe(
			'2026-06-26T12:00:00.000Z'
		);
		expect(url.searchParams.get('portletId')).toBe(
			'com_liferay_document_library_web_portlet_DLPortlet'
		);
		expect(url.searchParams.get('sitePageExternalReferenceCode')).toBe(
			'home'
		);
		expect(url.searchParams.get('startDate')).toBe(
			'2026-06-26T00:00:00.000Z'
		);
	});

	it('appends the date filter to a url without a query string', async () => {
		await getPreview({
			dateFilter: {hours: 12, type: 'LastHours'},
			url: '/o/export-import/v1.0/sites/L_GUEST/export-preview',
		});

		const url = getRequestedURL();

		expect(url.searchParams.get('dateFilterType')).toBe('LastHours');
		expect(url.searchParams.get('hours')).toBe('12');
	});

	it('keeps the url untouched when there is no date filter', async () => {
		await getPreview({
			url: '/o/export-import/v1.0/sites/L_GUEST/export-preview?portletId=com_liferay_document_library_web_portlet_DLAdminPortlet',
		});

		const url = getRequestedURL();

		expect(url.searchParams.has('dateFilterType')).toBe(false);
		expect(url.searchParams.has('endDate')).toBe(false);
		expect(url.searchParams.get('portletId')).toBe(
			'com_liferay_document_library_web_portlet_DLAdminPortlet'
		);
		expect(url.searchParams.has('startDate')).toBe(false);
	});
});
