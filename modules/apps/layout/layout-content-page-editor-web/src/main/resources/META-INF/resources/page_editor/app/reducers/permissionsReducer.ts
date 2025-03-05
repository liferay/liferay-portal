/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import togglePermission from '../actions/togglePermission';
import {INIT, TOGGLE_PERMISSION} from '../actions/types';

import type {PermissionKey} from '../actions/togglePermission';

export type PermissionsState = Record<PermissionKey, boolean | undefined>;

export const INITIAL_STATE: PermissionsState = {
	EDIT_SEGMENTS_ENTRY: false,
	LOCKED_SEGMENTS_EXPERIMENT: false,
	SWITCH_EDIT_MODE: true,
	UPDATE: true,
	UPDATE_LAYOUT_ADVANCED_OPTIONS: undefined,
	UPDATE_LAYOUT_BASIC: undefined,
	UPDATE_LAYOUT_CONTENT: true,
	UPDATE_LAYOUT_LIMITED: undefined,
	VIEW_MARKETPLACE: false,
};

export default function permissionsReducer(
	state: PermissionsState = INITIAL_STATE,
	action: ReturnType<typeof togglePermission> | {type: typeof INIT}
): PermissionsState {
	switch (action.type) {
		case TOGGLE_PERMISSION:
			return {
				...state,
				[action.key]:
					typeof action.forceNewValue === 'undefined'
						? !state[action.key]
						: action.forceNewValue,
			};
		case INIT:
			return {
				...state,
				SWITCH_EDIT_MODE:
					state.UPDATE ||
					state.UPDATE_LAYOUT_BASIC ||
					state.UPDATE_LAYOUT_LIMITED,
			};

		default:
			return state;
	}
}
