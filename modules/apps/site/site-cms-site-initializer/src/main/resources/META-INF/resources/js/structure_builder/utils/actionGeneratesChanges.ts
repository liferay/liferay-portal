/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Action} from '../contexts/StateContext';

export default function actionGeneratesChanges(actionType: Action['type']) {
	switch (actionType) {
		case 'add-field':
		case 'add-referenced-structures':
		case 'delete-field':
		case 'update-field':
		case 'update-structure':
			return true;
		case 'add-validation-error':
		case 'clear-error':
		case 'create-structure':
		case 'delete-selection':
		case 'publish-structure':
		case 'set-error':
		case 'set-selection':
		case 'validate':
			return false;
		default: {
			const exhaustiveCheck: never = actionType;
			throw new Error(`Unhandled action type case: ${exhaustiveCheck}`);
		}
	}
}
