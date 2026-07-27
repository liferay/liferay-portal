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
import {DEBOUNCE} from '../utils/constants';
import {debounce} from '../utils/debounce';
import {composeDisposers} from '../utils/disposers';
import {clickEvent} from '../utils/events';
import {ScrollTracker} from '../utils/scroll';
import {trackVisibleElements} from '../utils/trackVisibleElements';

/**
 * Returns analytics payload with Blog information.
 */
function getBlogPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		entryId: dataset.analyticsAssetId.trim(),
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
 * Sends information the first time a Blog is visible inside the viewport.
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

	const stopTrackingBlogViewed = trackVisibleElements({
		isTrackable,
		onVisible: (element) => {
			const payload = getBlogPayload(element);

			Object.assign(payload, {
				numberOfWords: getNumberOfWords(element),
			});

			blogElements.push(element);

			analytics.send(eventId, AnalyticsType.ApplicationId.Blog, payload);
		},
		selector,
	});

	const stopTrackingBlogsScroll = trackBlogsScroll(analytics, blogElements);

	return () => {
		stopTrackingBlogsScroll();
		stopTrackingBlogViewed();
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
 * Sends the impression event the first time a Blog flagged for impression is
 * visible inside the viewport.
 */
function trackBlogImpression(analytics: Analytics) {
	return trackBlog(analytics, {
		eventId: AnalyticsType.EventId.BlogImpressionMade,
		isTrackable: (element) =>
			isTrackable(element) && isImpressionAction(element),
	});
}

/**
 * Sends the view event the first time a Blog is visible inside the viewport.
 */
function trackBlogViewed(analytics: Analytics) {
	return trackBlog(analytics, {
		eventId: AnalyticsType.EventId.BlogViewed,
		isTrackable: (element) => isTrackable(element) && isViewAction(element),
	});
}

/**
 * Plugin function that registers listeners for Blog events
 */
function blogs(analytics: Analytics) {
	return composeDisposers([
		trackBlogClicked(analytics),
		trackBlogImpression(analytics),
		trackBlogViewed(analytics),
	]);
}

export {blogs};
export default blogs;
