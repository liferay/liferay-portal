/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useCallback} from 'react';

type FocusHandler = (index: number) => void;

function getStepOffset(key: string): number {
	if (key === 'ArrowRight' || key === 'ArrowDown') {
		return 1;
	}

	if (key === 'ArrowLeft' || key === 'ArrowUp') {
		return -1;
	}

	return 0;
}

/**
 * Shared arrow-key navigation across a chart's focusable items. `indexes` is
 * the ordered list of navigable data indexes: a contiguous range for charts
 * where every datum is focusable (Bar, Line, Pie) or a sparse subset for
 * charts that skip items without data (Map). Arrows move to the next/previous
 * entry with wraparound; Home/End jump to the first/last.
 */
export function useChartKeyboardNav(indexes: number[], focus: FocusHandler) {
	return useCallback(
		(event: React.KeyboardEvent, index: number) => {
			const count = indexes.length;

			if (count <= 0) {
				return;
			}

			const currentPosition = indexes.indexOf(index);

			if (currentPosition === -1) {
				return;
			}

			if (event.key === 'Home') {
				event.preventDefault();

				focus(indexes[0]);

				return;
			}

			if (event.key === 'End') {
				event.preventDefault();

				focus(indexes[count - 1]);

				return;
			}

			const offset = getStepOffset(event.key);

			if (offset === 0) {
				return;
			}

			event.preventDefault();

			focus(indexes[(currentPosition + offset + count) % count]);
		},
		[indexes, focus]
	);
}
