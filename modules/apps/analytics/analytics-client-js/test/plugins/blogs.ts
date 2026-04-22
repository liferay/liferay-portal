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

const applicationId = 'Blog';

const googleUrl = 'http://google.com/';

const createBlogElement = (assetId?: string, assetTitle?: string) => {
	const blogElement = document.createElement('div');

	blogElement.dataset.analyticsAssetId = assetId || 'assetId';
	blogElement.dataset.analyticsAssetTitle = assetTitle || 'Blog Title 1';
	blogElement.dataset.analyticsAssetType = 'blog';
	blogElement.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(blogElement);

	return blogElement;
};

function createDynamicBlogElement(attrs: any) {
	const element = document.createElement('div');

	for (let index = 0; index < Object.keys(attrs).length; index++) {
		element.dataset[Object.keys(attrs)[index]] = attrs[index];
	}

	element.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(element);

	const link = document.createElement('a');

	link.href = googleUrl;

	link.innerText = 'Link inside a Blog';

	element.appendChild(link);

	return [element, link];
}

describe('Blogs Plugin', () => {
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

	describe('blogViewed event', () => {
		it('is fired for every blog on the page', async () => {
			const blogElement = createBlogElement();

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'blogViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'blogViewed',
					properties: expect.objectContaining({
						entryId: 'assetId',
					}),
				})
			);

			document.body.removeChild(blogElement);
		});

		it('remove spaces between assetTitle and assetId', async () => {
			const blogElement = createBlogElement(
				' myAssetId ',
				' my asset title '
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'blogViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'blogViewed',
					properties: expect.objectContaining({
						entryId: 'myAssetId',
						title: 'my asset title',
					}),
				})
			);

			document.body.removeChild(blogElement);
		});

		it('includes assetCategories, assetVocabularies, mimeType and assetTags in the payload', async () => {
			const blogElement = createBlogElement('assetId', 'Blog Title');
			blogElement.dataset.analyticsAssetCategories =
				'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]';
			blogElement.dataset.analyticsAssetMimeType = 'text/html';
			blogElement.dataset.analyticsAssetTags =
				'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]';
			blogElement.dataset.analyticsAssetVocabularies =
				'[{"id":"voc1","name":"Vocabulary 1"}]';

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'blogViewed'
			);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: 'blogViewed',
					properties: expect.objectContaining({
						assetCategories:
							'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]',
						assetTags:
							'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]',
						assetVocabularies:
							'[{"id":"voc1","name":"Vocabulary 1"}]',
						entryId: 'assetId',
						mimeType: 'text/html',
						title: 'Blog Title',
					}),
				})
			);

			document.body.removeChild(blogElement);
		});
	});

	describe('blogClicked event', () => {
		it('is fired when clicking an image inside a blog', async () => {
			const blogElement = createBlogElement();

			const imageInsideBlog = document.createElement('img');

			imageInsideBlog.src = googleUrl;

			blogElement.appendChild(imageInsideBlog);

			await userEvent.click(imageInsideBlog);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'blogClicked',
					properties: expect.objectContaining({
						entryId: 'assetId',
						src: googleUrl,
						tagName: 'img',
					}),
				}),
			]);

			document.body.removeChild(blogElement);
		});

		it('is fired when clicking a link inside a blog', async () => {
			const blogElement = createBlogElement();

			const text = 'Link inside a Blog';

			const linkInsideBlog = document.createElement('a');

			linkInsideBlog.href = googleUrl;

			linkInsideBlog.innerText = text;

			blogElement.appendChild(linkInsideBlog);

			await userEvent.click(linkInsideBlog);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'blogClicked',
					properties: expect.objectContaining({
						entryId: 'assetId',
						href: googleUrl,
						tagName: 'a',
						text,
					}),
				}),
			]);

			document.body.removeChild(blogElement);
		});

		it('is fired when clicking any other element inside a blog', async () => {
			const blogElement = createBlogElement();

			const linkInsideBlog = document.createElement('a');

			linkInsideBlog.href = googleUrl;

			linkInsideBlog.innerText = 'Link inside a Blog';

			blogElement.appendChild(linkInsideBlog);

			await userEvent.click(linkInsideBlog);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'blogClicked',
					properties: expect.objectContaining({
						entryId: 'assetId',
						tagName: 'a',
					}),
				}),
			]);

			document.body.removeChild(blogElement);
		});
	});

	describe('blogClicked required attributes', () => {
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
					await createDynamicBlogElement(attrs);

				await userEvent.click(paragraph);

				expect(Analytics.getEvents()).toEqual([]);

				document.body.removeChild(element);
			}
		);
	});

	describe('blog events with actions', () => {
		const createBlogElementWithAction = (
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

			const blogElement = document.createElement(
				'div'
			) as unknown as AnalyticsTypes.HTMLElement;

			setDataset(blogElement, {
				analyticsAssetAction: action,
				analyticsAssetId: 'assetId',
				analyticsAssetSubtype: 'basic-blog',
				analyticsAssetTitle: 'assetTitle',
				analyticsAssetType: type,
			});

			blogElement.innerText = `Lorem ipsum dolor, sit amet consectetur adipisicing elit.`;

			document.body.appendChild(blogElement);

			return blogElement;
		};

		it('is not fired when view blog with an incorrect action value', async () => {
			const element = createBlogElementWithAction(
				'unknown' as AnalyticsTypes.ElementAction,
				AnalyticsTypes.ElementType.Blog
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
				({eventId}) => eventId === 'blogViewed'
			);

			expect(events.length).toBeGreaterThanOrEqual(0);

			document.body.removeChild(element);
		});

		[
			AnalyticsTypes.ElementType.Blog,
			AnalyticsTypes.ElementType.BlogsEntry,
		].forEach(async (type) => {
			[
				{
					action: AnalyticsTypes.ElementAction.View,
					eventId: AnalyticsTypes.EventId.BlogViewed,
				},
				{
					action: AnalyticsTypes.ElementAction.Impression,
					eventId: AnalyticsTypes.EventId.BlogImpressionMade,
				},
			].forEach(async (props) => {
				it(`is fired ${props.eventId} when view a blog with action ${props.action} and type: ${type}`, async () => {
					const element = createBlogElementWithAction(
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
								entryId: 'assetId',
								numberOfWords: 8,
								subtype: 'basic-blog',
								title: 'assetTitle',
								type,
							}),
						})
					);

					document.body.removeChild(element);
				});
			});
		});
	});
});
