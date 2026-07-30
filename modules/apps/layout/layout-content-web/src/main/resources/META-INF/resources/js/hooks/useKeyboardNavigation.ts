/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useRef, useState} from 'react';

export interface NavigationItemProps {
	onFocus: () => void;
	onKeyDown: (event: React.KeyboardEvent<HTMLElement>) => void;
	ref: (element: HTMLElement | null) => void;
	tabIndex: number;
}

export default function useKeyboardNavigation({
	itemCount,
}: {
	itemCount: number;
}) {
	const [targetIndex, setTargetIndex] = useState(0);

	const elementsRef = useRef<Array<HTMLElement | null>>([]);

	const navigationTargetIndex = Math.min(
		targetIndex,
		Math.max(0, itemCount - 1)
	);

	const getItemProps = (index: number): NavigationItemProps => ({
		onFocus: () => setTargetIndex(index),
		onKeyDown: (event) => {
			if (event.target !== event.currentTarget) {
				return;
			}

			let nextIndex;

			if (event.key === 'ArrowDown') {
				nextIndex = Math.min(index + 1, itemCount - 1);
			}
			else if (event.key === 'ArrowUp') {
				nextIndex = Math.max(index - 1, 0);
			}
			else {
				return;
			}

			event.preventDefault();

			setTargetIndex(nextIndex);

			elementsRef.current[nextIndex]?.focus();
		},
		ref: (element) => {
			elementsRef.current[index] = element;
		},
		tabIndex: index === navigationTargetIndex ? 0 : -1,
	});

	return {getItemProps};
}
