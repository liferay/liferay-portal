/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {
	isImpressionAction,
	isTrackable,
	isViewAction,
	transformAssetTypeToSelector,
} from '../utils/assets';
import {composeDisposers} from '../utils/disposers';
import {trackVisibleElements} from '../utils/trackVisibleElements';

const customDatasetList = [
	AnalyticsType.DataSetList.AnalyticsAssetAction,
	AnalyticsType.DataSetList.AnalyticsAssetType,
	AnalyticsType.DataSetList.AnalyticsExternalReferenceCode,
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

	if (dataset.analyticsAssetCategories) {
		Object.assign(payload, {
			assetCategories: dataset.analyticsAssetCategories.trim(),
		});
	}

	if (dataset.analyticsAssetMimeType) {
		Object.assign(payload, {
			mimeType: dataset.analyticsAssetMimeType.trim(),
		});
	}

	if (dataset.analyticsAssetTags) {
		Object.assign(payload, {assetTags: dataset.analyticsAssetTags.trim()});
	}

	if (dataset.analyticsAssetTitle) {
		Object.assign(payload, {title: dataset.analyticsAssetTitle.trim()});
	}

	if (dataset.analyticsAssetVocabularies) {
		Object.assign(payload, {
			assetVocabularies: dataset.analyticsAssetVocabularies.trim(),
		});
	}

	if (dataset.analyticsObjectType) {
		Object.assign(payload, {
			objectType: dataset.analyticsObjectType.trim(),
		});
	}

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
 * Sends information the first time a ObjectEntry is visible inside the viewport.
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
		AnalyticsType.ElementType.ObjectEntry
	);

	return trackVisibleElements({
		isTrackable,
		onVisible: (element) => {
			analytics.send(
				eventId,
				AnalyticsType.ApplicationId.ObjectEntry,
				getObjectEntryPayload(element)
			);
		},
		selector,
	});
}

/**
 * Sends the impression event the first time an ObjectEntry flagged for
 * impression is visible inside the viewport.
 */
function trackObjectEntryImpression(analytics: Analytics) {
	return trackObjectEntry(analytics, {
		eventId: AnalyticsType.EventId.ObjectEntryImpressionMade,
		isTrackable: (element) =>
			isTrackable(element, customDatasetList) &&
			isImpressionAction(element),
	});
}

/**
 * Sends the view event the first time an ObjectEntry is visible inside the
 * viewport.
 */
function trackObjectEntryViewed(analytics: Analytics) {
	return trackObjectEntry(analytics, {
		eventId: AnalyticsType.EventId.ObjectEntryViewed,
		isTrackable: (element) =>
			isTrackable(element, customDatasetList) && isViewAction(element),
	});
}

/**
 * Plugin function that registers listeners for ObjectEntry events
 */
function objectEntry(analytics: Analytics) {
	return composeDisposers([
		trackObjectEntryDownloaded(analytics),
		trackObjectEntryImpression(analytics),
		trackObjectEntryViewed(analytics),
	]);
}

export {objectEntry};
export default objectEntry;
