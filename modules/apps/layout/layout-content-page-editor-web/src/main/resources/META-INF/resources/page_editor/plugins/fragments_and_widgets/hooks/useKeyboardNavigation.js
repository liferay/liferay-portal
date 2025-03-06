/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEventListener} from '@liferay/frontend-js-react-web';
import {useEffect, useState} from 'react';

import {
	ARROW_DOWN_KEY_CODE,
	ARROW_LEFT_KEY_CODE,
	ARROW_RIGHT_KEY_CODE,
	ARROW_UP_KEY_CODE,
} from '../../../app/config/constants/keyboardCodes';
import {LIST_ITEM_TYPES} from '../../../app/config/constants/listItemTypes';

const ALLOWED_KEY_CODES = [
	ARROW_DOWN_KEY_CODE,
	ARROW_LEFT_KEY_CODE,
	ARROW_RIGHT_KEY_CODE,
	ARROW_UP_KEY_CODE,
];

export default function useKeyboardNavigation({handleOpen, type}) {
	const [element, setElement] = useState(null);
	const [isTarget, setIsTarget] = useState(false);

	const rtl =
		Liferay.Language.direction?.[themeDisplay?.getLanguageId()] === 'rtl';

	useEffect(() => {
		const list = element?.closest('[role="menubar"]');
		const listItem = element?.closest('li');

		const isFirstChild = listItem === list?.firstChild;

		setIsTarget(isFirstChild);
	}, [element]);

	useEventListener(
		'keydown',
		(event) => {
			const {code} = event;

			if (!ALLOWED_KEY_CODES.includes(code)) {
				return;
			}

			let nextCode = code;

			if (rtl && code === ARROW_RIGHT_KEY_CODE) {
				nextCode = ARROW_LEFT_KEY_CODE;
			}

			if (rtl && code === ARROW_LEFT_KEY_CODE) {
				nextCode = ARROW_RIGHT_KEY_CODE;
			}

			event.preventDefault();

			if (type === LIST_ITEM_TYPES.header) {
				onHeaderKeyDown(element, nextCode, handleOpen);
			}
			else if (type === LIST_ITEM_TYPES.listItem) {
				onListItemKeyDown(element, nextCode);
			}
		},
		true,
		element
	);

	useEventListener('focus', () => setIsTarget(true), true, element);

	useEventListener(
		'blur',
		(event) => {
			const list = event.target.closest('[role="menubar"]');

			const nextActiveElement = event.relatedTarget;

			if (list.contains(nextActiveElement)) {
				setIsTarget(false);
			}
		},
		true,
		element
	);

	return {isTarget, setElement};
}

function onHeaderKeyDown(element, keyCode, handleOpen) {
	if (keyCode === ARROW_DOWN_KEY_CODE) {

		// Target first item of the list. If it's collapsed, target next header

		const list = element.nextSibling;
		const firstItem = list?.querySelector('li');

		if (firstItem) {
			firstItem.focus();
		}
		else {
			const collapse = element.parentElement;
			const nextCollapse = collapse?.nextSibling;
			const nextHeader = nextCollapse?.querySelector('button');

			nextHeader?.focus();
		}
	}
	else if (keyCode === ARROW_UP_KEY_CODE) {

		// Target last item of the previous list. If it's collapsed, target previous header

		const collapse = element.parentElement;
		const previousCollapse = collapse?.previousSibling;

		if (!previousCollapse) {
			return;
		}

		const previousList = previousCollapse.querySelector('ul');

		if (previousList) {
			const lastItem = previousList.lastChild;

			lastItem.focus();
		}
		else {
			const previousHeader = previousCollapse.querySelector('button');

			previousHeader.focus();
		}
	}
	else if (keyCode === ARROW_RIGHT_KEY_CODE) {

		// Expand

		handleOpen(true);
	}
	else if (keyCode === ARROW_LEFT_KEY_CODE) {

		// Collapse

		handleOpen(false);
	}
}

function onListItemKeyDown(element, keyCode) {
	if (keyCode === ARROW_UP_KEY_CODE) {

		// Target previous list item. If it's the first one, target header

		if (element.previousSibling) {
			element.previousSibling.focus();
		}
		else {
			const collapse = element.closest('.page-editor__collapse');
			const header = collapse?.querySelector('button');

			header?.focus();
		}
	}
	else if (keyCode === ARROW_DOWN_KEY_CODE) {

		// Target next list item. If it's the last one, target next header

		if (element.nextSibling) {
			element.nextSibling.focus();
		}
		else {
			const collapse = element.closest('.page-editor__collapse');
			const nextCollapse = collapse?.nextSibling;
			const nextHeader = nextCollapse?.querySelector('button');

			nextHeader?.focus();
		}
	}
	else if (keyCode === ARROW_RIGHT_KEY_CODE) {

		// If the active element is the list item itself, target first option button

		// If the active element is an option button, target the next one

		if (document.activeElement === element) {
			const firstButton = element.querySelector('button');

			firstButton.focus();
		}
		else {
			const nextButton = document.activeElement.nextSibling;

			nextButton?.focus();
		}
	}
	else if (keyCode === ARROW_LEFT_KEY_CODE) {

		// If the previous element is another button, target it, otherwise target the list item

		const previousSibling = document.activeElement.previousSibling;

		if (previousSibling?.tagName === 'BUTTON') {
			previousSibling.focus();
		}
		else {
			element.focus();
		}
	}
}
