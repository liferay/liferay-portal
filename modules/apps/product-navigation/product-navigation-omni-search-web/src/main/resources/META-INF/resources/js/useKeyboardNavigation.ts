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
	onOpen: () => void
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

	const onInputKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
		if (!navigableItems.length) {
			return;
		}

		if (event.key === 'ArrowDown') {
			event.preventDefault();

			setActiveIndex((index) => (index + 1) % navigableItems.length);
		}
		else if (event.key === 'ArrowUp') {
			event.preventDefault();

			setActiveIndex((index) =>
				index <= 0 ? navigableItems.length - 1 : index - 1
			);
		}
		else if (event.key === 'Enter' && activeIndex >= 0) {
			event.preventDefault();

			navigableItems[activeIndex].onClick();
		}
	};

	return {activeIndex, onInputKeyDown, sectionOffsets};
}

export type {NavigableItem, Section};
