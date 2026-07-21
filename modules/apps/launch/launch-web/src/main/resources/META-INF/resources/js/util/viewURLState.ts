/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type View =
	| {type: 'landing'}
	| {type: 'new'}
	| {launchId: number; type: 'details'};

const PARAM_LAUNCH_ID = 'p_r_p_launchId';
const PARAM_LAUNCH_VIEW = 'p_r_p_launchView';

export function readViewFromURL(): View {
	const url = new URL(window.location.href);
	const launchView = url.searchParams.get(PARAM_LAUNCH_VIEW);

	if (launchView === 'new') {
		return {type: 'new'};
	}

	if (launchView === 'details') {
		const launchId = Number(url.searchParams.get(PARAM_LAUNCH_ID));

		if (launchId) {
			return {launchId, type: 'details'};
		}
	}

	return {type: 'landing'};
}

export function pushViewToURL(view: View): void {
	writeViewToURL(view, true);
}

export function replaceViewInURL(view: View): void {
	writeViewToURL(view, false);
}

function writeViewToURL(view: View, push: boolean): void {
	const url = new URL(window.location.href);

	url.searchParams.delete(PARAM_LAUNCH_ID);
	url.searchParams.delete(PARAM_LAUNCH_VIEW);

	if (view.type === 'details') {
		url.searchParams.set(PARAM_LAUNCH_ID, String(view.launchId));
		url.searchParams.set(PARAM_LAUNCH_VIEW, 'details');
	}
	else if (view.type === 'new') {
		url.searchParams.set(PARAM_LAUNCH_VIEW, 'new');
	}

	if (push) {
		window.history.pushState(null, '', url.href);
	}
	else {
		window.history.replaceState(null, '', url.href);
	}
}
