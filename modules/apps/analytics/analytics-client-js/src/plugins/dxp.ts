/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Analytics from '../analytics';
import {Analytics as AnalyticsType} from '../types';
import {
	MARK_LOAD_EVENT_START,
	MARK_NAVIGATION_START,
	MARK_PAGE_LOAD_TIME,
	MARK_VIEW_DURATION,
	PARAM_CONFIGURATION_PORTLET_NAME,
	PARAM_MODE_KEY,
	PARAM_PAGE_EDITOR_PORTLET_NAME,
	PARAM_PORTLET_ID_KEY,
	PARAM_VIEW_MODE,
} from '../utils/constants';
import {getSearchParams} from '../utils/params';
import {createMark, getDuration} from '../utils/performance';

/**
 * Plugin function that registers listeners related to DXP
 */
function dxp(analytics: Analytics) {
	const Liferay = window.Liferay;

	/**
	 * Sends view duration information on the window unload event
	 */
	function sendUnloadEvent() {
		analytics.send(
			AnalyticsType.EventId.PageUnloaded,
			AnalyticsType.ApplicationId.Page,
			{
				externalReferenceCode:
					analytics._getContext().layoutExternalReferenceCode,
				viewDuration: getDuration(
					MARK_VIEW_DURATION,
					MARK_NAVIGATION_START
				),
			}
		);
	}

	/**
	 * Sends page load information on the endNavigate event when SPA is enabled on DXP
	 */
	function sendLoadEvent() {
		analytics.send(
			AnalyticsType.EventId.PageLoaded,
			AnalyticsType.ApplicationId.Page,
			{
				externalReferenceCode:
					analytics._getContext().layoutExternalReferenceCode,
				pageLoadTime: getDuration(
					MARK_PAGE_LOAD_TIME,
					MARK_LOAD_EVENT_START,
					MARK_NAVIGATION_START
				),
			}
		);
	}

	/**
	 * Checks based on the URL param if it is a configuration portlet
	 */
	function isConfigurationPortlet(searchParams: URLSearchParams) {
		const portletId = searchParams.get(PARAM_PORTLET_ID_KEY);

		return portletId === PARAM_CONFIGURATION_PORTLET_NAME;
	}

	/**
	 * Checks based on the URL param if it is a configuration portlet
	 */
	function isPageEditorPortlet(searchParams: URLSearchParams) {
		const portletId = searchParams.get(PARAM_PORTLET_ID_KEY);

		return portletId === PARAM_PAGE_EDITOR_PORTLET_NAME;
	}

	/**
	 * Checks based on the URL param if the page is in view mode
	 */
	function isViewMode(searchParams: URLSearchParams) {
		const mode = searchParams.get(PARAM_MODE_KEY) || PARAM_VIEW_MODE;

		return mode === PARAM_VIEW_MODE;
	}

	if (Liferay) {
		const searchParams = getSearchParams();

		if (
			isConfigurationPortlet(searchParams) ||
			isPageEditorPortlet(searchParams) ||
			!isViewMode(searchParams)
		) {
			return analytics._disposeInternal();
		}

		if (Liferay.SPA) {
			const loadingStartMarks = window.performance.getEntriesByName(
				MARK_LOAD_EVENT_START
			);

			createMark(MARK_NAVIGATION_START);

			if (!loadingStartMarks.length) {
				const createLoadMark = createMark.bind(
					null,
					MARK_LOAD_EVENT_START
				);

				createMark(MARK_LOAD_EVENT_START);
				Liferay?.on?.('beforeNavigate', createLoadMark);
			}

			if (document.readyState === 'complete') {
				sendLoadEvent();
			}

			Liferay?.once?.('beforeNavigate', sendUnloadEvent);
		}
	}
}

export {dxp};
export default dxp;
