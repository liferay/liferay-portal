/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// @ts-ignore - Check possibility to install package in ts format

import fetchMock from 'fetch-mock';

import AnalyticsClient from '../../src/analytics';
import {INITIAL_ANALYTICS_CONFIG} from '../helpers';

const applicationId = 'Form';

const createDynamicFormElement = async (attrs: any) => {
	const element = document.createElement('div');

	for (let index = 0; index < Object.keys(attrs).length; index++) {
		element.dataset[Object.keys(attrs)[index]] = attrs[index];
	}

	document.body.appendChild(element);

	element.addEventListener('submit', (event) => event.preventDefault());

	const event = new Event('submit', {
		cancelable: true,
	});

	await element.dispatchEvent(event);

	return element;
};

describe('Forms Plugin', () => {
	let Analytics: AnalyticsClient;
	let duration: number;

	beforeEach(() => {

		// Force attaching DOM Content Loaded event

		Object.defineProperty(document, 'readyState', {
			value: 'loading',
			writable: false,
		});

		if (!global.performance.clearMarks) {
			global.performance.clearMarks = () => {};
		}

		if (!global.performance.mark) {

			// @ts-ignore

			global.performance.mark = () => {};
		}

		if (!global.performance.measure) {

			// @ts-ignore

			global.performance.measure = () => {};
		}

		if (!global.performance.getEntriesByName) {
			global.performance.getEntriesByName = () => [
				{
					duration: duration || 1,
					entryType: '',
					name: '',
					startTime: 0,
					toJSON() {
						throw new Error('Function not implemented.');
					},
				},
			];
		}

		fetchMock.mock('*', () => 200);

		Analytics = AnalyticsClient.create(INITIAL_ANALYTICS_CONFIG);
	});

	afterEach(() => {
		Analytics.reset();
		AnalyticsClient.dispose();

		fetchMock.restore();
	});

	describe('formViewed event', () => {
		it('is fired for every form on the page', async () => {
			const formElement = document.createElement('form');

			formElement.dataset.analyticsAssetId = 'assetId';
			formElement.dataset.analyticsAssetTitle = 'Form Title 1';
			formElement.dataset.analyticsAssetType = 'form';

			document.body.appendChild(formElement);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'formViewed'
			);

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'formViewed',
					properties: expect.objectContaining({
						formId: 'assetId',
						title: 'Form Title 1',
					}),
				}),
			]);

			document.body.removeChild(formElement);
		});

		it('includes assetCategories, assetVocabularies, mimeType and assetTags in the payload', async () => {
			const formElement = document.createElement('form');

			formElement.dataset.analyticsAssetId = 'assetId';
			formElement.dataset.analyticsAssetTitle = 'Form Title';
			formElement.dataset.analyticsAssetType = 'form';
			formElement.dataset.analyticsAssetCategories =
				'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]';
			formElement.dataset.analyticsAssetMimeType = 'text/html';
			formElement.dataset.analyticsAssetTags =
				'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]';
			formElement.dataset.analyticsAssetVocabularies =
				'[{"id":"voc1","name":"Vocabulary 1"}]';

			document.body.appendChild(formElement);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'formViewed'
			);

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'formViewed',
					properties: expect.objectContaining({
						assetCategories:
							'[{"id":"cat1","name":"Category 1","vocabularyId":"voc1"},{"id":"cat2","name":"Category 2","vocabularyId":"voc1"}]',
						assetTags:
							'[{"id":"tag1","name":"Tag 1"},{"id":"tag2","name":"Tag 2"}]',
						assetVocabularies:
							'[{"id":"voc1","name":"Vocabulary 1"}]',
						formId: 'assetId',
						mimeType: 'text/html',
						title: 'Form Title',
					}),
				}),
			]);

			document.body.removeChild(formElement);
		});

		it('remove spaces between assetTitle and assetId', async () => {
			const formElement = document.createElement('form');

			formElement.dataset.analyticsAssetId = ' assetId ';
			formElement.dataset.analyticsAssetTitle = ' Form Title 1 ';
			formElement.dataset.analyticsAssetType = 'form';

			document.body.appendChild(formElement);

			const domContentLoaded = new Event('DOMContentLoaded');

			await document.dispatchEvent(domContentLoaded);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'formViewed'
			);

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'formViewed',
					properties: expect.objectContaining({
						formId: 'assetId',
						title: 'Form Title 1',
					}),
				}),
			]);

			document.body.removeChild(formElement);
		});
	});

	describe('formSubmitted event', () => {
		it('is fired when a form is submitted', async () => {
			const form = document.createElement('form');

			form.dataset.analyticsAssetId = 'formId';
			form.dataset.analyticsAssetTitle = 'Form Title';
			form.dataset.analyticsAssetType = 'form';

			document.body.appendChild(form);

			form.addEventListener('submit', (event) => event.preventDefault());

			const event = new Event('submit', {
				cancelable: true,
			});

			await form.dispatchEvent(event);

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'formSubmitted'
			);

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'formSubmitted',
					properties: {
						formId: 'formId',
						title: 'Form Title',
					},
				}),
			]);
		});
	});

	describe('fieldFocused event', () => {
		it('is fired whenever a field is focused', async () => {
			const form = document.createElement('form');

			form.dataset.analyticsAssetId = 'formId';
			form.dataset.analyticsAssetTitle = 'Form Title';
			form.dataset.analyticsAssetType = 'form';

			document.body.appendChild(form);

			const field = document.createElement('input');

			field.name = 'myField';
			field.type = 'text';

			form.appendChild(field);

			await field.dispatchEvent(new Event('focus'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'fieldFocused'
			);

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'fieldFocused',
					properties: {
						fieldName: 'myField',
						formId: 'formId',
						title: 'Form Title',
					},
				}),
			]);
		});
	});

	describe('fieldBlurred event', () => {
		it('is fired whenever a field is blurred', async () => {
			const form = document.createElement('form');

			form.dataset.analyticsAssetId = 'formId';
			form.dataset.analyticsAssetTitle = 'Form Title';
			form.dataset.analyticsAssetType = 'form';

			document.body.appendChild(form);

			const field = document.createElement('input');

			field.name = 'myField';
			field.type = 'text';

			form.appendChild(field);

			field.dispatchEvent(new Event('focus'));

			// Fake timing.

			duration = 1500;

			await field.dispatchEvent(new Event('blur'));

			const events = Analytics.getEvents().filter(
				({eventId}) => eventId === 'fieldBlurred'
			);

			expect(events).toEqual([
				expect.objectContaining({
					applicationId,
					eventId: 'fieldBlurred',
					properties: expect.objectContaining({
						fieldName: 'myField',
						focusDuration: expect.any(Number),
						formId: 'formId',
						title: 'Form Title',
					}),
				}),
			]);

			expect(events[0].properties.focusDuration).toBeGreaterThanOrEqual(
				1500
			);
		});
	});

	describe('formSubmitted required attributes', () => {
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
				const element = await createDynamicFormElement(attrs);

				const events = Analytics.getEvents().filter(
					({eventId}) => eventId === 'formSubmitted'
				);

				expect(events).toEqual([]);

				document.body.removeChild(element);
			}
		);
	});
});
