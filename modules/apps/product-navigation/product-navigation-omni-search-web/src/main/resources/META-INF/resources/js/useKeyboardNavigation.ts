/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEventListener} from '@liferay/frontend-js-react-web';
import React, {useEffect, useMemo, useState} from 'react';

type NavigableItem = {
	description?: string;
	icon: string;
	key: string;
	onClick: () => void;
	onDelete?: () => void;
	title: string;
};

type Section = {
	icon: string;
	items: NavigableItem[];
	key: string;
	label: string;
};

export default function useKeyboardNavigation(
	sections: Section[],
	onOpen: () => void,
	inputRef: React.RefObject<HTMLInputElement>
) {
	const [activeIndex, setActiveIndex] = useState<number>(-1);

	useEventListener(
		'keydown',
		(event) => {
			const {ctrlKey, key, metaKey} = event as KeyboardEvent;

			if ((ctrlKey || metaKey) && key.toLowerCase() === 'k') {
				event.preventDefault();

				onOpen();
			}
		},
		true,
		document
	);

	const navigableItems: NavigableItem[] = useMemo(
		() => sections.flatMap((section) => section.items),
		[sections]
	);

	useEffect(() => {
		setActiveIndex(-1);
	}, [navigableItems]);

	useEffect(() => {
		if (activeIndex >= 0) {
			const activeRow = document.getElementById(
				`omniSearchOption${activeIndex}`
			);

			activeRow?.scrollIntoView({block: 'nearest'});
		}
	}, [activeIndex]);

	const sectionOffsets = useMemo(
		() =>
			sections.map((_, index) =>
				sections
					.slice(0, index)
					.reduce((sum, section) => sum + section.items.length, 0)
			),
		[sections]
	);

	const stepIndex = (delta: 1 | -1) =>
		setActiveIndex((index) =>
			delta === 1
				? (index + 1) % navigableItems.length
				: index <= 0
					? navigableItems.length - 1
					: index - 1
		);

	const onInputKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
		if (!navigableItems.length) {
			return;
		}

		if (event.key === 'ArrowDown') {
			event.preventDefault();

			stepIndex(1);
		}
		else if (event.key === 'ArrowUp') {
			event.preventDefault();

			stepIndex(-1);
		}
		else if (event.key === 'Enter' && activeIndex >= 0) {
			event.preventDefault();

			navigableItems[activeIndex].onClick();
		}
		else if (event.key === 'Tab' && !event.shiftKey && activeIndex >= 0) {
			if (navigableItems[activeIndex].onDelete) {
				const activeRow = document.getElementById(
					`omniSearchOption${activeIndex}`
				);

				const deleteButton =
					activeRow?.parentElement?.querySelector<HTMLButtonElement>(
						'.omni-search-result-delete'
					);

				if (deleteButton) {
					event.preventDefault();

					requestAnimationFrame(() => deleteButton.focus());
				}
			}
		}
	};

	const onDeleteKeyDown = (event: React.KeyboardEvent<HTMLButtonElement>) => {
		if (event.key === 'ArrowDown') {
			event.preventDefault();

			stepIndex(1);

			inputRef.current?.focus();
		}
		else if (event.key === 'ArrowUp') {
			event.preventDefault();

			stepIndex(-1);

			inputRef.current?.focus();
		}
	};

	return {activeIndex, onDeleteKeyDown, onInputKeyDown, sectionOffsets};
}

export type {NavigableItem, Section};
