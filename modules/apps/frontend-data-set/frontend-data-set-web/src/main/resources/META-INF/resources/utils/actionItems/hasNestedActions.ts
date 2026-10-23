/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {EItemActionsType} from '../types';

/**
 * Returns whether the action renders its `items` as nested actions. Only
 * `group` (a section) and `contextual` (a submenu) actions do, so an action of
 * type `item` keeps rendering as a single action even when it carries `items`.
 */
export default function hasNestedActions<
	T extends {items?: Array<T>; type?: string},
>(action: T): action is T & {items: Array<T>} {
	return (
		(action.type === EItemActionsType.CONTEXTUAL ||
			action.type === EItemActionsType.GROUP) &&
		!!action.items
	);
}
