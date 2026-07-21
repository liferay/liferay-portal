/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	View,
	pushViewToURL,
	readViewFromURL,
	replaceViewInURL,
} from '../../src/main/resources/META-INF/resources/js/util/viewURLState';

const BASE_URL = `${window.location.origin}/group/control_panel/manage?p_p_id=com_liferay_launch_web_portlet_LaunchPortlet`;

function setURL(url: string): void {
	window.history.replaceState(null, '', url);
}

describe('readViewFromURL', () => {
	it('returns landing when no view param is present', () => {
		setURL(BASE_URL);

		expect(readViewFromURL()).toEqual({type: 'landing'});
	});

	it('returns new when the view param is new', () => {
		setURL(`${BASE_URL}&p_r_p_launchView=new`);

		expect(readViewFromURL()).toEqual({type: 'new'});
	});

	it('returns details with the launchId when both params are present', () => {
		setURL(`${BASE_URL}&p_r_p_launchId=42&p_r_p_launchView=details`);

		expect(readViewFromURL()).toEqual({launchId: 42, type: 'details'});
	});

	it('falls back to landing when launchId is missing', () => {
		setURL(`${BASE_URL}&p_r_p_launchView=details`);

		expect(readViewFromURL()).toEqual({type: 'landing'});
	});

	it('falls back to landing when launchId is not numeric', () => {
		setURL(`${BASE_URL}&p_r_p_launchId=garbage&p_r_p_launchView=details`);

		expect(readViewFromURL()).toEqual({type: 'landing'});
	});
});

describe('pushViewToURL', () => {
	beforeEach(() => {
		setURL(BASE_URL);
	});

	it('pushes a new history entry with the view params', () => {
		const historySpy = jest.spyOn(window.history, 'pushState');

		const view: View = {launchId: 7, type: 'details'};

		pushViewToURL(view);

		expect(historySpy).toHaveBeenCalled();
		expect(readViewFromURL()).toEqual(view);

		historySpy.mockRestore();
	});

	it('preserves unrelated existing query params', () => {
		pushViewToURL({type: 'new'});

		const url = new URL(window.location.href);

		expect(url.searchParams.get('p_p_id')).toBe(
			'com_liferay_launch_web_portlet_LaunchPortlet'
		);
	});

	it('removes the launchId param when returning to landing', () => {
		pushViewToURL({launchId: 7, type: 'details'});
		pushViewToURL({type: 'landing'});

		const url = new URL(window.location.href);

		expect(url.searchParams.has('p_r_p_launchId')).toBe(false);
		expect(url.searchParams.has('p_r_p_launchView')).toBe(false);
	});
});

describe('replaceViewInURL', () => {
	beforeEach(() => {
		setURL(BASE_URL);
	});

	it('replaces the current history entry instead of pushing a new one', () => {
		const historySpy = jest.spyOn(window.history, 'replaceState');

		const view: View = {launchId: 7, type: 'details'};

		replaceViewInURL(view);

		expect(historySpy).toHaveBeenCalled();
		expect(readViewFromURL()).toEqual(view);

		historySpy.mockRestore();
	});
});
