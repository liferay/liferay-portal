/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEventListener} from '@liferay/frontend-js-react-web';

function isTypingTarget(target: EventTarget | undefined) {
	if (!(target instanceof HTMLElement)) {
		return false;
	}

	if (['INPUT', 'SELECT', 'TEXTAREA'].includes(target.tagName)) {
		return true;
	}

	const editable = target.closest('[contenteditable]');

	if (editable) {
		return editable.getAttribute('contenteditable') !== 'false';
	}

	return false;
}

export function useUnifiedSidebarShortcut(
	navigationId: string,
	onShortcut: () => void
) {
	useEventListener(
		'keydown',
		(event) => {
			const {altKey, code, ctrlKey, key, metaKey, repeat, shiftKey} =
				event as KeyboardEvent;

			// Match the key by its position, which still works when Ctrl+Alt
			// types another character, or by its label, which follows layouts
			// that move the M key, such as AZERTY.

			const isMKey = code === 'KeyM' || key.toLowerCase() === 'm';

			const isCtrlAltM = altKey && ctrlKey && isMKey;

			if (!isCtrlAltM || metaKey || repeat || shiftKey) {
				return;
			}

			if (
				isTypingTarget(event.composedPath()[0]) ||
				!document.getElementById(navigationId)
			) {
				return;
			}

			event.preventDefault();

			onShortcut();
		},
		true,
		document
	);
}
