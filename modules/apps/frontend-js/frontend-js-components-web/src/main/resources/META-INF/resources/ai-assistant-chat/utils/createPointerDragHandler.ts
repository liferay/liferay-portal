/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

export default function createPointerDragHandler(
	onDelta: (deltaX: number, deltaY: number) => void
) {
	return (event: React.PointerEvent) => {
		if (event.button !== 0) {
			return;
		}

		event.currentTarget?.setPointerCapture?.(event.pointerId);

		let lastClientX = event.clientX;
		let lastClientY = event.clientY;

		const handlePointerMove = (moveEvent: PointerEvent) => {
			onDelta(
				moveEvent.clientX - lastClientX,
				moveEvent.clientY - lastClientY
			);

			lastClientX = moveEvent.clientX;
			lastClientY = moveEvent.clientY;
		};

		const handlePointerEnd = () => {
			document.removeEventListener('pointermove', handlePointerMove);
			document.removeEventListener('pointerup', handlePointerEnd);
			document.removeEventListener('pointercancel', handlePointerEnd);
		};

		document.addEventListener('pointermove', handlePointerMove);
		document.addEventListener('pointerup', handlePointerEnd);
		document.addEventListener('pointercancel', handlePointerEnd);
	};
}
