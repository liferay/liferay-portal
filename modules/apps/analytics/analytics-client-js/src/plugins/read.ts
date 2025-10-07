/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {getNumberOfWords} from '../utils/assets';
import {
	DEBOUNCE,
	READ_CHARS_PER_MIN,
	READ_LOGOGRAPHIC_LANGUAGES,
	READ_MINIMUM_SCROLL_DEPTH,
	READ_TIME_FACTOR,
	READ_WORDS_PER_MIN,
} from '../utils/constants';
import {debounce} from '../utils/debounce';
import {onReady} from '../utils/events';
import {ReadTracker} from '../utils/read';
import {ScrollTracker} from '../utils/scroll';

const MIN_TO_MS = 60000;

/**
 * Get all readable content on the page
 */
function getReadableContent() {
	const mainContent = document.getElementById('main-content');
	const meta = document.querySelector(
		"meta[name='data-analytics-readable-content']"
	);

	if (meta && meta.getAttribute('content') === 'true' && mainContent) {
		return mainContent.innerText;
	}

	return '';
}

/**
 * Get the lang property on documentElement.
 */
function getLang() {
	return document.documentElement.lang;
}

/**
 * Calculates the viewDuration based on total characters.
 */
export function viewDurationByCharacters(content: string) {
	return (
		(content.replace(/\s+/gm, '').length / READ_CHARS_PER_MIN) *
		MIN_TO_MS *
		READ_TIME_FACTOR
	);
}

/**
 * Calculates the viewDuration based on total words.
 */
export function viewDurationByWords(content: string) {
	return (
		(getNumberOfWords({innerText: content} as AnalyticsType.HTMLElement) /
			READ_WORDS_PER_MIN) *
		MIN_TO_MS *
		READ_TIME_FACTOR
	);
}

/**
 * Calculates the viewDuration based on the documentElement language.
 */
export function getExpectedViewDuration(content: string) {
	const language = getLang();

	if (READ_LOGOGRAPHIC_LANGUAGES.has(language)) {
		return viewDurationByCharacters(content);
	}

	return viewDurationByWords(content);
}

/**
 * Sends information when user read a page.
 */
function read(analytics: Analytics) {
	const readTracker = new ReadTracker();
	let scrollTracker = new ScrollTracker();

	const stopTrackingOnReady = onReady(() => {
		const content = getReadableContent();

		readTracker.setExpectedViewDuration(
			() =>
				analytics.send(
					AnalyticsType.EventId.PageRead,
					AnalyticsType.ApplicationId.Page,
					{
						externalReferenceCode:
							analytics._getContext().layoutExternalReferenceCode,
					}
				),
			getExpectedViewDuration(content)
		);
	});

	const onScroll = debounce(() => {
		scrollTracker.onDepthReached((depth) => {
			if (depth >= READ_MINIMUM_SCROLL_DEPTH) {
				readTracker.onDepthReached(() => {
					analytics.send(
						AnalyticsType.EventId.PageRead,
						AnalyticsType.ApplicationId.Page,
						{
							externalReferenceCode:
								analytics._getContext()
									.layoutExternalReferenceCode,
						}
					);
				});
			}
		});
	}, DEBOUNCE);

	document.addEventListener('scroll', onScroll as EventListener);

	return () => {
		stopTrackingOnReady();
		document.removeEventListener('scroll', onScroll as EventListener);
		readTracker.dispose();
		scrollTracker = new ScrollTracker();
	};
}

export {read};
export default read;
