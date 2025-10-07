/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {isTrackable, transformAssetTypeToSelector} from '../utils/assets';
import {debounce} from '../utils/debounce';
import {onEvents, onReady} from '../utils/events';
import {isPartiallyInViewport} from '../utils/scroll';

const customDatasetList = [
	AnalyticsType.DataSetList.AnalyticsAssetAction,
	AnalyticsType.DataSetList.AnalyticsExternalReferenceCode,
	AnalyticsType.DataSetList.AnalyticsAssetType,
	AnalyticsType.DataSetList.AnalyticsObjectDefinitionName,
];

/**
 * Returns analytics payload with ObjectEntry information.
 */
function getObjectEntryPayload({
	dataset,
}: AnalyticsType.ObjectEntryHTMLElement) {
	const payload = {
		externalReferenceCode: dataset.analyticsExternalReferenceCode.trim(),
		objectDefinitionName: dataset.analyticsObjectDefinitionName.trim(),
	};

	return payload;
}

/**
 * Sends information when user clicks on a ObjectEntry with a link.
 */
function trackObjectEntryDownloaded(analytics: Analytics) {
	const onClick = (event: MouseEvent) => {
		const target = event.target as AnalyticsType.ObjectEntryHTMLElement;

		if (
			isTrackable(target, customDatasetList) &&
			target.dataset.analyticsAssetAction === 'download'
		) {
			analytics.send(
				AnalyticsType.EventId.ObjectEntryDownloaded,
				AnalyticsType.ApplicationId.ObjectEntry,
				getObjectEntryPayload(target)
			);
		}
	};

	document.addEventListener('click', onClick);

	return () => document.removeEventListener('click', onClick);
}

/**
 * Sends information the first time a ObjectEntry enters into the viewport.
 */
function trackObjectEntry(
	analytics: Analytics,
	{
		eventId,
		isTrackable,
	}: {
		eventId: AnalyticsType.EventId;
		isTrackable: (element: AnalyticsType.ObjectEntryHTMLElement) => boolean;
	}
) {
	const selector = transformAssetTypeToSelector(
		AnalyticsType.ElementType.ObjectEntry,
		`:not([data-analytics-asset-viewed="true"])`
	);

	const markViewedElements = debounce(() => {
		const elements = Array.prototype.slice
			.call(document.querySelectorAll(selector))
			.filter(isTrackable);

		elements.forEach((element) => {
			if (isPartiallyInViewport(element)) {
				const payload = getObjectEntryPayload(element);

				element.dataset.analyticsAssetViewed = true;

				analytics.send(
					eventId,
					AnalyticsType.ApplicationId.ObjectEntry,
					payload
				);
			}
		});
	}, 250);

	const stopTrackingOnReady = onReady(markViewedElements);
	const stopTrackingEvents = onEvents(
		['scroll', 'resize'],
		markViewedElements
	);

	return () => {
		stopTrackingEvents();
		stopTrackingOnReady();
	};
}

/**
 * Plugin function that registers listeners for ObjectEntry events
 */
function objectEntry(analytics: Analytics) {
	const stopTrackingObjectEntryDownloaded =
		trackObjectEntryDownloaded(analytics);
	const stopTrackingObjectEntryImpressionMade = trackObjectEntry(analytics, {
		eventId: AnalyticsType.EventId.ObjectEntryImpressionMade,
		isTrackable: (element) =>
			isTrackable(element, customDatasetList) &&
			element.dataset?.analyticsAssetAction ===
				AnalyticsType.ElementAction.Impression,
	});
	const stopTrackingObjectEntryViewed = trackObjectEntry(analytics, {
		eventId: AnalyticsType.EventId.ObjectEntryViewed,
		isTrackable: (element) =>
			isTrackable(element, customDatasetList) &&
			(!element.dataset?.analyticsAssetAction ||
				element.dataset?.analyticsAssetAction ===
					AnalyticsType.ElementAction.View),
	});

	return () => {
		stopTrackingObjectEntryDownloaded();
		stopTrackingObjectEntryImpressionMade();
		stopTrackingObjectEntryViewed();
	};
}

export {objectEntry};
export default objectEntry;
