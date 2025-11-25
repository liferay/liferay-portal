/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function deactivateFilter(filter: any) {
	const updatedFilter = JSON.parse(JSON.stringify(filter));

	updatedFilter.active = false;
	updatedFilter.odataFilterString = undefined;
	updatedFilter.selectedData = undefined;

	return updatedFilter;
}
