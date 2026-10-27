/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getLiveProcess} from './liveProcesses';

export interface ItemAction {
	data?: {
		id?: string;
		visibilityFilters?: Record<string, unknown>;
	};
}

function getItemValue(itemData: unknown, path: string) {
	return path
		.split('.')
		.reduce(
			(value, key) => (value as Record<string, unknown>)?.[key],
			itemData
		);
}

export function toLiveVisibilityAction(action: ItemAction) {
	const {visibilityFilters, ...data} = action.data ?? {};

	if (!visibilityFilters) {
		return action;
	}

	return {
		...action,
		data,
		isVisible: (itemData: {id: number}) =>
			Object.entries(visibilityFilters).every(([key, value]) => {
				const itemValue = getItemValue(
					{...itemData, ...getLiveProcess(itemData.id)},
					key
				);

				if (Array.isArray(value)) {
					return value.includes(itemValue);
				}

				return itemValue === value;
			}),
	};
}
