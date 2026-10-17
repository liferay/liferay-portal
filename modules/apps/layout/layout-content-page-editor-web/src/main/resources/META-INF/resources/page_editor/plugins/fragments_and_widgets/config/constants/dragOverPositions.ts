/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export const DRAG_OVER_POSITIONS = {
	bottom: 'bottom',
	top: 'top',
} as const;

export const DRAG_OVER_POSITIONS_LABELS: Record<DragOverPosition, string> = {
	[DRAG_OVER_POSITIONS.bottom]: Liferay.Language.get('bottom'),
	[DRAG_OVER_POSITIONS.top]: Liferay.Language.get('top'),
};

export type DragOverPosition =
	(typeof DRAG_OVER_POSITIONS)[keyof typeof DRAG_OVER_POSITIONS];
