/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {MARK_NAVIGATION_START, MARK_VIEW_DURATION} from '../utils/constants';
import {createMark, getDuration} from '../utils/performance';

/**
 * Sends page load information on the window load event
 */
function onload(analytics: Analytics) {
	const perfData = window.performance.timing;

	const pageLoadTime = perfData.loadEventStart - perfData.navigationStart;

	const props = {
		externalReferenceCode:
			analytics._getContext().layoutExternalReferenceCode,
		pageLoadTime,
	};

	analytics.send(
		AnalyticsType.EventId.PageLoaded,
		AnalyticsType.ApplicationId.Page,
		props
	);
}

/**
 * Sends view duration information on the window pagehide event
 */
function pagehide(analytics: Analytics) {
	const navigationStartMark = window.performance.getEntriesByName(
		MARK_NAVIGATION_START
	);
	const navigationStart = navigationStartMark.length
		? MARK_NAVIGATION_START
		: 'navigationStart';

	const duration = getDuration(MARK_VIEW_DURATION, navigationStart);

	const props = {
		externalReferenceCode:
			analytics._getContext().layoutExternalReferenceCode,
		viewDuration: duration,
	};

	analytics.send(
		AnalyticsType.EventId.PageUnloaded,
		AnalyticsType.ApplicationId.Page,
		props
	);
}

/**
 * Restarts the view duration measurement when the page is restored from the
 * back/forward cache, so that the next pagehide event reports the duration of
 * the new view instead of the time the page spent frozen
 */
function pageshow(event: PageTransitionEvent) {
	if (event.persisted) {
		createMark(MARK_NAVIGATION_START);
	}
}

/**
 * Plugin function that registers listeners against browser time events
 */
function timing(analytics: Analytics) {
	const onLoad = onload.bind(null, analytics);

	window.addEventListener('load', onLoad);

	const onPageHide = pagehide.bind(null, analytics);

	window.addEventListener('pagehide', onPageHide);
	window.addEventListener('pageshow', pageshow);

	return () => {
		window.removeEventListener('load', onLoad);
		window.removeEventListener('pagehide', onPageHide);
		window.removeEventListener('pageshow', pageshow);
	};
}

export {timing};
export default timing;
