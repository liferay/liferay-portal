/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {
	getNumberOfWords,
	isTrackable,
	transformAssetTypeToSelector,
} from '../utils/assets';
import {debounce} from '../utils/debounce';
import {clickEvent, onEvents, onReady} from '../utils/events';
import {isPartiallyInViewport} from '../utils/scroll';

/**
 * Returns analytics payload with WebContent information.
 */
function getWebContentPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		articleId: dataset.analyticsAssetId.trim(),
	};

	if (dataset.analyticsAssetSubtype) {
		Object.assign(payload, {subtype: dataset.analyticsAssetSubtype.trim()});
	}

	if (dataset.analyticsAssetTitle) {
		Object.assign(payload, {title: dataset.analyticsAssetTitle.trim()});
	}

	if (dataset.analyticsAssetType) {
		Object.assign(payload, {type: dataset.analyticsAssetType.trim()});
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
 * Sends information the first time a WebContent enters into the viewport.
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
	const selector = transformAssetTypeToSelector(
		[
			AnalyticsType.ElementType.WebContent,
			AnalyticsType.ElementType.JournalArticle,
		],
		`:not([data-analytics-asset-viewed="true"])`
	);

	const markViewedElements = debounce(() => {
		const elements = Array.prototype.slice
			.call(document.querySelectorAll(selector))
			.filter(isTrackable);

		elements.forEach((element) => {
			if (isPartiallyInViewport(element)) {
				const payload = getWebContentPayload(element);

				Object.assign(payload, {
					numberOfWords: getNumberOfWords(element),
				});

				element.dataset.analyticsAssetViewed = true;

				analytics.send(
					eventId,
					AnalyticsType.ApplicationId.WebContent,
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
 * Plugin function that registers listeners for Web Content events
 */
function webContent(analytics: Analytics) {
	const stopTrackingWebContentClicked = trackWebContentClicked(analytics);
	const stopTrackingWebContentImpressionMade = trackWebContent(analytics, {
		eventId: AnalyticsType.EventId.WebContentImpressionMade,
		isTrackable: (element) =>
			isTrackable(element) &&
			element.dataset?.analyticsAssetAction ===
				AnalyticsType.ElementAction.Impression,
	});
	const stopTrackingWebContentViewed = trackWebContent(analytics, {
		eventId: AnalyticsType.EventId.WebContentViewed,
		isTrackable: (element) =>
			isTrackable(element) &&
			(!element.dataset?.analyticsAssetAction ||
				element.dataset?.analyticsAssetAction ===
					AnalyticsType.ElementAction.View),
	});

	return () => {
		stopTrackingWebContentClicked();
		stopTrackingWebContentImpressionMade();
		stopTrackingWebContentViewed();
	};
}

export {webContent};
export default webContent;
