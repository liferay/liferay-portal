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
	const updatedFilter = JSON.parse(JSON.stringify(filter));
	updatedFilter.active = true;

	if (selectedData) {
		updatedFilter.selectedData = selectedData;
	}

	const filterType: keyof typeof FILTER_IMPLEMENTATIONS = updatedFilter.type;

	const filterImplementation = FILTER_IMPLEMENTATIONS[filterType];

	updatedFilter.odataFilterString =
		filterImplementation.getOdataString(updatedFilter);
	updatedFilter.selectedItemsLabel =
		filterImplementation.getSelectedItemsLabel(updatedFilter);

	return updatedFilter;
}
