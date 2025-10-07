/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function addOnClickToCreationMenuItems(
	items: {data: {action: string}}[],
	actions: Record<
		string,
		(data: any, additionalProps?: any, loadData?: () => {}) => void
	>
) {
	return items.map((item: {data?: {action?: string}}) => {
		return {
			...item,
			onClick({loadData}: {loadData?: () => {}}) {
				const action = item?.data?.action;

				if (action) {
					actions[action](item.data, loadData);
				}
			},
		};
	});
}
