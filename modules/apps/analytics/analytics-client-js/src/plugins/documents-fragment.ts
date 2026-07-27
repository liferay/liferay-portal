/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {
	isDownloadAction,
	isImpressionAction,
	isTrackable,
	transformAssetTypeToSelector,
} from '../utils/assets';
import {composeDisposers} from '../utils/disposers';
import {trackVisibleElements} from '../utils/trackVisibleElements';

/**
 * Returns analytics payload with Document information.
 */
function getDocumentPayload({dataset}: AnalyticsType.HTMLElement) {
	const payload = {
		fileEntryId: dataset.analyticsAssetId.trim(),
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

	if (dataset.analyticsAssetVersion) {
		Object.assign(payload, {
			fileEntryVersion: dataset.analyticsAssetVersion.trim(),
		});
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
 * Sends information the first time a Document is visible inside the viewport.
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

	return trackVisibleElements({
		isTrackable,
		onVisible: (element) => {
			analytics.send(
				eventId,
				AnalyticsType.ApplicationId.Document,
				getDocumentPayload(element)
			);
		},
		selector,
	});
}

/**
 * Sends the impression event the first time a Document is visible inside the
 * viewport. A link flagged for download also fires the impression event on the
 * documentsFragment plugin.
 */
function trackDocumentImpression(analytics: Analytics) {
	return trackDocument(analytics, {
		eventId: AnalyticsType.EventId.DocumentImpressionMade,
		isTrackable: (element) =>
			isTrackable(element) &&
			(isImpressionAction(element) || isDownloadAction(element)),
	});
}

/**
 * Plugin function that registers listeners for Document events.
 */
function documents(analytics: Analytics) {
	return composeDisposers([
		trackDocumentDownloaded(analytics),
		trackDocumentImpression(analytics),
	]);
}

export {documents};
export default documents;
