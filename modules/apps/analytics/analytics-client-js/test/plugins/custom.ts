/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import userEvent from '@testing-library/user-event';

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {DEBOUNCE} from '../../src/utils/constants';
import {INITIAL_ANALYTICS_CONFIG, mockVisibleRect, wait} from '../helpers';

const applicationId = 'Custom';

const googleUrl = 'http://google.com/';

const createCustomAssetElement = (assetId?: string, asseTitle?: string) => {
	const customAssetElement = document.createElement('div');

	customAssetElement.dataset.analyticsAssetCategory = 'custom-asset-category';
	customAssetElement.dataset.analyticsAssetId = assetId || 'assetId';
	customAssetElement.dataset.analyticsAssetTitle =
		asseTitle || 'Custom Asset Title 1';
	customAssetElement.dataset.analyticsAssetType = 'custom';
	customAssetElement.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(customAssetElement);

	return customAssetElement;
};

const createCustomAssetElementWithForm = () => {
	const customAssetElement = document.createElement('div');

	customAssetElement.dataset.analyticsAssetCategory = 'custom-asset-category';
	customAssetElement.dataset.analyticsAssetId = 'assetId';
	customAssetElement.dataset.analyticsAssetTitle = 'Custom Asset Title 1';
	customAssetElement.dataset.analyticsAssetType = 'custom';

	customAssetElement.innerHTML =
		'<form><input type="text" /><button type="submit" /></form>';

	document.body.appendChild(customAssetElement);

	return customAssetElement;
};

const createDynamicCustomAssetElement = (attrs: any) => {
	const element = document.createElement('div');

	element.dataset.analyticsAssetCategory = 'custom-asset-category';

	for (let index = 0; index < Object.keys(attrs).length; index++) {
		element.dataset[Object.keys(attrs)[index]] = attrs[index];
	}

	document.body.appendChild(element);

	const link = document.createElement('a');

	link.href = googleUrl;

	link.innerText = 'Link inside a Custom Asset';

	element.appendChild(link);

	return [element, link];
};

describe('Custom Asset Plugin', () => {
	let Analytics: AnalyticsClient;

	beforeEach(() => {

		// Force attaching DOM Content Loaded event

		Object.defineProperty(document, 'readyState', {
			value: 'loading',
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

	describe('assetViewed event', () => {
		it('is fired for every visible custom asset on the page', async () => {
			const customAssetElement = createCustomAssetElement();

			mockVisibleRect(customAssetElement);

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetViewed',
					properties: expect.objectContaining({
						assetId: 'assetId',
					}),
				})
			);

			document.body.removeChild(customAssetElement);
		});

		it('remove spaces between assetTitle and assetId', async () => {
			const customAssetElement = createCustomAssetElement(
				' myAssetId ',
				' my asset title '
			);

			mockVisibleRect(customAssetElement);

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetViewed',
					properties: expect.objectContaining({
						assetId: 'myAssetId',
						title: 'my asset title',
					}),
				})
			);

			document.body.removeChild(customAssetElement);
		});

		it('is fired with formEnabled if there is form element every custom asset on the page', async () => {
			const customAssetElement = createCustomAssetElementWithForm();

			mockVisibleRect(customAssetElement);

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetViewed',
					properties: expect.objectContaining({
						assetId: 'assetId',
						formEnabled: true,
					}),
				})
			);

			document.body.removeChild(customAssetElement);
		});

		it('includes assetCategories, assetVocabularies, mimeType and assetTags in the payload', async () => {
			const customAssetElement = createCustomAssetElement(
				'assetId',
				'Custom Asset Title'
			);
			customAssetElement.dataset.analyticsAssetCategories =
				'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]';
			customAssetElement.dataset.analyticsAssetMimeType = 'text/html';
			customAssetElement.dataset.analyticsAssetTags =
				'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]';
			customAssetElement.dataset.analyticsAssetVocabularies =
				'[{"id":"voc1","name":"Vocabulary 1"}]';

			mockVisibleRect(customAssetElement);

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetViewed'
			);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetViewed',
					properties: expect.objectContaining({
						assetCategories:
							'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]',
						assetId: 'assetId',
						assetTags:
							'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]',
						assetVocabularies:
							'[{"id":"voc1","name":"Vocabulary 1"}]',
						mimeType: 'text/html',
						title: 'Custom Asset Title',
					}),
				})
			);

			document.body.removeChild(customAssetElement);
		});

		it('is not fired when a custom asset is in the viewport but hidden by CSS', async () => {
			const customAssetElement = createCustomAssetElement();

			customAssetElement.style.visibility = 'hidden';

			mockVisibleRect(customAssetElement);

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetViewed'
			);

			expect(events.length).toBe(0);

			document.body.removeChild(customAssetElement);
		});

		it('is fired when a hidden custom asset becomes visible after a reveal event', async () => {
			const customAssetElement = createCustomAssetElement();

			customAssetElement.style.visibility = 'hidden';

			mockVisibleRect(customAssetElement);

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(300);

			expect(
				Analytics.getEvents().filter(
					({eventId}) => eventId === 'assetViewed'
				).length
			).toBe(0);

			customAssetElement.style.visibility = 'visible';

			customAssetElement.dispatchEvent(
				new Event('click', {bubbles: true})
			);

			await wait(300);

			expect(
				Analytics.getEvents().filter(
					({eventId}) => eventId === 'assetViewed'
				).length
			).toBe(1);

			document.body.removeChild(customAssetElement);
		});
	});

	describe('assetDepthReached event', () => {
		beforeEach(() => {

			// Recreate with a flush interval large enough that the queue is not
			// drained before the debounced scroll depth event is asserted.

			AnalyticsClient.dispose();

			Analytics = AnalyticsClient.create({
				...INITIAL_ANALYTICS_CONFIG,
				flushInterval: 60000,
			});
		});

		it('is fired on scroll after a viewed custom asset reaches a depth level', async () => {
			const customAssetElement = createCustomAssetElement();

			mockVisibleRect(customAssetElement);

			// The element must be viewed first so it is tracked for scrolling

			document.dispatchEvent(new Event('DOMContentLoaded'));

			await wait(100);

			document.dispatchEvent(new Event('scroll'));

			await wait(DEBOUNCE + 200);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetDepthReached'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetDepthReached',
					properties: expect.objectContaining({
						assetId: 'assetId',
						depth: expect.any(Number),
					}),
				})
			);

			expect(events[0].properties.depth).toBeGreaterThan(0);

			document.body.removeChild(customAssetElement);
		});
	});

	describe('assetSubmitted event', () => {
		it('is fired when a form inside a custom asset is submitted', async () => {
			const customAssetElement = createCustomAssetElementWithForm();

			const form = customAssetElement.querySelector(
				'form'
			) as HTMLFormElement;

			form.dispatchEvent(new Event('submit', {bubbles: true}));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetSubmitted'
			);

			expect(events.length).toBe(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetSubmitted',
					properties: expect.objectContaining({
						assetId: 'assetId',
					}),
				})
			);

			document.body.removeChild(customAssetElement);
		});

		it('is not fired when the submit event is defaultPrevented', async () => {
			const customAssetElement = createCustomAssetElementWithForm();

			const form = customAssetElement.querySelector(
				'form'
			) as HTMLFormElement;

			form.addEventListener('submit', (event) => event.preventDefault());

			form.dispatchEvent(
				new Event('submit', {bubbles: true, cancelable: true})
			);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'assetSubmitted'
			);

			expect(events.length).toBe(0);

			document.body.removeChild(customAssetElement);
		});
	});

	describe('assetClicked event', () => {
		it('is fired when clicking an image inside a custom asset', async () => {
			const customAssetElement = createCustomAssetElement();

			const imageInsideCustomAsset = document.createElement('img');

			imageInsideCustomAsset.src = googleUrl;

			customAssetElement.appendChild(imageInsideCustomAsset);

			await userEvent.click(imageInsideCustomAsset);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'assetClicked',
					properties: expect.objectContaining({
						assetId: 'assetId',
						src: googleUrl,
						tagName: 'img',
					}),
				}),
			]);

			document.body.removeChild(customAssetElement);
		});

		it('is fired when clicking a link inside a custom asset', async () => {
			const customAssetElement = createCustomAssetElement();

			const text = 'Link inside a Custom Asset';

			const linkInsideCustomAsset = document.createElement('a');

			linkInsideCustomAsset.href = googleUrl;

			linkInsideCustomAsset.innerText = text;

			customAssetElement.appendChild(linkInsideCustomAsset);

			await userEvent.click(linkInsideCustomAsset);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'assetClicked',
					properties: expect.objectContaining({
						assetId: 'assetId',
						href: googleUrl,
						tagName: 'a',
						text,
					}),
				}),
			]);

			document.body.removeChild(customAssetElement);
		});

		it('is fired when clicking any other element inside a custom asset', async () => {
			const customAssetElement = createCustomAssetElement();

			const linkInsideCustomAsset = document.createElement('a');

			linkInsideCustomAsset.href = googleUrl;

			linkInsideCustomAsset.innerText = 'Link inside a Custom Asset';

			customAssetElement.appendChild(linkInsideCustomAsset);

			await userEvent.click(linkInsideCustomAsset);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'assetClicked',
					properties: expect.objectContaining({
						assetId: 'assetId',
						tagName: 'a',
					}),
				}),
			]);

			document.body.removeChild(customAssetElement);
		});
	});

	describe('assetDownloaded', () => {
		it('is fired when clicking a link inside a custom asset', async () => {
			const customAssetElement = createCustomAssetElement();

			const text = 'Link inside a Custom Asset';

			const linkInsideCustomAsset = document.createElement('a');

			linkInsideCustomAsset.href = '#';

			linkInsideCustomAsset.innerText = text;

			linkInsideCustomAsset.setAttribute(
				'data-analytics-asset-action',
				'download'
			);

			customAssetElement.appendChild(linkInsideCustomAsset);

			await userEvent.click(linkInsideCustomAsset);

			expect(Analytics.getEvents().length).toEqual(2);

			expect(Analytics.getEvents()[1]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'assetDownloaded',
				})
			);

			document.body.removeChild(customAssetElement);
		});
	});

	describe('assetClicked required attributes', () => {
		it.each([
			[
				'assetId',
				{
					analyticsAssetTitle: 'assetTitle',
					analyticsAssetType: 'blog',
				},
			],
			[
				'assetTitle',
				{
					analyticsAssetId: 'assetId',
					analyticsAssetType: 'blog',
				},
			],
			[
				'assetType',
				{
					analyticsAssetId: 'assetId',
					analyticsAssetType: 'assetTitle',
				},
			],
		])(
			'is not fired if asset missing %s attribute',
			async (label, attrs) => {
				const [element, paragraph] =
					await createDynamicCustomAssetElement(attrs);

				await userEvent.click(paragraph);

				expect(Analytics.getEvents()).toEqual([]);

				document.body.removeChild(element);
			}
		);
	});
});
