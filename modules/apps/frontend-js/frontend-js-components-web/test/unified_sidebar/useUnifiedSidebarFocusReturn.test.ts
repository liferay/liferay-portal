/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import '@testing-library/jest-dom';

import {useUnifiedSidebarFocusReturn} from '../../src/main/resources/META-INF/resources/unified_sidebar/useUnifiedSidebarFocusReturn';

const NAVIGATION_ID = 'navigation';

function advanceFrames(milliseconds: number) {
	act(() => {
		jest.advanceTimersByTime(milliseconds);
	});
}

function renderFocusReturn() {
	const navigation = document.createElement('aside');

	navigation.classList.add('c-slideout-transition');
	navigation.id = NAVIGATION_ID;

	const item = document.createElement('a');

	item.href = '#item';

	navigation.append(item);

	const toggler = document.createElement('button');

	document.body.append(navigation, toggler);

	const {rerender, result} = renderHook(
		({visible}) =>
			useUnifiedSidebarFocusReturn({
				buttonRef: {current: toggler},
				navigationId: NAVIGATION_ID,
				visible,
			}),
		{initialProps: {visible: true}}
	);

	item.focus();

	act(() => {
		result.current(false);
	});

	rerender({visible: false});

	// Hiding the closed panel drops the focus to the body

	item.blur();

	return {navigation, toggler};
}

describe('useUnifiedSidebarFocusReturn', () => {
	beforeEach(() => {
		jest.useFakeTimers();
	});

	afterEach(() => {
		jest.useRealTimers();

		document.body.innerHTML = '';
	});

	it('focuses the toggler once the navigation stops sliding', () => {
		const {navigation, toggler} = renderFocusReturn();

		advanceFrames(100);

		expect(toggler).not.toHaveFocus();

		navigation.classList.remove('c-slideout-transition');

		advanceFrames(100);

		expect(toggler).toHaveFocus();
	});

	it('focuses the toggler when the navigation never stops sliding', () => {
		const {toggler} = renderFocusReturn();

		advanceFrames(600);

		expect(toggler).toHaveFocus();
	});
});
