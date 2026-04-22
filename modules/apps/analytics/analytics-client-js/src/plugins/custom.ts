/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {closest, getClosestAssetElement, isTrackable} from '../utils/assets';
import {DEBOUNCE} from '../utils/constants';
import {debounce} from '../utils/debounce';
import {clickEvent, onReady} from '../utils/events';
import {ScrollTracker} from '../utils/scroll';

/**
 * Returns analytics payload with Custom Asset information.
 */
function getCustomAssetPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		assetId: dataset.analyticsAssetId.trim(),
	};

	if (dataset.analyticsAssetCategory) {
		Object.assign(payload, {
			category: dataset.analyticsAssetCategory.trim(),
		});
	}

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

	return payload;
}

/**
 * Sends information when user clicks on a Custom Asset.
 */
function trackCustomAssetDownloaded(analytics: Analytics) {
	const onClick = (event: MouseEvent) => {
		const target = event.target as AnalyticsType.HTMLElement;

		const actionElement = closest(
			target,
			'[data-analytics-asset-action="download"]'
		);

		const customAssetElement = getClosestAssetElement(
			target,
			AnalyticsType.ElementType.Custom
		) as AnalyticsType.HTMLElement;

		if (
			actionElement &&
			isTrackable(customAssetElement, [
				AnalyticsType.DataSetList.AnalyticsAssetId,
				AnalyticsType.DataSetList.AnalyticsAssetType,
			])
		) {
			analytics.send(
				AnalyticsType.EventId.AssetDownloaded,
				AnalyticsType.ApplicationId.Custom,
				getCustomAssetPayload(customAssetElement)
			);
		}
	};

	document.addEventListener('click', onClick);

	return () => document.removeEventListener('click', onClick);
}

/**
 * Sends information about Custom Asset scroll actions.
 */
function trackCustomAssetScroll(
	analytics: Analytics,
	customAssetElements: AnalyticsType.HTMLElement[]
) {
	const scrollSessionId = new Date().toISOString();
	const scrollTracker = new ScrollTracker();

	const onScroll = debounce(() => {
		customAssetElements.forEach((element) => {
			scrollTracker.onDepthReached((depth) => {
				const payload = getCustomAssetPayload(element);
				Object.assign(payload, {depth, sessionId: scrollSessionId});

				analytics.send(
					AnalyticsType.EventId.AssetDepthReached,
					AnalyticsType.ApplicationId.Custom,
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
 * Adds an event listener for a Custom Asset submission and sends information when that
 * event happens.
 */
function trackCustomAssetSubmitted(analytics: Analytics) {
	const onSubmit = (event: MouseEvent) => {
		const target = event.target as AnalyticsType.HTMLElement;
		const customAssetElement = getClosestAssetElement(
			target,
			AnalyticsType.ElementType.Custom
		) as AnalyticsType.HTMLElement;
		const isElementTrackable = isTrackable(customAssetElement, [
			AnalyticsType.DataSetList.AnalyticsAssetId,
			AnalyticsType.DataSetList.AnalyticsAssetType,
		]);

		if (
			!isElementTrackable ||
			(isElementTrackable &&
				(target.tagName !== 'FORM' || event.defaultPrevented))
		) {
			return;
		}

		analytics.send(
			AnalyticsType.EventId.AssetSubmitted,
			AnalyticsType.ApplicationId.Custom,
			getCustomAssetPayload(customAssetElement)
		);
	};

	document.addEventListener('submit', onSubmit as EventListener);

	return () =>
		document.removeEventListener('submit', onSubmit as EventListener);
}

/**
 * Sends information when user scrolls on a Custom asset.
 */
function trackCustomAssetViewed(analytics: Analytics) {
	const customAssetElements: AnalyticsType.HTMLElement[] = [];
	const stopTrackingOnReady = onReady(() => {
		Array.prototype.slice
			.call(
				document.querySelectorAll(
					'[data-analytics-asset-type="custom"]'
				)
			)
			.filter((element) =>
				isTrackable(element, [
					AnalyticsType.DataSetList.AnalyticsAssetId,
					AnalyticsType.DataSetList.AnalyticsAssetType,
				])
			)
			.forEach((element) => {
				const formEnabled =
					!!element.getElementsByTagName('form').length;

				const payload = getCustomAssetPayload(element);
				Object.assign(payload, {formEnabled});

				customAssetElements.push(element);

				analytics.send(
					AnalyticsType.EventId.AssetViewed,
					AnalyticsType.ApplicationId.Custom,
					payload
				);
			});
	});

	const stopTrackingCustomAssetScroll = trackCustomAssetScroll(
		analytics,
		customAssetElements
	);

	return () => {
		stopTrackingCustomAssetScroll();
		stopTrackingOnReady();
	};
}

/**
 * Sends information when user clicks on a Custom Asset.
 */
function trackCustomAssetClick(analytics: Analytics) {
	return clickEvent({
		analytics,
		applicationId: AnalyticsType.ApplicationId.Custom,
		eventType: AnalyticsType.EventId.AssetClicked,
		getPayload: getCustomAssetPayload,
		isTrackable: (element) =>
			isTrackable(element, [
				AnalyticsType.DataSetList.AnalyticsAssetId,
				AnalyticsType.DataSetList.AnalyticsAssetType,
			]),
		type: AnalyticsType.ElementType.Custom,
	});
}

/**
 * Plugin function that registers listeners for Custom Asset events
 */
function custom(analytics: Analytics) {
	const stopTrackingClicked = trackCustomAssetClick(analytics);
	const stopTrackingDownloaded = trackCustomAssetDownloaded(analytics);
	const stopTrackingSubmitted = trackCustomAssetSubmitted(analytics);
	const stopTrackingViewed = trackCustomAssetViewed(analytics);

	return () => {
		stopTrackingClicked();
		stopTrackingDownloaded();
		stopTrackingSubmitted();
		stopTrackingViewed();
	};
}

export {custom};
export default custom;
