/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Analytics} from '../types';

function transformAssetTypeToSelector(
	assetType: Analytics.ElementType | Analytics.ElementType[],
	suffix: string = ''
) {
	if (typeof assetType === 'object') {
		return assetType
			.map((type) => `[data-analytics-asset-type="${type}"]${suffix}`)
			.join(', ');
	}

	return `[data-analytics-asset-type="${assetType}"]${suffix}`;
}

/**
 * Returns first webContent element ancestor of given element.
 */
function getClosestAssetElement(
	element: Analytics.HTMLElement,
	assetType: Analytics.ElementType | Analytics.ElementType[]
): Analytics.HTMLElement | null {
	return closest(element, transformAssetTypeToSelector(assetType));
}

function closest(
	element: Analytics.HTMLElement,
	selector: string
): Analytics.HTMLElement | null {
	if (element.closest) {
		return element.closest(selector);
	}

	if (!document.documentElement.contains(element)) {
		return null;
	}

	do {
		if (element.matches(selector)) {
			return element;
		}

		element = (element.parentElement ||
			element.parentNode) as Analytics.HTMLElement;
	} while (element !== null && element.nodeType === 1);

	return null;
}

/**
 * Return all words from an element
 */
function getNumberOfWords({innerText}: Analytics.HTMLElement) {
	const words = innerText.split(/\s+/).filter(Boolean);

	return innerText !== '' ? words.length : 0;
}

function isTrackable(
	element: Analytics.HTMLElement | Analytics.ObjectEntryHTMLElement | null,
	customDatasetList?: Analytics.DataSetList[]
) {
	const defaultDatasetList = [
		Analytics.DataSetList.AnalyticsAssetId,
		Analytics.DataSetList.AnalyticsAssetTitle,
		Analytics.DataSetList.AnalyticsAssetType,
	];
	const datasetList = customDatasetList || defaultDatasetList;

	return !!(
		element && datasetList.every((dataset) => dataset in element.dataset)
	);
}

/**
 * Whether the element is flagged as a download asset.
 */
function isDownloadAction(
	element: Analytics.HTMLElement | Analytics.ObjectEntryHTMLElement
) {
	return (
		element.dataset?.analyticsAssetAction ===
		Analytics.ElementAction.Download
	);
}

/**
 * Whether the element is flagged as an impression asset.
 */
function isImpressionAction(
	element: Analytics.HTMLElement | Analytics.ObjectEntryHTMLElement
) {
	return (
		element.dataset?.analyticsAssetAction ===
		Analytics.ElementAction.Impression
	);
}

/**
 * Whether the element is flagged as a view asset, which is the default when no
 * asset action is set.
 */
function isViewAction(
	element: Analytics.HTMLElement | Analytics.ObjectEntryHTMLElement
) {
	return (
		!element.dataset?.analyticsAssetAction ||
		element.dataset?.analyticsAssetAction === Analytics.ElementAction.View
	);
}

export {
	closest,
	getClosestAssetElement,
	getNumberOfWords,
	isDownloadAction,
	isImpressionAction,
	isTrackable,
	isViewAction,
	transformAssetTypeToSelector,
};

/**
 * Polyfill for .matches in IE11
 */
if (!Element.prototype.matches) {
	Element.prototype.matches = (Element.prototype as any).msMatchesSelector;
}
