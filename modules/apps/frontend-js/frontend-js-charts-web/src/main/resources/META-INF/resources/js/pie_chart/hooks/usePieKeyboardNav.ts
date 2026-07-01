/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback} from 'react';

type FocusHandler = (index: number) => void;

export function usePieKeyboardNav(count: number, focus: FocusHandler) {
	return useCallback(
		(event: React.KeyboardEvent, index: number) => {
			if (count <= 0) {
				return;
			}

			if (event.key === 'ArrowRight' || event.key === 'ArrowDown') {
				event.preventDefault();

				focus((index + 1) % count);
			}
			else if (event.key === 'ArrowLeft' || event.key === 'ArrowUp') {
				event.preventDefault();

				focus((index - 1 + count) % count);
			}
			else if (event.key === 'Home') {
				event.preventDefault();

				focus(0);
			}
			else if (event.key === 'End') {
				event.preventDefault();

				focus(count - 1);
			}
		},
		[count, focus]
	);
}
