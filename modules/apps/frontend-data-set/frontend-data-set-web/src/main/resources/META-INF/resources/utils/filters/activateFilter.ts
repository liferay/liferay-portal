/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FILTER_IMPLEMENTATIONS} from '../../management_bar/controls/filters/Filter';

export function activateFilter({
	filter,
	selectedData,
}: {
	filter: any;
	selectedData?: any;
}) {
	filter.active = true;

	if (selectedData) {
		filter.selectedData = selectedData;
	}

	const filterType: keyof typeof FILTER_IMPLEMENTATIONS = filter.type;

	const filterImplementation = FILTER_IMPLEMENTATIONS[filterType];

	filter.odataFilterString = filterImplementation.getOdataString(filter);
	filter.selectedItemsLabel =
		filterImplementation.getSelectedItemsLabel(filter);

	return filter;
}
