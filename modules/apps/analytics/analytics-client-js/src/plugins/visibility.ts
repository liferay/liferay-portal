/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';

type ExtendedDoc = Document & {
	msHidden?: boolean;
	webkitHidden?: boolean;
};

let enableTabEvent = true;

/**
 * Handle differents browser versions to Visibility API
 */
function getHiddenKey() {
	let hidden;
	let visibilityChange;

	const extendedDoc: ExtendedDoc = document;

	if (typeof extendedDoc.hidden !== 'undefined') {
		hidden = 'hidden';
		visibilityChange = 'visibilitychange';
	}
	else if (typeof extendedDoc.msHidden !== 'undefined') {
		hidden = 'msHidden';
		visibilityChange = 'msvisibilitychange';
	}
	else if (typeof extendedDoc.webkitHidden !== 'undefined') {
		hidden = 'webkitHidden';
		visibilityChange = 'webkitvisibilitychange';
	}

	return {
		hidden,
		visibilityChange,
	};
}

/**
 * Sends tabBlurred or tabFocused event on visibilityChange
 */
function handleVisibilityChange(analytics: Analytics) {
	const {hidden} = getHiddenKey();

	const extendedDoc: ExtendedDoc = document;

	if (enableTabEvent) {
		if (extendedDoc[hidden as 'hidden' | 'msHidden' | 'webkitHidden']) {
			analytics.send(
				AnalyticsType.EventId.TabBlurred,
				AnalyticsType.ApplicationId.Page
			);
		}
		else {
			analytics.send(
				AnalyticsType.EventId.TabFocused,
				AnalyticsType.ApplicationId.Page
			);
		}
	}
}

/**
 * Plugin function that registers listeners against browser visibility tabs
 */
function visibility(analytics: Analytics) {
	const {visibilityChange} = getHiddenKey();

	if (visibilityChange) {
		const onVisibilityChange = handleVisibilityChange.bind(null, analytics);

		window.addEventListener('beforeunload', enableTabEventHandle);
		window.addEventListener('pageshow', pageShowHandle);
		document.addEventListener(visibilityChange, onVisibilityChange);

		return () => {
			window.removeEventListener('beforeunload', enableTabEventHandle);
			window.removeEventListener('pageshow', pageShowHandle);
			document.removeEventListener(visibilityChange, onVisibilityChange);
		};
	}
}

function enableTabEventHandle() {
	enableTabEvent = false;
}

/**
 * Leaving the page disables the tab events so that the visibility change it
 * causes is not reported. A page restored from the back/forward cache is being
 * viewed again, so the events must be enabled back.
 */
function pageShowHandle(event: PageTransitionEvent) {
	if (event.persisted) {
		enableTabEvent = true;
	}
}

export {visibility};
export default visibility;
