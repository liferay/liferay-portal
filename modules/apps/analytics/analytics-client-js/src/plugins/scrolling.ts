/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {DEBOUNCE} from '../utils/constants';
import {debounce} from '../utils/debounce';
import {ScrollTracker} from '../utils/scroll';

/**
 * Plugin function that registers listener against scroll event
 */
function scrolling(analytics: Analytics) {
	let scrollTracker = new ScrollTracker();

	const onScroll = debounce(() => {
		scrollTracker.onDepthReached((depth) => {
			analytics.send(
				AnalyticsType.EventId.PageDepthReached,
				AnalyticsType.ApplicationId.Page,
				{
					depth,
					externalReferenceCode:
						analytics._getContext().layoutExternalReferenceCode,
				}
			);
		});
	}, DEBOUNCE);

	document.addEventListener('scroll', onScroll as EventListener);

	// Reset levels on SPA-enabled environments

	const onLoad = () => {
		scrollTracker.dispose();
		scrollTracker = new ScrollTracker();
	};

	window.addEventListener('load', onLoad);

	return () => {
		document.removeEventListener('scroll', onScroll as EventListener);
		window.removeEventListener('load', onLoad);
	};
}

export {scrolling};
export default scrolling;
