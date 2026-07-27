/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {closest, getClosestAssetElement, isTrackable} from '../utils/assets';
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

	if (dataset.analyticsAssetTags) {
		Object.assign(payload, {assetTags: dataset.analyticsAssetTags.trim()});
	}

	if (dataset.analyticsAssetTitle) {
		Object.assign(payload, {title: dataset.analyticsAssetTitle.trim()});
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
 * Sends information the first time a previewed Document is visible inside the
 * viewport.
 */
function trackDocumentPreviewed(analytics: Analytics) {
	return trackVisibleElements<AnalyticsType.HTMLElement>({
		isTrackable,
		onVisible: (element) => {
			analytics.send(
				AnalyticsType.EventId.DocumentPreviewed,
				AnalyticsType.ApplicationId.Document,
				getDocumentPayload(element)
			);
		},
		selector: '[data-analytics-asset-action="preview"]',
	});
}

/**
 * Plugin function that registers listeners for Document events
 */
function documents(analytics: Analytics) {
	return composeDisposers([
		trackDocumentDownloaded(analytics),
		trackDocumentPreviewed(analytics),
	]);
}

export {documents};
export default documents;
