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

const applicationId = 'ObjectEntry';

const createObjectEntryElement = (action: AnalyticsTypes.ElementAction) => {
	const objectEntryElement = document.createElement('div');

	objectEntryElement.dataset.analyticsAssetAction = action;
	objectEntryElement.dataset.analyticsExternalReferenceCode =
		'a66d047e-3203-401a-890c-b881a9c54648';
	objectEntryElement.dataset.analyticsObjectDefinitionName =
		'my-custom-object-definition-name';
	objectEntryElement.dataset.analyticsAssetType =
		AnalyticsTypes.ElementType.ObjectEntry;
	objectEntryElement.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(objectEntryElement);

	return objectEntryElement;
};

const createObjectEntryLinkElement = () => {
	const objectEntryElement = document.createElement('a');

	objectEntryElement.href = '#';
	objectEntryElement.dataset.analyticsAssetAction =
		AnalyticsTypes.ElementAction.Download;
	objectEntryElement.dataset.analyticsExternalReferenceCode =
		'a66d047e-3203-401a-890c-b881a9c54648';
	objectEntryElement.dataset.analyticsObjectDefinitionName =
		'my-custom-object-definition-name';
	objectEntryElement.dataset.analyticsAssetType =
		AnalyticsTypes.ElementType.ObjectEntry;
	objectEntryElement.innerText =
		'Lorem ipsum dolor, sit amet consectetur adipisicing elit.';

	document.body.appendChild(objectEntryElement);

	return objectEntryElement;
};

describe('ObjectEntry Plugin', () => {
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

	describe('objectEntryDownloaded event', () => {
		it('is fired when clicking in a fragment with a link', async () => {
			const objectEntryElement = createObjectEntryLinkElement();

			await userEvent.click(objectEntryElement);

			expect(Analytics.getEvents()).toEqual([
				expect.objectContaining({
					applicationId: AnalyticsTypes.ApplicationId.ObjectEntry,
					eventId: AnalyticsTypes.EventId.ObjectEntryDownloaded,
					properties: expect.objectContaining({
						externalReferenceCode:
							'a66d047e-3203-401a-890c-b881a9c54648',
						objectDefinitionName:
							'my-custom-object-definition-name',
					}),
				}),
			]);

			document.body.removeChild(objectEntryElement);
		});
	});

	describe('objectEntryViewed event', () => {
		it('is fired when objectEntry is in viewport', async () => {
			const objectEntryElement = createObjectEntryElement(
				AnalyticsTypes.ElementAction.View
			);

			jest.spyOn(
				objectEntryElement,
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
				({eventId}) =>
					eventId === AnalyticsTypes.EventId.ObjectEntryViewed
			);

			expect(events.length).toBeGreaterThanOrEqual(1);

			expect(events[0]).toEqual(
				expect.objectContaining({
					applicationId,
					eventId: AnalyticsTypes.EventId.ObjectEntryViewed,
					properties: expect.objectContaining({
						externalReferenceCode:
							'a66d047e-3203-401a-890c-b881a9c54648',
						objectDefinitionName:
							'my-custom-object-definition-name',
					}),
				})
			);

			document.body.removeChild(objectEntryElement);
		});

		it('is not fired when objectEntry is not in viewport', async () => {
			const objectEntryElement = createObjectEntryElement(
				AnalyticsTypes.ElementAction.View
			);

			jest.spyOn(
				objectEntryElement,
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
				({eventId}) =>
					eventId === AnalyticsTypes.EventId.ObjectEntryViewed
			);

			expect(events.length).toBeGreaterThanOrEqual(0);

			document.body.removeChild(objectEntryElement);
		});
	});

	describe('ObjectEntry events with actions', () => {
		const createObjectEntryElementWithAction = (
			action: AnalyticsTypes.ElementAction
		) => {
			const setDataset = (
				element: AnalyticsTypes.ObjectEntryHTMLElement,
				data: AnalyticsTypes.ObjectEntryHTMLElement['dataset']
			) => {
				Object.entries(data).forEach(([key, value]) => {

					// @ts-ignore

					element.dataset[key] = value;
				});
			};

			const objectEntryElement = document.createElement(
				'div'
			) as unknown as AnalyticsTypes.ObjectEntryHTMLElement;

			setDataset(objectEntryElement, {
				analyticsAssetAction: action,
				analyticsAssetType: AnalyticsTypes.ElementType.ObjectEntry,
				analyticsExternalReferenceCode:
					'a66d047e-3203-401a-890c-b881a9c54648',
				analyticsObjectDefinitionName:
					'my-custom-object-definition-name',
			});

			objectEntryElement.innerText = `Lorem ipsum dolor, sit amet consectetur adipisicing elit.`;

			document.body.appendChild(objectEntryElement);

			return objectEntryElement;
		};

		it('is not fired when view objectEntry with an incorrect action value', async () => {
			const element = createObjectEntryElementWithAction(
				'unknown' as AnalyticsTypes.ElementAction
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
				({eventId}) =>
					eventId === AnalyticsTypes.EventId.ObjectEntryViewed
			);

			expect(events.length).toBeGreaterThanOrEqual(0);

			document.body.removeChild(element);
		});

		[
			{
				action: AnalyticsTypes.ElementAction.View,
				eventId: AnalyticsTypes.EventId.ObjectEntryViewed,
			},
			{
				action: AnalyticsTypes.ElementAction.Impression,
				eventId: AnalyticsTypes.EventId.ObjectEntryImpressionMade,
			},
		].forEach(
			async (props: {
				action: AnalyticsTypes.ElementAction;
				eventId: AnalyticsTypes.EventId;
			}) => {
				it(`is fired ${props.eventId} when view a objectEntry with action ${props.action} and type: ${AnalyticsTypes.ElementType.ObjectEntry}`, async () => {
					const element = createObjectEntryElementWithAction(
						props.action
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
								externalReferenceCode:
									'a66d047e-3203-401a-890c-b881a9c54648',
								objectDefinitionName:
									'my-custom-object-definition-name',
							}),
						})
					);

					document.body.removeChild(element);
				});
			}
		);
	});
});
