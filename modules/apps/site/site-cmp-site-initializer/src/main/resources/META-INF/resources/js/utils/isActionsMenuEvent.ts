/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {SyntheticEvent} from 'react';

/**
 * Used for preventing clicking a "ClayDropDownWithItems" trigger from bubbling
 * a click to the parent. Add "data-actions-menu" to the trigger button, then
 * skip the parent's handler when this returns "true".
 *
 * A "stopPropagation" on the trigger does not solve this. ClayDropDown clones
 * the trigger and overrides its "onClick" with its own toggle handler, so the
 * trigger's "stopPropagation" is not guaranteed to run. And even when it does,
 * the menu is portaled: a menu item lives outside the parent in the DOM but is
 * still a React descendant, so its click bubbles to the parent's "onClick"
 * through the React tree, which "stopPropagation" on the trigger never sees.
 */
export default function isActionsMenuEvent(event: SyntheticEvent) {
	const target = event.target as HTMLElement;

	return (
		!event.currentTarget.contains(target) ||
		Boolean(target.closest('[data-actions-menu]'))
	);
}
