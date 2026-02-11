/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEventListener} from '@liferay/frontend-js-react-web';
import {useEffect, useState} from 'react';

type KeyboardKey = 'ArrowDown' | 'ArrowUp';

export default function useKeyboardNavigation() {
	const [element, setElement] = useState<HTMLElement | null>(null);
	const [isTarget, setIsTarget] = useState<boolean>(false);

	useEffect(() => {
		const menu = element?.closest('.applications-menu-nav-columns');
		const wrapper = element?.closest('div');
		const list = element?.closest('ul');
		const listItem = element?.closest('li');

		if (!menu || !wrapper || !list || !listItem) {
			return;
		}

		const isFirstChild =
			listItem === list.firstChild && wrapper === menu.firstChild;

		setIsTarget(!!isFirstChild);
	}, [element]);

	useEventListener(
		'keydown',
		(event) => {
			const key = (<KeyboardEvent>event).key as KeyboardKey;

			if ((key !== 'ArrowDown' && key !== 'ArrowUp') || !element) {
				return;
			}

			event.preventDefault();

			onListItemKeyDown(element, key);
		},
		true,
		element as Node
	);

	useEventListener('focus', () => setIsTarget(true), true, element as Node);

	useEventListener(
		'blur',
		(event) => {
			const keyboardEvent =
				event as unknown as React.FocusEvent<HTMLElement>;

			const menu = keyboardEvent.target?.closest(
				'.applications-menu-nav-columns'
			);

			const nextActiveElement = <HTMLElement>keyboardEvent.relatedTarget;

			if (menu && menu.contains(nextActiveElement)) {
				setIsTarget(false);
			}
		},
		true,
		element as Node
	);

	return {isTarget, setElement};
}

function onListItemKeyDown(element: HTMLElement, keyCode: KeyboardKey) {
	if (keyCode === 'ArrowUp') {
		const listItem = element?.closest('li');

		if (listItem?.previousSibling) {
			const item = listItem.previousSibling.firstChild as HTMLElement;

			item.focus();
		}
		else {
			const wrapper = element.closest('div');

			if (wrapper?.previousSibling) {
				const previousWrapper = wrapper.previousSibling as HTMLElement;

				const list = previousWrapper.querySelector('ul');

				const lastItem = list?.lastChild?.firstChild as HTMLElement;

				lastItem?.focus();
			}
		}
	}
	else if (keyCode === 'ArrowDown') {
		const listItem = element?.closest('li');

		if (listItem?.nextSibling) {
			const item = listItem.nextSibling.firstChild as HTMLElement;

			item.focus();
		}
		else {
			const wrapper = element.closest('div');

			if (wrapper?.nextSibling) {
				const nextWrapper = wrapper.nextSibling as HTMLElement;

				const firstItem = nextWrapper.querySelector('a');

				firstItem?.focus();
			}
		}
	}
}
