/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ICreationActionItem} from '../types';
import hasNestedActions from './hasNestedActions';

const filterCreationActions = ({
	customActions,
	globalCollectionActions,
}: {
	customActions: Array<ICreationActionItem>;
	globalCollectionActions: any;
}): Array<ICreationActionItem> => {
	return customActions.flatMap((action: ICreationActionItem) => {
		if (!hasPermission(action, globalCollectionActions)) {
			return [];
		}

		if (hasNestedActions(action)) {
			const items = filterCreationActions({
				customActions: action.items,
				globalCollectionActions,
			});

			return items.length ? [{...action, items}] : [];
		}

		return [action];
	});
};

function hasPermission(
	action: ICreationActionItem,
	globalCollectionActions: any
): boolean {
	const permissionKey = action.data?.permissionKey;

	if (!permissionKey) {
		return true;
	}

	return (
		!!globalCollectionActions &&
		Object.keys(globalCollectionActions).some(
			(globalAction) =>
				globalAction.toLowerCase() === permissionKey.toLowerCase()
		)
	);
}

export default filterCreationActions;
