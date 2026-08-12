/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Action} from '../contexts/StateContext';

export default function actionGeneratesChanges(actionType: Action['type']) {
	switch (actionType) {
		case 'add-field':
		case 'add-referenced-structures':
		case 'add-related-content':
		case 'add-repeatable-group':
		case 'delete-children':
		case 'duplicate-children':
		case 'move-children':
		case 'paste':
		case 'rename-item':
		case 'set-workflow':
		case 'ungroup':
		case 'update-field':
		case 'update-repeatable-group':
		case 'update-related-content':
		case 'update-structure':
			return true;
		case 'add-error':
		case 'clear-errors':
		case 'copy-children':
		case 'create-structure':
		case 'end-operation':
		case 'publish-structure':
		case 'refresh-referenced-structures':
		case 'save-structure':
		case 'set-renaming-item-uuid':
		case 'set-selection':
		case 'start-operation':
		case 'validate':
			return false;
		default: {
			const exhaustiveCheck: never = actionType;
			throw new Error(`Unhandled action type case: ${exhaustiveCheck}`);
		}
	}
}
