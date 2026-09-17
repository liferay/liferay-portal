/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isCtrlOrMeta} from '@liferay/layout-js-components-web';
import {useEffect, useMemo} from 'react';

import {useCache, useStaleCache} from '../contexts/CacheContext';
import {useSelector, useStateDispatch} from '../contexts/StateContext';
import selectState from '../selectors/selectState';
import findChild from '../utils/findChild';
import handleAddGroup from '../utils/handleAddGroup';
import handleDeleteChildren from '../utils/handleDeleteChildren';
import handlePaste from '../utils/handlePaste';
import handlePublishStructure from '../utils/handlePublishStructure';
import handleSaveStructure from '../utils/handleSaveStructure';
import handleUngroup from '../utils/handleUngroup';
import isCopyable from '../utils/isCopyable';
import isLocked from '../utils/isLocked';
import isReferenced from '../utils/isReferenced';
import isRenamable from '../utils/isRenamable';
import openHelpModal from '../utils/openHelpModal';
import openReferencedStructureModal from '../utils/openReferencedStructureModal';
import {useValidate} from '../utils/validation';

type Combo = string;

type Shortcut = {
	enabled: () => boolean;
	handler: () => void;
};

export default function ShortcutManager() {
	const state = useSelector(selectState);

	const {clipboard, publishedChildren, selection, structure} = state;

	const staleCache = useStaleCache();
	const dispatch = useStateDispatch();
	const validate = useValidate();

	const {data: objectDefinitions, status: objectDefinitionsStatus} =
		useCache('object-definitions');

	const {data: spaces} = useCache('spaces');

	const shortcuts = useMemo(() => {
		const map: Map<Combo, Shortcut> = new Map();

		// Duplicate child

		map.set('Ctrl+D', {
			enabled: () => {
				if (selection.length > 1) {
					return false;
				}

				const [uuid] = selection;

				if (
					isReferenced({root: structure, uuid}) ||
					isLocked({root: structure, uuid})
				) {
					return false;
				}

				return true;
			},
			handler: () =>
				dispatch({type: 'duplicate-children', uuids: [selection[0]]}),
		});

		// Copy children

		const getCopyableUuids = () =>
			selection.filter((uuid) => isCopyable({root: structure, uuid}));

		map.set('Ctrl+C', {
			enabled: () => Boolean(getCopyableUuids().length),
			handler: () =>
				dispatch({type: 'copy-children', uuids: getCopyableUuids()}),
		});

		// Paste children

		map.set('Ctrl+V', {
			enabled: () =>
				Boolean(clipboard?.items.length) && selection.length === 1,
			handler: () => {
				const [targetUuid] = selection;

				if (targetUuid) {
					handlePaste({
						clipboard,
						dispatch,
						structure,
						targetUuid,
					});
				}
			},
		});

		// Rename item

		map.set('Ctrl+Alt+R', {
			enabled: () => {
				if (selection.length > 1) {
					return false;
				}

				const [uuid] = selection;

				if (!isRenamable({structure, uuid})) {
					return false;
				}

				return true;
			},
			handler: () =>
				dispatch({type: 'set-renaming-item-uuid', uuid: selection[0]}),
		});

		// Delete item

		const deleteShortcut: Shortcut = {
			enabled: () => Boolean(selection.length),
			handler: () =>
				handleDeleteChildren({
					dispatch,
					publishedChildren,
					structure,
					uuids: selection,
				}),
		};

		map.set('Backspace', deleteShortcut);
		map.set('Delete', deleteShortcut);

		// Open referenced structures modal

		map.set('Shift+Enter', {
			enabled: () => true,
			handler: () =>
				openReferencedStructureModal({
					dispatch,
					objectDefinitions,
					parentUuid: structure.uuid,
					status: objectDefinitionsStatus,
					structure,
				}),
		});

		// Create repeatable group

		map.set('Ctrl+G', {
			enabled: () => Boolean(selection.length),
			handler: () =>
				handleAddGroup({
					dispatch,
					publishedChildren,
					structure,
					uuids: selection,
				}),
		});

		// Ungroup repeatable group

		map.set('Ctrl+Shift+G', {
			enabled: () => {
				if (selection.length > 1) {
					return false;
				}

				const [uuid] = selection;

				const item = findChild({root: structure, uuid})!;

				if (
					isReferenced({root: structure, uuid}) ||
					item.type !== 'group'
				) {
					return false;
				}

				return true;
			},
			handler: () =>
				handleUngroup({
					dispatch,
					publishedChildren,
					uuid: selection[0],
				}),
		});

		// Save structure

		map.set('Ctrl+S', {
			enabled: () => structure.status !== 'published',
			handler: () =>
				handleSaveStructure({
					dispatch,
					state,
					validate,
				}),
		});

		// Publish structure

		map.set('Ctrl+P', {
			enabled: () => true,
			handler: () =>
				handlePublishStructure({
					dispatch,
					objectDefinitions,
					showExperienceLink: true,
					spaces,
					staleCache,
					state,
					validate,
				}),
		});

		// Open help and shortcuts modal

		map.set('Shift+?', {
			enabled: () => true,
			handler: () => openHelpModal(),
		});

		return map;
	}, [
		clipboard,
		dispatch,
		objectDefinitions,
		objectDefinitionsStatus,
		publishedChildren,
		selection,
		spaces,
		staleCache,
		state,
		structure,
		validate,
	]);

	useEffect(() => {
		const onKeyDown = (event: KeyboardEvent) => {
			if (isInteractiveElement(event)) {
				return;
			}

			const combo = getCombo(event);

			const shortcut = shortcuts.get(combo);

			if (shortcut?.enabled()) {
				event.stopPropagation();
				event.preventDefault();

				shortcut.handler();
			}
		};

		window.addEventListener('keydown', onKeyDown);

		return () => {
			window.removeEventListener('keydown', onKeyDown);
		};
	}, [shortcuts]);

	return null;
}

function getCombo(event: KeyboardEvent): Combo {
	const keys = [];

	if (isCtrlOrMeta(event)) {
		keys.push('Ctrl');
	}
	if (event.altKey) {
		keys.push('Alt');
	}
	if (event.shiftKey) {
		keys.push('Shift');
	}

	if (event.key === '?') {
		keys.push('?');
	}
	else {
		keys.push(event.code.replace('Key', ''));
	}

	return keys.join('+');
}

function isInteractiveElement(event: KeyboardEvent): boolean {
	const target = event.target as HTMLElement | null;

	if (!target) {
		return false;
	}

	if (
		target instanceof HTMLInputElement ||
		target instanceof HTMLTextAreaElement ||
		target instanceof HTMLSelectElement
	) {
		return true;
	}

	if (target.isContentEditable) {
		return true;
	}

	if (target.closest('[contenteditable="true"]')) {
		return true;
	}

	return false;
}
