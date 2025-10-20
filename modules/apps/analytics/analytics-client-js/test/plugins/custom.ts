/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import userEvent from '@testing-library/user-event';

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {INITIAL_ANALYTICS_CONFIG} from '../helpers';

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
		it('is fired for every custom asset on the page', async () => {
			const customAssetElement = createCustomAssetElement();

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

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

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

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

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

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
