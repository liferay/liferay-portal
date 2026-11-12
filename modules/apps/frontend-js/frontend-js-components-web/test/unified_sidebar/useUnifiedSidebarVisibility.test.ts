/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, renderHook} from '@testing-library/react';

import {useUnifiedSidebarVisibility} from '../../src/main/resources/META-INF/resources/unified_sidebar/useUnifiedSidebarVisibility';

function renderVisibility(onVisibleChange?: (visible: boolean) => void) {
	return renderHook(() =>
		useUnifiedSidebarVisibility({
			hiddenSessionValue: 'closed',
			initialVisible: true,
			onVisibleChange,
			sessionKey: 'sessionKey',
			stateChangedEventName: 'stateChanged',
			stateRequestedEventName: 'stateRequested',
			visibleSessionValue: 'open',
		})
	);
}

describe('useUnifiedSidebarVisibility', () => {
	const util = Liferay.Util;

	beforeEach(() => {
		jest.clearAllMocks();

		Liferay.Util = {
			...util,
			Session: {
				get: jest.fn(),
				set: jest.fn(() => Promise.resolve()),
			},
		};
	});

	afterEach(() => {
		Liferay.Util = util;
	});

	it('updates the state before saving it', () => {
		(Liferay.Util.Session.set as jest.Mock).mockReturnValueOnce(
			new Promise(() => {})
		);

		const onVisibleChange = jest.fn();

		const {result} = renderVisibility(onVisibleChange);

		act(() => result.current.updateVisible(false));

		expect(result.current.visible).toBe(false);
		expect(onVisibleChange).toHaveBeenCalledWith(false);
		expect(Liferay.fire).toHaveBeenCalledWith('stateChanged', {
			visible: false,
		});
		expect(Liferay.Util.Session.set).toHaveBeenCalledWith(
			'sessionKey',
			'closed'
		);
	});

	it('keeps the state when saving fails', async () => {
		(Liferay.Util.Session.set as jest.Mock).mockReturnValueOnce(
			Promise.reject(new Error())
		);

		const {result} = renderVisibility();

		await act(async () => result.current.updateVisible(false));

		expect(result.current.visible).toBe(false);
	});

	it('answers state requests', () => {
		const {result, unmount} = renderVisibility();

		const [, handleStateRequest] = (
			Liferay.on as jest.Mock
		).mock.calls.find(([eventName]) => eventName === 'stateRequested');

		act(() => handleStateRequest({visible: false}));

		expect(result.current.visible).toBe(false);

		unmount();

		expect(Liferay.detach).toHaveBeenCalledWith(
			'stateRequested',
			handleStateRequest
		);
	});
});
