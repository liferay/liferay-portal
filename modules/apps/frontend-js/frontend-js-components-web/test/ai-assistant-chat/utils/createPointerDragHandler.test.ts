/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import createPointerDragHandler from '../../../src/main/resources/META-INF/resources/ai-assistant-chat/utils/createPointerDragHandler';

function createPointerDownEvent(button = 0): React.PointerEvent {
	return {
		button,
		clientX: 0,
		clientY: 0,
	} as React.PointerEvent;
}

describe('createPointerDragHandler', () => {
	it('ignores presses from a non-primary button', () => {
		const onDelta = jest.fn();
		const handler = createPointerDragHandler(onDelta);

		handler(createPointerDownEvent(1));

		document.dispatchEvent(
			new MouseEvent('pointermove', {clientX: 10, clientY: 10})
		);

		expect(onDelta).not.toHaveBeenCalled();
	});

	it('captures the pointer so the drag survives leaving the window', () => {
		const setPointerCapture = jest.fn();
		const handler = createPointerDragHandler(jest.fn());

		handler({
			button: 0,
			clientX: 0,
			clientY: 0,
			currentTarget: {setPointerCapture},
			pointerId: 7,
		} as unknown as React.PointerEvent);

		expect(setPointerCapture).toHaveBeenCalledWith(7);

		document.dispatchEvent(new Event('pointerup'));
	});

	it('reports the delta between successive pointer moves', () => {
		const onDelta = jest.fn();
		const handler = createPointerDragHandler(onDelta);

		handler(createPointerDownEvent());

		document.dispatchEvent(
			new MouseEvent('pointermove', {clientX: 10, clientY: 5})
		);

		expect(onDelta).toHaveBeenCalledWith(10, 5);

		document.dispatchEvent(
			new MouseEvent('pointermove', {clientX: 16, clientY: 5})
		);

		expect(onDelta).toHaveBeenLastCalledWith(6, 0);

		document.dispatchEvent(new Event('pointerup'));
	});

	it('stops tracking moves on pointerup', () => {
		const onDelta = jest.fn();
		const handler = createPointerDragHandler(onDelta);

		handler(createPointerDownEvent());

		document.dispatchEvent(new Event('pointerup'));

		document.dispatchEvent(
			new MouseEvent('pointermove', {clientX: 100, clientY: 100})
		);

		expect(onDelta).not.toHaveBeenCalled();
	});

	it('stops tracking moves on pointercancel', () => {
		const onDelta = jest.fn();
		const handler = createPointerDragHandler(onDelta);

		handler(createPointerDownEvent());

		document.dispatchEvent(new Event('pointercancel'));

		document.dispatchEvent(
			new MouseEvent('pointermove', {clientX: 100, clientY: 100})
		);

		expect(onDelta).not.toHaveBeenCalled();
	});
});
