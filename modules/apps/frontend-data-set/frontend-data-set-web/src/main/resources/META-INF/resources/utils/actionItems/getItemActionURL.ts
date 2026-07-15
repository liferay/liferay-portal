/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IItemsActions} from '../types';
import findAction from './findAction';
import {replaceTokens, rewriteRedirectParams} from './formatActionURL';

/**
 * Resolves the URL of the item action identified by `actionId`: finds the
 * action in `itemsActions`, interpolates its `href` tokens with `item`
 * properties, and rewrites its `redirect` and `backURL` parameters to the
 * current browser location.
 *
 * Custom views and cell renderers that navigate to an action `href`
 * themselves bypass the equivalent handling the Frontend Data Set applies to
 * the actions it renders (see `formatActionURL`), so the server-rendered
 * `redirect` snapshot goes stale and navigating back through it drops any
 * state the client wrote to the URL, such as the active view. Use this helper
 * instead of composing `findAction` and `replaceTokens` by hand.
 *
 * Returns an empty string when the action does not exist or has no `href`.
 */
const getItemActionURL = (
	itemsActions: IItemsActions[],
	actionId: string,
	item: any
): string => {
	const action = findAction(itemsActions, actionId);

	if (!action?.href) {
		return '';
	}

	return rewriteRedirectParams(replaceTokens(action.href, item));
};

export default getItemActionURL;
