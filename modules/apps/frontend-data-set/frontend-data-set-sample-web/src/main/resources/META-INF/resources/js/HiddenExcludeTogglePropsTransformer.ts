/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import type {ISelectionFilterState} from '@liferay/frontend-data-set-web';

export default function propsTransformer({filters, ...otherProps}: any) {
	return {
		...otherProps,
		filters: filters.map((filter: ISelectionFilterState) => {
			if (filter.id !== 'color') {
				return filter;
			}

			return {
				...filter,
				preloadedData: {
					exclude: true,
					selectedItems: [{label: 'Blue', value: 'Blue'}],
				},
				showExcludeToggle: false,
			};
		}),
	};
}
