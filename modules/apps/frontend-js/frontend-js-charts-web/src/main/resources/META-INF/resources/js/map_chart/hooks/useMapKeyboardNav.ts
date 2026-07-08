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

export function useMapKeyboardNav(validIndexes: number[], focus: FocusHandler) {
	return useCallback(
		(event: React.KeyboardEvent, index: number) => {
			const count = validIndexes.length;

			if (count <= 0) {
				return;
			}

			const currentPosition = validIndexes.indexOf(index);

			if (currentPosition === -1) {
				return;
			}

			if (event.key === 'Home') {
				event.preventDefault();

				focus(validIndexes[0]);

				return;
			}

			if (event.key === 'End') {
				event.preventDefault();

				focus(validIndexes[count - 1]);

				return;
			}

			const offset = getStepOffset(event.key);

			if (offset === 0) {
				return;
			}

			event.preventDefault();

			focus(validIndexes[(currentPosition + offset + count) % count]);
		},
		[validIndexes, focus]
	);
}
