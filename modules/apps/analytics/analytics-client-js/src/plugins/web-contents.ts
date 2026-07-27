/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {
	getNumberOfWords,
	isImpressionAction,
	isTrackable,
	isViewAction,
	transformAssetTypeToSelector,
} from '../utils/assets';
import {composeDisposers} from '../utils/disposers';
import {clickEvent} from '../utils/events';
import {trackVisibleElements} from '../utils/trackVisibleElements';

/**
 * Returns analytics payload with WebContent information.
 */
function getWebContentPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		articleId: dataset.analyticsAssetId.trim(),
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

	if (dataset.analyticsAssetSubtype) {
		Object.assign(payload, {subtype: dataset.analyticsAssetSubtype.trim()});
	}

	if (dataset.analyticsAssetTags) {
		Object.assign(payload, {assetTags: dataset.analyticsAssetTags.trim()});
	}

	if (dataset.analyticsAssetTitle) {
		Object.assign(payload, {title: dataset.analyticsAssetTitle.trim()});
	}

	if (dataset.analyticsAssetType) {
		Object.assign(payload, {type: dataset.analyticsAssetType.trim()});
	}

	if (dataset.analyticsAssetVocabularies) {
		Object.assign(payload, {
			assetVocabularies: dataset.analyticsAssetVocabularies.trim(),
		});
	}

	if (dataset.analyticsWebContentResourcePk) {
		Object.assign(payload, {
			webContentResourcePk: dataset.analyticsWebContentResourcePk.trim(),
		});
	}

	if (dataset.analyticsExternalReferenceCode) {
		Object.assign(payload, {
			externalReferenceCode:
				dataset.analyticsExternalReferenceCode.trim(),
		});
	}

	return payload;
}

/**
 * Sends information when user clicks on a Web Content.
 */
function trackWebContentClicked(analytics: Analytics) {
	return clickEvent({
		analytics,
		applicationId: AnalyticsType.ApplicationId.WebContent,
		eventType: AnalyticsType.EventId.WebContentClicked,
		getPayload: getWebContentPayload,
		isTrackable,
		type: [
			AnalyticsType.ElementType.WebContent,
			AnalyticsType.ElementType.JournalArticle,
		],
	});
}

/**
 * Sends information the first time a WebContent is visible inside the viewport.
 */
function trackWebContent(
	analytics: Analytics,
	{
		eventId,
		isTrackable,
	}: {
		eventId: AnalyticsType.EventId;
		isTrackable: (element: AnalyticsType.HTMLElement) => boolean;
	}
) {
	const selector = transformAssetTypeToSelector([
		AnalyticsType.ElementType.WebContent,
		AnalyticsType.ElementType.JournalArticle,
	]);

	return trackVisibleElements({
		isTrackable,
		onVisible: (element) => {
			const payload = getWebContentPayload(element);

			Object.assign(payload, {
				numberOfWords: getNumberOfWords(element),
			});

			analytics.send(
				eventId,
				AnalyticsType.ApplicationId.WebContent,
				payload
			);
		},
		selector,
	});
}

/**
 * Sends the impression event the first time a WebContent flagged for impression
 * is visible inside the viewport.
 */
function trackWebContentImpression(analytics: Analytics) {
	return trackWebContent(analytics, {
		eventId: AnalyticsType.EventId.WebContentImpressionMade,
		isTrackable: (element) =>
			isTrackable(element) && isImpressionAction(element),
	});
}

/**
 * Sends the view event the first time a WebContent is visible inside the
 * viewport.
 */
function trackWebContentViewed(analytics: Analytics) {
	return trackWebContent(analytics, {
		eventId: AnalyticsType.EventId.WebContentViewed,
		isTrackable: (element) => isTrackable(element) && isViewAction(element),
	});
}

/**
 * Plugin function that registers listeners for Web Content events
 */
function webContent(analytics: Analytics) {
	return composeDisposers([
		trackWebContentClicked(analytics),
		trackWebContentImpression(analytics),
		trackWebContentViewed(analytics),
	]);
}

export {webContent};
export default webContent;
