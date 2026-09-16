/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

if (!globalThis.URL.createObjectURL) {
	globalThis.URL.createObjectURL = () => 'blob:jsdom-test';
	globalThis.URL.revokeObjectURL = () => {};
}

if (!globalThis.ResizeObserver) {
	globalThis.ResizeObserver = class {
		disconnect() {}
		observe() {}
		unobserve() {}
	} as unknown as typeof ResizeObserver;
}

HTMLCanvasElement.prototype.getContext = (() =>
	null) as unknown as HTMLCanvasElement['getContext'];

if (!globalThis.PointerEvent) {

	// jsdom has no PointerEvent; without it fireEvent.pointerDown builds a
	// plain Event and the coordinates and modifier keys silently vanish.

	globalThis.PointerEvent = class PointerEvent extends MouseEvent {
		pointerId: number;
		pointerType: string;

		constructor(type: string, params: PointerEventInit = {}) {
			super(type, params);

			this.pointerId = params.pointerId ?? 0;
			this.pointerType = params.pointerType ?? 'mouse';
		}
	} as unknown as typeof globalThis.PointerEvent;
}
