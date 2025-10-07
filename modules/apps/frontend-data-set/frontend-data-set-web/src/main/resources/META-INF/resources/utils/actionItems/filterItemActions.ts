/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getObjectValueFromPath} from 'frontend-js-web';

import {getLocalizedValue} from '../getLocalizedValue';
import {
	EItemActionsType,
	IItemActionsDataFilter,
	IItemsActions,
} from '../types';
import {ACTION_ITEM_TARGETS} from './constants';

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

const isDisabled = (
	action: IItemsActions,
	infoPanelOpen: boolean,
	itemData: any,
	selectedItem: boolean
): boolean => {
	if (action?.isDisabled) {
		return action.isDisabled(itemData);
	}

	if (
		infoPanelOpen &&
		action.target === ACTION_ITEM_TARGETS.INFO_PANEL &&
		selectedItem
	) {
		return true;
	}

	return false;
};

const isVisible = (
	action: IItemsActions,
	itemData: any,
	selectable: boolean = false
): boolean => {
	if (
		!hasPermission(action, itemData) ||
		!matchesVisibilityFilters(action, itemData) ||
		(action.target === ACTION_ITEM_TARGETS.INFO_PANEL && !selectable)
	) {
		return false;
	}

	if (action?.isVisible) {
		return action.isVisible(itemData);
	}

	return true;
};

const transformAction = ({
	action,
	infoPanelOpen,
	itemData,
	selectedItem,
}: {
	action: IItemsActions;
	infoPanelOpen: boolean;
	itemData: any;
	selectedItem: boolean;
}): IItemsActions => {
	action.disabled = isDisabled(action, infoPanelOpen, itemData, selectedItem);

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

const filterItemActions = ({
	actions,
	infoPanelOpen = false,
	itemData,
	selectable,
	selectedItemsKey,
	selectedItemsValue,
}: {
	actions: Array<IItemsActions>;
	infoPanelOpen?: boolean;
	itemData: any;
	selectable?: boolean;
	selectedItemsKey: string;
	selectedItemsValue?: Array<any>;
}): Array<IItemsActions> => {
	const selectedItem =
		selectedItemsValue?.length === 1 &&
		!!selectedItemsValue?.includes(
			getObjectValueFromPath({object: itemData, path: selectedItemsKey})
		);

	return actions
		? actions
				.filter((action: IItemsActions) =>
					isVisible(action, itemData, selectable)
				)
				.map((action: IItemsActions) => {
					const transformedAction = transformAction({
						action,
						infoPanelOpen,
						itemData,
						selectedItem,
					});

					if (
						action.type === EItemActionsType.GROUP &&
						action.items
					) {
						return {
							...transformedAction,
							items: filterItemActions({
								actions: action.items,
								infoPanelOpen,
								itemData,
								selectable,
								selectedItemsKey,
								selectedItemsValue,
							}),
						};
					}

					return transformedAction;
				})
		: [];
};

export default filterItemActions;
