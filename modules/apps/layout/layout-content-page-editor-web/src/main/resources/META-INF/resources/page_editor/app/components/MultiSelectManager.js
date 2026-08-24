/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import {useEffect, useRef} from 'react';

import {
	ARROW_DOWN_KEY_CODE,
	ARROW_UP_KEY_CODE,
	ENTER_KEY_CODE,
	ESCAPE_KEY_CODE,
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
			keyCombination: (event) =>
				event.shiftKey && !event.altKey && !isCtrlOrMeta(event),
			keyboardActivation: (event) =>
				[ARROW_DOWN_KEY_CODE, ARROW_UP_KEY_CODE].includes(event.key),
			type: MULTI_SELECT_TYPES.range,
		},
		simpleMultiSelect: {
			keyCombination: (event) => !event.altKey && isCtrlOrMeta(event),
			keyboardActivation: (event) =>
				event.key === ENTER_KEY_CODE ||
				event.code === SPACE_KEY_CODE ||
				isCtrlOrMeta(event),
			type: MULTI_SELECT_TYPES.simple,
		},
	};

	useEffect(() => {
		const findMultiSelection = (event) =>
			Object.values(keymapRef.current).find((multiSelection) =>
				multiSelection.keyCombination(event)
			);

		const updateMultiSelect = (type) => {
			if (type !== multiSelectType) {
				activateMultiSelect(type);
			}
		};

		const onBlur = () => {
			updateMultiSelect(null);
		};

		const onKeydown = (event) => {
			const multiSelection = findMultiSelection(event);

			updateMultiSelect(
				multiSelection?.keyboardActivation(event)
					? multiSelection.type
					: null
			);

			if (event.key === ESCAPE_KEY_CODE && activeItemIds.length) {
				selectItem(null);
			}
		};

		const onKeyup = (event) => {
			if (findMultiSelection(event)?.type !== multiSelectType) {
				updateMultiSelect(null);
			}
		};

		const onMouse = (event) => {
			updateMultiSelect(findMultiSelection(event)?.type ?? null);
		};

		window.addEventListener('blur', onBlur);
		window.addEventListener('click', onMouse, true);
		window.addEventListener('keydown', onKeydown, true);
		window.addEventListener('keyup', onKeyup, true);
		window.addEventListener('mousedown', onMouse, true);

		return () => {
			window.removeEventListener('blur', onBlur);
			window.removeEventListener('click', onMouse, true);
			window.removeEventListener('keydown', onKeydown, true);
			window.removeEventListener('keyup', onKeyup, true);
			window.removeEventListener('mousedown', onMouse, true);
		};
	}, [activeItemIds, activateMultiSelect, multiSelectType, selectItem]);

	return null;
}
