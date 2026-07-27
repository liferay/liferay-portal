/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {Analytics as AnalyticsTypes} from '../../src/types';
import {INITIAL_ANALYTICS_CONFIG, mockVisibleRect, wait} from '../helpers';

const createPreviewElement = () => {
	const element = document.createElement('div');

	element.dataset.analyticsAssetAction = 'preview';
	element.dataset.analyticsAssetId = 'myDocumentId';
	element.dataset.analyticsAssetTitle = 'my document title';
	element.dataset.analyticsAssetType = AnalyticsTypes.ElementType.FileEntry;
	element.innerText = 'this is a document preview';

	document.body.appendChild(element);

	mockVisibleRect(element);

	return element;
};

describe('Documents Plugin (preview)', () => {
	let Analytics: AnalyticsClient;

	beforeEach(() => {

		// Force attaching DOM Content Loaded event

		Object.defineProperty(document, 'readyState', {
			writable: false,
		});

		fetchMock.mock('*', () => 200);

		Analytics = AnalyticsClient.create(INITIAL_ANALYTICS_CONFIG);
	});

	afterEach(() => {
		Analytics.reset();
		AnalyticsClient.dispose();

		fetchMock.restore();
	});

	describe('documentPreviewed event', () => {
		it('is fired when a visible previewed document is on the page', async () => {
			const documentsElement = createPreviewElement();

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'documentPreviewed'
			);

			expect(events.length).toBe(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentPreviewed',
					properties: expect.objectContaining({
						fileEntryId: 'myDocumentId',
						title: 'my document title',
					}),
				})
			);

			document.body.removeChild(documentsElement);
		});

		it('is not fired when the previewed document is in the viewport but hidden by CSS', async () => {
			const documentsElement = createPreviewElement();

			documentsElement.style.visibility = 'hidden';

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'documentPreviewed'
			);

			expect(events.length).toBe(0);

			document.body.removeChild(documentsElement);
		});

		it('is fired when a hidden previewed document becomes visible after a reveal event', async () => {
			const documentsElement = createPreviewElement();

			documentsElement.style.visibility = 'hidden';

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			expect(
				Analytics.getEvents().filter(
					({eventId}) => eventId === 'documentPreviewed'
				).length
			).toBe(0);

			documentsElement.style.visibility = 'visible';

			documentsElement.dispatchEvent(new Event('click', {bubbles: true}));

			await wait(300);

			expect(
				Analytics.getEvents().filter(
					({eventId}) => eventId === 'documentPreviewed'
				).length
			).toBe(1);

			document.body.removeChild(documentsElement);
		});
	});
});
