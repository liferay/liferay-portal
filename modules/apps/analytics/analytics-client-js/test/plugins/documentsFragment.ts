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

const createElement = (tmpl: string) =>
	new DOMParser().parseFromString(tmpl, 'text/html').body.firstChild;

const createElementTitle = () => {
	const node = createElement(`
		<div data-analytics-asset-id="myDocumentId" data-analytics-asset-title="my document title" data-analytics-asset-type="${AnalyticsTypes.ElementType.FileEntry}" data-analytics-asset-action="impression">
			this is a title
		</div>
	`) as AnalyticsTypes.HTMLElement;

	document.body.appendChild(node);

	return node;
};

const createFragmentWithLink = () => {
	const node = createElement(`
		<a data-analytics-asset-id="myDocumentId" data-analytics-asset-title="my document with link" data-analytics-asset-type="${AnalyticsTypes.ElementType.FileEntry}" data-analytics-asset-action="download" href="#">
			this is a link
		</a>
	`) as AnalyticsTypes.HTMLElement;

	document.body.appendChild(node);

	return node;
};

const createHeadingFragmentWithLink = () => {
	const node = createElement(`
		<div data-analytics-asset-id="myDocumentId" data-analytics-asset-title="my document with link" data-analytics-asset-type="${AnalyticsTypes.ElementType.FileEntry}" data-analytics-asset-action="download">
			<a href="#">
				this is a link
			</a>
		</div>
	`) as AnalyticsTypes.HTMLElement;

	document.body.appendChild(node);

	return node;
};

function createDynamicDocumentsElement(attrs: any) {
	const documentElement = document.createElement('div');

	for (let index = 0; index < Object.keys(attrs).length; index++) {
		documentElement.dataset[Object.keys(attrs)[index]] = attrs[index];
	}

	const linkElement = document.createElement('a');

	linkElement.dataset.analyticsAssetAction = 'download';
	linkElement.dataset.href = '#';
	linkElement.innerText = 'download document';

	documentElement.appendChild(linkElement);
	document.body.appendChild(documentElement);

	return [documentElement, linkElement];
}

describe('Documents Plugin', () => {
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

	describe('documentImpressionMade event', () => {
		it('is fired when there is a document with a title on the page', async () => {
			const documentsElement = createElementTitle();

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'documentImpressionMade'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentImpressionMade',
					properties: {
						fileEntryId: 'myDocumentId',
						title: 'my document title',
						type: AnalyticsTypes.ElementType.FileEntry,
					},
				})
			);

			document.body.removeChild(documentsElement);
		});

		it('is fired when there is a document with a link on the page', async () => {
			const documentsElement = createFragmentWithLink();

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'documentImpressionMade'
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentImpressionMade',
					properties: {
						fileEntryId: 'myDocumentId',
						title: 'my document with link',
						type: AnalyticsTypes.ElementType.FileEntry,
					},
				})
			);

			document.body.removeChild(documentsElement);
		});

		it('includes assetCategories, assetVocabularies, mimeType and assetTags in the payload', async () => {
			const documentsElement = createElementTitle();
			documentsElement.dataset.analyticsAssetCategories =
				'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]';
			documentsElement.dataset.analyticsAssetMimeType = 'application/pdf';
			documentsElement.dataset.analyticsAssetTags =
				'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]';
			documentsElement.dataset.analyticsAssetVocabularies =
				'[{"id":"voc1","name":"Vocabulary 1"}]';

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'documentImpressionMade'
			);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentImpressionMade',
					properties: expect.objectContaining({
						assetCategories:
							'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]',
						assetTags:
							'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]',
						assetVocabularies:
							'[{"id":"voc1","name":"Vocabulary 1"}]',
						fileEntryId: 'myDocumentId',
						mimeType: 'application/pdf',
						title: 'my document title',
					}),
				})
			);

			document.body.removeChild(documentsElement);
		});
	});

	describe('documentDownloaded event', () => {
		it('is fired when clicking in a fragment with a link', async () => {
			const documentsElement = createFragmentWithLink();

			await userEvent.click(documentsElement);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentDownloaded',
					properties: expect.objectContaining({
						fileEntryId: 'myDocumentId',
						title: 'my document with link',
						type: AnalyticsTypes.ElementType.FileEntry,
					}),
				}),
			]);

			document.body.removeChild(documentsElement);
		});

		it('is fired when clicking in a heading fragment with a link', async () => {
			const documentsElement = createHeadingFragmentWithLink();

			await userEvent.click(documentsElement);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId: 'Document',
					eventId: 'documentDownloaded',
					properties: expect.objectContaining({
						fileEntryId: 'myDocumentId',
						title: 'my document with link',
						type: AnalyticsTypes.ElementType.FileEntry,
					}),
				}),
			]);

			document.body.removeChild(documentsElement);
		});
	});

	describe('documentDownloaded required attributes', () => {
		it.each([
			[
				'assetId',
				{
					analyticsAssetTitle: 'assetTitle',
					analyticsAssetType: AnalyticsTypes.ElementType.FileEntry,
				},
			],
			[
				'assetTitle',
				{
					analyticsAssetId: 'assetId',
					analyticsAssetType: AnalyticsTypes.ElementType.FileEntry,
				},
			],
			[
				'assetType',
				{
					analyticsAssetId: 'assetId',
					analyticsAssetType: AnalyticsTypes.ElementType.FileEntry,
				},
			],
		])(
			'is not fired if asset missing %s attribute',
			async (label, attrs) => {
				const [element, link] =
					await createDynamicDocumentsElement(attrs);

				await userEvent.click(link);

				expect(Analytics.getEvents()).toEqual([]);

				document.body.removeChild(element);
			}
		);
	});

	describe('documents events with actions', () => {
		const createDocumentsElementWithAction = (
			action: AnalyticsTypes.ElementAction
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

			const documentElement = document.createElement(
				'div'
			) as unknown as AnalyticsTypes.HTMLElement;

			setDataset(documentElement, {
				analyticsAssetAction: action,
				analyticsAssetId: 'assetId',
				analyticsAssetSubtype: 'basic-document',
				analyticsAssetTitle: 'assetTitle',
				analyticsAssetType: AnalyticsTypes.ElementType.FileEntry,
			});

			const linkElement = document.createElement('a');

			linkElement.dataset.analyticsAssetAction = 'download';
			linkElement.dataset.href = '#';
			linkElement.innerText = 'download document';

			documentElement.appendChild(linkElement);
			document.body.appendChild(documentElement);

			return documentElement;
		};

		it('is not fired when impression a document with an incorrect action value', async () => {
			const element = createDocumentsElementWithAction(
				'unknown' as AnalyticsTypes.ElementAction
			);

			const domContentLoaded = new Event('DOMContentLoaded');

			document.dispatchEvent(domContentLoaded);

			await wait(250);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'documentImpressionMade'
			);

			expect(events.length).toBeGreaterThanOrEqual(0);

			document.body.removeChild(element);
		});

		[
			{
				action: AnalyticsTypes.ElementAction.Impression,
				eventId: AnalyticsTypes.EventId.DocumentImpressionMade,
			},
			{
				action: AnalyticsTypes.ElementAction.Download,
				eventId: AnalyticsTypes.EventId.DocumentImpressionMade,
			},
		].forEach(async (props) => {
			it(`is fired ${props.eventId} when view a document with action ${props.action} and type: ${AnalyticsTypes.ElementType.FileEntry}`, async () => {
				const element = createDocumentsElementWithAction(props.action);

				const domContentLoaded = new Event('DOMContentLoaded');

				document.dispatchEvent(domContentLoaded);

				await wait(250);

				const events = Analytics.getEvents().filter(
					({eventId}) => eventId === props.eventId
				);

				expect(events.length).toBeGreaterThanOrEqual(1);

				expect(events[0]).toEqual(
					expect.objectContaining({
						applicationId: 'Document',
						eventId: props.eventId,
						properties: expect.objectContaining({
							fileEntryId: 'assetId',
							subtype: 'basic-document',
							title: 'assetTitle',
							type: AnalyticsTypes.ElementType.FileEntry,
						}),
					})
				);

				document.body.removeChild(element);
			});
		});
	});
});
