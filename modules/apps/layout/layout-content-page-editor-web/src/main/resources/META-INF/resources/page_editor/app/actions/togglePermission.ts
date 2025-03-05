/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {TOGGLE_PERMISSION} from './types';

export type PermissionKey =
	| 'EDIT_SEGMENTS_ENTRY'
	| 'LOCKED_SEGMENTS_EXPERIMENT'
	| 'SWITCH_EDIT_MODE'
	| 'UPDATE'
	| 'UPDATE_LAYOUT_ADVANCED_OPTIONS'
	| 'UPDATE_LAYOUT_BASIC'
	| 'UPDATE_LAYOUT_CONTENT'
	| 'UPDATE_LAYOUT_LIMITED'
	| 'VIEW_MARKETPLACE';

export default function togglePermission(
	key: PermissionKey,
	value: boolean | undefined = undefined
) {
	const action: {
		forceNewValue?: boolean;
		key: PermissionKey;
		type: typeof TOGGLE_PERMISSION;
	} = {
		key,
		type: TOGGLE_PERMISSION,
	};

	if (typeof value === 'boolean') {
		action.forceNewValue = value;
	}

	return action;
}
