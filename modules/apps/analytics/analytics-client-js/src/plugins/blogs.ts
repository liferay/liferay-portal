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
import {DEBOUNCE} from '../utils/constants';
import {debounce} from '../utils/debounce';
import {clickEvent, onReady} from '../utils/events';
import {ScrollTracker} from '../utils/scroll';

/**
 * Returns analytics payload with Blog information.
 */
function getBlogPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		entryId: dataset.analyticsAssetId.trim(),
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

	if (dataset.analyticsExternalReferenceCode) {
		Object.assign(payload, {
			externalReferenceCode:
				dataset.analyticsExternalReferenceCode.trim(),
		});
	}

	return payload;
}

/**
 * Sends information about Blogs scroll actions.
 */
function trackBlogsScroll(
	analytics: Analytics,
	blogElements: AnalyticsType.HTMLElement[]
) {
	const scrollSessionId = new Date().toISOString();
	const scrollTracker = new ScrollTracker();

	const onScroll = debounce(() => {
		blogElements.forEach((element) => {
			scrollTracker.onDepthReached((depth) => {
				const payload = getBlogPayload(element);
				Object.assign(payload, {depth, sessionId: scrollSessionId});

				analytics.send(
					AnalyticsType.EventId.BlogDepthReached,
					AnalyticsType.ApplicationId.Blog,
					payload
				);
			}, element);
		});
	}, DEBOUNCE);

	document.addEventListener('scroll', onScroll as EventListener);

	return () => {
		document.removeEventListener('scroll', onScroll as EventListener);
	};
}

/**
 * Sends information when user scrolls on a Blog.
 */
function trackBlog(
	analytics: Analytics,
	{
		eventId,
		isTrackable,
	}: {
		eventId: AnalyticsType.EventId;
		isTrackable: (element: AnalyticsType.HTMLElement) => boolean;
	}
) {
	const blogElements: AnalyticsType.HTMLElement[] = [];

	const selector = transformAssetTypeToSelector([
		AnalyticsType.ElementType.Blog,
		AnalyticsType.ElementType.BlogsEntry,
	]);

	const stopTrackingOnReady = onReady(() => {
		Array.prototype.slice
			.call(document.querySelectorAll(selector))
			.filter(isTrackable)
			.forEach((element: AnalyticsType.HTMLElement) => {
				const payload = getBlogPayload(element);

				Object.assign(payload, {
					numberOfWords: getNumberOfWords(element),
				});

				blogElements.push(element);

				analytics.send(
					eventId,
					AnalyticsType.ApplicationId.Blog,
					payload
				);
			});
	});

	const stopTrackingBlogsScroll = trackBlogsScroll(analytics, blogElements);

	return () => {
		stopTrackingBlogsScroll();
		stopTrackingOnReady();
	};
}

/**
 * Sends information when user clicks on a Blog.
 */
function trackBlogClicked(analytics: Analytics) {
	return clickEvent({
		analytics,
		applicationId: AnalyticsType.ApplicationId.Blog,
		eventType: AnalyticsType.EventId.BlogClicked,
		getPayload: getBlogPayload,
		isTrackable,
		type: AnalyticsType.ElementType.Blog,
	});
}

/**
 * Plugin function that registers listeners for Blog events
 */
function blogs(analytics: Analytics) {
	const stopTrackingBlogClicked = trackBlogClicked(analytics);
	const stopTrackingBlogImpressionMade = trackBlog(analytics, {
		eventId: AnalyticsType.EventId.BlogImpressionMade,
		isTrackable: (element) =>
			isTrackable(element) &&
			element.dataset?.analyticsAssetAction ===
				AnalyticsType.ElementAction.Impression,
	});
	const stopTrackingBlogViewed = trackBlog(analytics, {
		eventId: AnalyticsType.EventId.BlogViewed,
		isTrackable: (element) =>
			isTrackable(element) &&
			(!element.dataset?.analyticsAssetAction ||
				element.dataset?.analyticsAssetAction ===
					AnalyticsType.ElementAction.View),
	});

	return () => {
		stopTrackingBlogClicked();
		stopTrackingBlogImpressionMade();
		stopTrackingBlogViewed();
	};
}

export {blogs};
export default blogs;
