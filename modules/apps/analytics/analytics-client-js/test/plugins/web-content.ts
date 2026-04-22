/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import userEvent from '@testing-library/user-event';

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {Analytics as AnalyticsTypes} from '../../src/types';
import {INITIAL_ANALYTICS_CONFIG, wait} from '../helpers';

const applicationId = 'WebContent';

const googleUrl = 'http://google.com/';

const createWebContentElement = (assetId?: string, assetTitle?: string) => {
	const webContentElement = document.createElement('div');

	webContentElement.dataset.analyticsAssetId = assetId || 'assetId';
	webContentElement.dataset.analyticsAssetTitle =
		assetTitle || 'Web Content Title 1';
	webContentElement.dataset.analyticsAssetType = 'web-content';
	webContentElement.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(webContentElement);

	return webContentElement;
};

function createDynamicWebContentElement(attrs: any) {
	const element = document.createElement('div');

	for (let index = 0; index < Object.keys(attrs).length; index++) {
		element.dataset[Object.keys(attrs)[index]] = attrs[index];
	}

	element.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(element);

	const link = document.createElement('a');

	link.href = googleUrl;

	link.innerHTML = 'Paragraph inside a Web Content';

	element.appendChild(link);

	return [element, link];
}

describe('WebContent Plugin', () => {
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

	describe('webContentViewed event', () => {
		it('is fired when web-content is in viewport', async () => {
			const webContentElement = createWebContentElement();

			jest.spyOn(
				webContentElement,
				'getBoundingClientRect'
			).mockImplementation(
				() =>
					({
						bottom: 500,
						height: 500,
						left: 0,
						right: 500,
						top: 0,
						width: 500,
					}) as DOMRect
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			await wait(250);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'webContentViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'webContentViewed',
					properties: expect.objectContaining({
						articleId: 'assetId',
					}),
				})
			);

			document.body.removeChild(webContentElement);
		});

		it('includes assetCategories, assetVocabularies, mimeType and assetTags in the payload', async () => {
			const webContentElement = createWebContentElement(
				'assetId',
				'Web Content Title'
			);
			webContentElement.dataset.analyticsAssetCategories =
				'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]';
			webContentElement.dataset.analyticsAssetMimeType = 'text/html';
			webContentElement.dataset.analyticsAssetTags =
				'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]';
			webContentElement.dataset.analyticsAssetVocabularies =
				'[{"id":"voc1","name":"Vocabulary 1"}]';

			jest.spyOn(
				webContentElement,
				'getBoundingClientRect'
			).mockImplementation(
				() =>
					({
						bottom: 500,
						height: 500,
						left: 0,
						right: 500,
						top: 0,
						width: 500,
					}) as DOMRect
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			await wait(250);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'webContentViewed'
			);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'webContentViewed',
					properties: expect.objectContaining({
						articleId: 'assetId',
						assetCategories:
							'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]',
						assetTags:
							'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]',
						assetVocabularies:
							'[{"id":"voc1","name":"Vocabulary 1"}]',
						mimeType: 'text/html',
						title: 'Web Content Title',
					}),
				})
			);

			document.body.removeChild(webContentElement);
		});

		it('is not fired when web-content is not in viewport', async () => {
			const webContentElement = createWebContentElement();

			jest.spyOn(
				webContentElement,
				'getBoundingClientRect'
			).mockImplementation(
				() =>
					({
						bottom: 1500,
						height: 500,
						left: 0,
						right: 500,
						top: 1000,
						width: 500,
					}) as DOMRect
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			await wait(250);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'webContentViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(0);

			document.body.removeChild(webContentElement);
		});

		it('remove spaces between assetTitle and assetId', async () => {
			const webContentElement = createWebContentElement(
				' myAssetId ',
				' my asset title '
			);

			jest.spyOn(
				webContentElement,
				'getBoundingClientRect'
			).mockImplementation(
				() =>
					({
						bottom: 500,
						height: 500,
						left: 0,
						right: 500,
						top: 0,
						width: 500,
					}) as DOMRect
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			await wait(250);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'webContentViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'webContentViewed',
					properties: expect.objectContaining({
						articleId: 'myAssetId',
						title: 'my asset title',
					}),
				})
			);

			document.body.removeChild(webContentElement);
		});
	});

	describe('webContentClicked event', () => {
		it('is fired when clicking an image inside a webContent', async () => {
			const webContentElement = createWebContentElement();

			const imageInsideWebContent = document.createElement('img');

			imageInsideWebContent.src = googleUrl;

			webContentElement.appendChild(imageInsideWebContent);

			await userEvent.click(imageInsideWebContent);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'webContentClicked',
					properties: expect.objectContaining({
						articleId: 'assetId',
						src: googleUrl,
						tagName: 'img',
					}),
				}),
			]);

			document.body.removeChild(webContentElement);
		});

		it('is fired when clicking a link inside a webContent', async () => {
			const webContentElement = createWebContentElement();

			const text = 'Link inside a WebContent';

			const linkInsideWebContent = document.createElement('a');

			linkInsideWebContent.href = googleUrl;

			linkInsideWebContent.innerText = text;

			webContentElement.appendChild(linkInsideWebContent);

			await userEvent.click(linkInsideWebContent);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'webContentClicked',
					properties: expect.objectContaining({
						articleId: 'assetId',
						href: googleUrl,
						tagName: 'a',
						text,
					}),
				}),
			]);

			document.body.removeChild(webContentElement);
		});

		it('is fired when clicking any other element inside a webContent', async () => {
			const webContentElement = createWebContentElement();

			const linkInsideWebContent = document.createElement('a');

			linkInsideWebContent.href = googleUrl;

			linkInsideWebContent.innerHTML = 'Paragraph inside a WebContent';

			webContentElement.appendChild(linkInsideWebContent);

			await userEvent.click(linkInsideWebContent);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'webContentClicked',
					properties: expect.objectContaining({
						articleId: 'assetId',
						tagName: 'a',
					}),
				}),
			]);

			document.body.removeChild(webContentElement);
		});
	});

	describe('webContentClicked required attributes', () => {
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
					await createDynamicWebContentElement(attrs);

				await userEvent.click(paragraph);

				expect(Analytics.getEvents()).toEqual([]);

				document.body.removeChild(element);
			}
		);
	});

	describe('webContent events with actions', () => {
		const createWebContentElementWithAction = (
			action: AnalyticsTypes.ElementAction,
			type: AnalyticsTypes.ElementType
		) => {
			const setDataset = (
				element: AnalyticsTypes.HTMLElement,
				data: AnalyticsTypes.HTMLElement['dataset']
			) => {
				Object.entries(data).forEach(([key, value]) => {

					// @ts-ignore

					element.dataset[key] = value;
				});
			};

			const webContentElement = document.createElement(
				'div'
			) as unknown as AnalyticsTypes.HTMLElement;

			setDataset(webContentElement, {
				analyticsAssetAction: action,
				analyticsAssetId: 'assetId',
				analyticsAssetSubtype: 'basic-web-content',
				analyticsAssetTitle: 'assetTitle',
				analyticsAssetType: type,
			});

			webContentElement.innerText = `Lorem ipsum dolor, sit amet consectetur adipisicing elit.`;

			document.body.appendChild(webContentElement);

			return webContentElement;
		};

		it('is not fired when view webContent with an incorrect action value', async () => {
			const element = createWebContentElementWithAction(
				'unknown' as AnalyticsTypes.ElementAction,
				AnalyticsTypes.ElementType.WebContent
			);

			jest.spyOn(element, 'getBoundingClientRect').mockImplementation(
				() =>
					({
						bottom: 500,
						height: 500,
						left: 0,
						right: 500,
						top: 0,
						width: 500,
					}) as DOMRect
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			document.dispatchEvent(domContentLoaded);

			await wait(250);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'webContentViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(0);

			document.body.removeChild(element);
		});

		[
			AnalyticsTypes.ElementType.WebContent,
			AnalyticsTypes.ElementType.JournalArticle,
		].forEach(async (type) => {
			[
				{
					action: AnalyticsTypes.ElementAction.View,
					eventId: AnalyticsTypes.EventId.WebContentViewed,
				},
				{
					action: AnalyticsTypes.ElementAction.Impression,
					eventId: AnalyticsTypes.EventId.WebContentImpressionMade,
				},
			].forEach(
				async (props: {
					action: AnalyticsTypes.ElementAction;
					eventId: AnalyticsTypes.EventId;
				}) => {
					it(`is fired ${props.eventId} when view a web content with action ${props.action} and type: ${type}`, async () => {
						const element = createWebContentElementWithAction(
							props.action,
							type
						);

						jest.spyOn(
							element,
							'getBoundingClientRect'
						).mockImplementation(
							() =>
								({
									bottom: 500,
									height: 500,
									left: 0,
									right: 500,
									top: 0,
									width: 500,
								}) as DOMRect
						);

						const domContentLoaded = new Event('DOMContentLoaded');

						document.dispatchEvent(domContentLoaded);

						await wait(250);

						const events = Analytics.getEvents().filter(
							({eventId}) => eventId === props.eventId
						);

						expect(events.length).toBeGreaterThanOrEqual(1);

						expect(events[0]).toEqual(
							expect.objectContaining({
								applicationId,
								eventId: props.eventId,
								properties: expect.objectContaining({
									articleId: 'assetId',
									subtype: 'basic-web-content',
									title: 'assetTitle',
									type,
								}),
							})
						);

						document.body.removeChild(element);
					});
				}
			);
		});
	});
});
