/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import {useEffect, useRef} from 'react';

import {
	ARROW_DOWN_KEY_CODE,
	ARROW_UP_KEY_CODE,
	CONTROL_KEY_CODE,
	ENTER_KEY_CODE,
	ESCAPE_KEY_CODE,
	META_KEY_CODE,
	SHIFT_KEY_CODE,
	SPACE_KEY_CODE,
} from '../config/constants/keyboardCodes';
import {MULTI_SELECT_TYPES} from '../config/constants/multiSelectTypes';
import {
	useActivateMultiSelect,
	useActiveItemIds,
	useMultiSelectType,
	useSelectItem,
} from '../contexts/ControlsContext';

export default function MultiSelectManager() {
	const activeItemIds = useActiveItemIds();
	const activateMultiSelect = useActivateMultiSelect();
	const keymapRef = useRef(null);
	const multiSelectType = useMultiSelectType();
	const selectItem = useSelectItem();

	keymapRef.current = {
		rangeMultiSelect: {
			action: () => {
				activateMultiSelect(MULTI_SELECT_TYPES.range);
			},
			disableKeyCombination: (event) => event.key === SHIFT_KEY_CODE,
			keyCombination: (event) => event.shiftKey && !isCtrlOrMeta(event),
			keyboardActivation: (event) =>
				[ARROW_DOWN_KEY_CODE, ARROW_UP_KEY_CODE].includes(event.key),
		},
		simpleMultiSelect: {
			action: () => {
				activateMultiSelect(MULTI_SELECT_TYPES.simple);
			},
			disableKeyCombination: (event) =>
				event.key === CONTROL_KEY_CODE ||
				event.key === META_KEY_CODE ||
				isCtrlOrMeta(event),
			keyCombination: (event) => isCtrlOrMeta(event),
			keyboardActivation: (event) =>
				event.key === ENTER_KEY_CODE ||
				event.key === SPACE_KEY_CODE ||
				isCtrlOrMeta(event),
		},
	};

	useEffect(() => {
		const onClick = (event) => {
			const multiSelection = Object.values(keymapRef.current).find(
				(multiSelection) =>
					!multiSelectType && multiSelection.keyCombination(event)
			);

			if (multiSelection) {
				multiSelection.action(event);
			}
		};

		const onKeydown = (event) => {
			const multiSelection = Object.values(keymapRef.current).find(
				(multiSelection) =>
					!multiSelectType && multiSelection.keyCombination(event)
			);

			if (multiSelection && multiSelection.keyboardActivation(event)) {
				multiSelection.action(event);
			}

			if (event.key === ESCAPE_KEY_CODE && activeItemIds.length) {
				selectItem(null);
			}
		};

		const onKeyup = (event) => {
			const multiSelection = Object.values(keymapRef.current).find(
				(multiSelection) =>
					multiSelectType &&
					multiSelection.disableKeyCombination(event)
			);

			if (multiSelection) {
				activateMultiSelect(null);
			}
		};

		window.addEventListener('click', onClick, true);
		window.addEventListener('keydown', onKeydown, true);
		window.addEventListener('keyup', onKeyup, true);

		return () => {
			window.removeEventListener('click', onClick, true);
			window.removeEventListener('keydown', onKeydown, true);
			window.removeEventListener('keyup', onKeyup, true);
		};
	}, [activeItemIds, activateMultiSelect, multiSelectType, selectItem]);

	return null;
}
