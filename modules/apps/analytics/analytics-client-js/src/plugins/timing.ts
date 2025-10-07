/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {MARK_NAVIGATION_START, MARK_VIEW_DURATION} from '../utils/constants';
import {getDuration} from '../utils/performance';

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
 * Sends view duration information on the window unload event
 */
function unload(analytics: Analytics) {
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
 * Plugin function that registers listeners against browser time events
 */
function timing(analytics: Analytics) {
	const onLoad = onload.bind(null, analytics);

	window.addEventListener('load', onLoad);

	const onUnload = unload.bind(null, analytics);

	window.addEventListener('unload', onUnload);

	return () => {
		window.removeEventListener('load', onLoad);
		window.removeEventListener('unload', onUnload);
	};
}

export {timing};
export default timing;
