/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {isTrackable, transformAssetTypeToSelector} from '../utils/assets';
import {onReady} from '../utils/events';

/**
 * Returns analytics payload with Document information.
 */
function getDocumentPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		fileEntryId: dataset.analyticsAssetId.trim(),
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

	if (dataset.analyticsAssetVersion) {
		Object.assign(payload, {
			fileEntryVersion: dataset.analyticsAssetVersion.trim(),
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
 * Sends information when user clicks on a Document.
 */
function trackDocumentDownloaded(analytics: Analytics) {
	const onClick = (event: MouseEvent) => {
		const element = event.target as AnalyticsType.HTMLElement;
		const parentElement =
			element.parentElement as AnalyticsType.HTMLElement | null;

		const target = [element, parentElement].find(
			(element) => element?.dataset.analyticsAssetAction === 'download'
		);

		if (target && isTrackable(target)) {
			analytics.send(
				AnalyticsType.EventId.DocumentDownloaded,
				AnalyticsType.ApplicationId.Document,
				getDocumentPayload(target)
			);
		}
	};

	document.addEventListener('click', onClick);

	return () => document.removeEventListener('click', onClick);
}

/**
 * Sends information when user scrolls on a Document.
 */
function trackDocument(
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
		AnalyticsType.ElementType.FileEntry
	);

	const stopTrackingOnReady = onReady(() => {
		Array.prototype.slice
			.call(document.querySelectorAll(selector))
			.filter((element) => isTrackable(element))
			.forEach((element) => {
				const payload = getDocumentPayload(element);

				analytics.send(
					eventId,
					AnalyticsType.ApplicationId.Document,
					payload
				);
			});
	});

	return () => stopTrackingOnReady();
}

/**
 * Plugin function that registers listeners for Document events.
 * A link with action download should fire a documentImpressionMade event
 * on load page to the documentsFragment plugin.
 */
function documents(analytics: Analytics) {
	const stopTrackingDocumentDownloaded = trackDocumentDownloaded(analytics);
	const stopTrackingDocumentImpressionMade = trackDocument(analytics, {
		eventId: AnalyticsType.EventId.DocumentImpressionMade,
		isTrackable: (element) =>
			(isTrackable(element) &&
				element.dataset.analyticsAssetAction ===
					AnalyticsType.ElementAction.Impression) ||
			(isTrackable(element) &&
				element.dataset.analyticsAssetAction ===
					AnalyticsType.ElementAction.Download),
	});

	return () => {
		stopTrackingDocumentDownloaded();
		stopTrackingDocumentImpressionMade();
	};
}

export {documents};
export default documents;
