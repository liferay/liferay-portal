/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {closest, getClosestAssetElement, isTrackable} from '../utils/assets';
import {onReady} from '../utils/events';

/**
 * Returns analytics payload with Document information.
 */
function getDocumentPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		fileEntryId: dataset.analyticsAssetId.trim(),
	};

	if (dataset.analyticsAssetTitle) {
		Object.assign(payload, {title: dataset.analyticsAssetTitle.trim()});
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
		const target = event.target as AnalyticsType.HTMLElement;
		const actionElement = closest(
			target,
			'[data-analytics-asset-action="download"]'
		);
		const documentElement = getClosestAssetElement(
			target,
			AnalyticsType.ElementType.Document
		) as AnalyticsType.HTMLElement;

		if (actionElement && isTrackable(documentElement)) {
			analytics.send(
				AnalyticsType.EventId.DocumentDownloaded,
				AnalyticsType.ApplicationId.Document,
				getDocumentPayload(documentElement)
			);
		}
	};

	document.addEventListener('click', onClick);

	return () => document.removeEventListener('click', onClick);
}

/**
 * Sends information when user scrolls on a Document.
 */
function trackDocumentPreviewed(analytics: Analytics) {
	const stopTrackingOnReady = onReady(() => {
		Array.prototype.slice
			.call(
				document.querySelectorAll(
					'[data-analytics-asset-action="preview"]'
				)
			)
			.filter((element) => isTrackable(element))
			.forEach((element) => {
				const payload = getDocumentPayload(element);

				analytics.send(
					AnalyticsType.EventId.DocumentPreviewed,
					AnalyticsType.ApplicationId.Document,
					payload
				);
			});
	});

	return () => stopTrackingOnReady();
}

/**
 * Plugin function that registers listeners for Document events
 */
function documents(analytics: Analytics) {
	const stopTrackingDocumentDownloaded = trackDocumentDownloaded(analytics);
	const stopTrackingDocumentPreviewed = trackDocumentPreviewed(analytics);

	return () => {
		stopTrackingDocumentDownloaded();
		stopTrackingDocumentPreviewed();
	};
}

export {documents};
export default documents;
