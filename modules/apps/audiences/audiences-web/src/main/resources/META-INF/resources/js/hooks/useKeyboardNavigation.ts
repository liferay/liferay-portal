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

	const rtl =
		Liferay.Language.direction?.[Liferay.ThemeDisplay.getLanguageId()] ===
		'rtl';

	const getItemProps = (index: number): NavigationItemProps => ({
		onFocus: () => setTargetIndex(index),
		onKeyDown: (event) => {
			const target = event.target as HTMLElement;

			const key = getKey(event, rtl);

			const isHorizontalKey = key === 'ArrowLeft' || key === 'ArrowRight';

			if (
				target !== event.currentTarget &&
				ownsArrowKeys(target, isHorizontalKey)
			) {
				return;
			}

			if (isHorizontalKey) {
				const item = event.currentTarget as HTMLElement;

				const focusableElements = Array.from(
					item.querySelectorAll<HTMLElement>(
						'a[href], button:not([disabled]), input, select, textarea'
					)
				);

				const position = focusableElements.indexOf(target);

				event.preventDefault();

				if (key === 'ArrowRight') {
					const nextElement =
						position === -1
							? focusableElements[0]
							: focusableElements[position + 1];

					nextElement?.focus();
				}
				else if (position === 0) {
					item.focus();
				}
				else if (position > 0) {
					focusableElements[position - 1].focus();
				}

				return;
			}

			let nextIndex = null;

			if (key === 'ArrowDown') {
				nextIndex = Math.min(index + 1, itemCount - 1);
			}
			else if (key === 'ArrowUp') {
				nextIndex = Math.max(index - 1, 0);
			}
			else if (key === 'End') {
				nextIndex = itemCount - 1;
			}
			else if (key === 'Home') {
				nextIndex = 0;
			}

			if (nextIndex === null) {
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

function getKey(event: React.KeyboardEvent<HTMLElement>, rtl: boolean) {
	const {key} = event;

	if (!rtl) {
		return key;
	}

	if (key === 'ArrowRight') {
		return 'ArrowLeft';
	}

	if (key === 'ArrowLeft') {
		return 'ArrowRight';
	}

	return key;
}

function ownsArrowKeys(target: HTMLElement, isHorizontalKey: boolean) {
	if (
		target.tagName === 'INPUT' ||
		target.tagName === 'SELECT' ||
		target.tagName === 'TEXTAREA' ||
		target.getAttribute('role') === 'listbox'
	) {
		return true;
	}

	return target.getAttribute('role') === 'combobox' && !isHorizontalKey;
}
