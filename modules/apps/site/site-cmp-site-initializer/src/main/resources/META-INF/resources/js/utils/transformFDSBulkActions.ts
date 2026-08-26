/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function hasPermission(
	action: any,
	item: any,
	getPermissionKey: (action: any, item: any) => string | undefined
): boolean {
	const permissionKey = getPermissionKey(action, item)?.toLowerCase();

	if (!permissionKey) {
		return true;
	}

	if (!item?.actions) {
		return false;
	}

	return Object.keys(item.actions).some(
		(itemAction) => itemAction.toLowerCase() === permissionKey
	);
}

export function transformFDSBulkActions(
	bulkActions: any[],
	getPermissionKey: (action: any, item: any) => string | undefined = (
		action
	) => action?.data?.permissionKey
): any[] {
	return bulkActions.map((action) => ({
		...action,
		isVisible: (
			context: {
				allItemsSelectedActive?: boolean;
				selectedItems?: any[];
			} = {}
		) => {
			if (action.isVisible && !action.isVisible(context)) {
				return false;
			}

			if (context.allItemsSelectedActive) {
				return true;
			}

			return (
				context.selectedItems?.every((selectedItem) =>
					hasPermission(action, selectedItem, getPermissionKey)
				) ?? false
			);
		},
	}));
}
