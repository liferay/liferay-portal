/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {DropTargetMonitor} from 'react-dnd';

import {DROP_POSITIONS, DropPosition} from '../constants/dropPositions';

export type DropZone = DropPosition | 'group';

export function getDropPosition(
	ref: React.RefObject<HTMLElement>,
	monitor: DropTargetMonitor,
	{canGroup}: {canGroup: boolean}
): DropZone | null {
	const clientOffset = monitor.getClientOffset();

	if (!ref.current || !clientOffset) {
		return null;
	}

	const {height, top} = ref.current.getBoundingClientRect();

	const offsetRatio = (clientOffset.y - top) / height;

	if (!canGroup) {
		return offsetRatio < 0.5 ? DROP_POSITIONS.top : DROP_POSITIONS.bottom;
	}

	const topLimit = 1 / 3;

	if (offsetRatio < topLimit) {
		return DROP_POSITIONS.top;
	}

	const bottomLimit = 2 / 3;

	if (offsetRatio > bottomLimit) {
		return DROP_POSITIONS.bottom;
	}

	return 'group';
}
