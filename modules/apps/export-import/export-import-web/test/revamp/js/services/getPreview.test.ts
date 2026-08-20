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
			query: {
				endDate: '2026-06-26T12:00:00.000Z',
				startDate: '2026-06-26T00:00:00.000Z',
			},
			url: '/o/export-import/v1.0/sites/L_GUEST/portlets/com_liferay_document_library_web_portlet_DLAdminPortlet/export-preview?plid=1',
		});

		const url = getRequestedURL();

		expect(url.searchParams.get('endDate')).toBe(
			'2026-06-26T12:00:00.000Z'
		);
		expect(url.searchParams.get('plid')).toBe('1');
		expect(url.searchParams.get('startDate')).toBe(
			'2026-06-26T00:00:00.000Z'
		);
	});

	it('appends the date filter to a url without a query string', async () => {
		await getPreview({
			query: {endDate: '2026-06-26T12:00:00.000Z'},
			url: '/o/export-import/v1.0/sites/L_GUEST/export-preview',
		});

		const url = getRequestedURL();

		expect(url.searchParams.get('endDate')).toBe(
			'2026-06-26T12:00:00.000Z'
		);
	});

	it('keeps the url untouched when there is no date filter', async () => {
		await getPreview({
			query: {endDate: '', startDate: undefined},
			url: '/o/export-import/v1.0/sites/L_GUEST/export-preview?plid=1',
		});

		const url = getRequestedURL();

		expect(url.searchParams.has('endDate')).toBe(false);
		expect(url.searchParams.get('plid')).toBe('1');
		expect(url.searchParams.has('startDate')).toBe(false);
	});
});
