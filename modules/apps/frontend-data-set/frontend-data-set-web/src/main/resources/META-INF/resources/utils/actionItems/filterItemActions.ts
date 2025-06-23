/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getLocalizedValue} from '../getLocalizedValue';
import {IItemActionsDataFilter, IItemsActions} from '../types';

const hasPermission = (action: IItemsActions, itemData: any): boolean => {
	if (!action?.data?.permissionKey) {
		return true;
	}

	if (!itemData?.actions) {
		return false;
	}

	const permissionKey = action.data?.permissionKey?.toLowerCase();

	return Object.keys(itemData?.actions).some(
		(itemAction) => itemAction.toLowerCase() === permissionKey
	);
};

const matchesVisibilityFilters = (
	action: IItemsActions,
	itemData: any
): boolean => {
	if (!action?.data?.visibilityFilters) {
		return true;
	}

	const visibilityFilters: IItemActionsDataFilter =
		action?.data?.visibilityFilters;

	return Object.keys(visibilityFilters).every(
		(key: string) =>
			getLocalizedValue(itemData, key)?.value === visibilityFilters[key]
	);
};

const isVisible = (action: IItemsActions, itemData: any): boolean => {
	if (
		!hasPermission(action, itemData) ||
		!matchesVisibilityFilters(action, itemData)
	) {
		return false;
	}

	if (action?.isVisible) {
		return action.isVisible(itemData);
	}

	return true;
};

const transformAction = (
	action: IItemsActions,
	itemData: any
): IItemsActions => {
	if (!action?.data?.permissionKey || action?.target !== 'headless') {
		return action;
	}

	const permissionKey = action?.data?.permissionKey?.toLowerCase();

	const matchedPermissionKeys = Object.keys(itemData?.actions).filter(
		(itemAction) => itemAction.toLowerCase() === permissionKey
	);

	if (!matchedPermissionKeys.length) {
		return action;
	}

	return {
		...action,
		...itemData?.actions[matchedPermissionKeys[0]],
	};
};

const filterItemActions = (
	actions: Array<IItemsActions>,
	itemData: any
): Array<IItemsActions> => {
	return actions
		? actions
				.filter((action: IItemsActions) => isVisible(action, itemData))
				.map((action: IItemsActions) =>
					transformAction(action, itemData)
				)
		: [];
};

export default filterItemActions;
