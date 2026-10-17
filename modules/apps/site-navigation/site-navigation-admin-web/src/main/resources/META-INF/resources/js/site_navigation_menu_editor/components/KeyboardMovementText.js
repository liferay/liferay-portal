/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ScreenReaderAnnouncer} from '@liferay/layout-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useRef} from 'react';

import {useItems} from '../contexts/ItemsContext';
import {useDragLayer} from '../contexts/KeyboardDndContext';
import getFlatItems from '../utils/getFlatItems';

export function getMovementText(dragLayer, items) {
	const {
		eventKey,
		menuItemTitle,
		menuItemType,
		order,
		parentSiteNavigationMenuItemId,
	} = dragLayer || {};

	if (!menuItemTitle) {
		return '';
	}

	if (eventKey === 'Enter') {
		return sub(
			Liferay.Language.get(
				'use-up-and-down-arrows-to-move-x-and-press-enter-to-place-it-in-desired-position'
			),
			`${menuItemTitle} (${menuItemType})`
		);
	}

	const siblingsItems = items.filter(
		(item) =>
			item.parentSiteNavigationMenuItemId ===
			parentSiteNavigationMenuItemId
	);

	if (!siblingsItems.length) {
		const parent = items.find(
			(item) =>
				item.siteNavigationMenuItemId === parentSiteNavigationMenuItemId
		);

		return sub(Liferay.Language.get('targeting-inside-of-x'), parent.title);
	}

	const sibling = siblingsItems[order - 1];

	if (sibling) {
		return sub(Liferay.Language.get('targeting-x-of-x'), [
			Liferay.Language.get('bottom'),
			sibling.title,
		]);
	}

	return sub(Liferay.Language.get('targeting-x-of-x'), [
		Liferay.Language.get('top'),
		siblingsItems[order]?.title,
	]);
}

export default function KeyboardMovementText() {
	const dragLayer = useDragLayer();

	const items = getFlatItems(useItems());

	const screenReaderAnnouncerRef = useRef();

	const sendMessage = useCallback((message) => {
		const ref = screenReaderAnnouncerRef;

		if (ref.current) {
			ref.current?.sendMessage(message);
		}
	}, []);

	useEffect(() => {
		sendMessage(getMovementText(dragLayer, items));
	}, [dragLayer]); //eslint-disable-line

	return (
		<ScreenReaderAnnouncer
			aria-live="assertive"
			ref={screenReaderAnnouncerRef}
		/>
	);
}
