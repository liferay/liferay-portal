/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FILTER_IMPLEMENTATIONS} from '../../management_bar/controls/filters/Filter';
import {IConnectedFDSState} from '../connection/types';
import {IBaseFilterState} from '../types';

/**
 * The OData expressions the data set sends along with the request, one per
 * filter in play.
 *
 * A connection that has taken over the filtering owns the whole
 * expression: the configured filters are then informative only, and the
 * consumer applies the ones it wants to obey.
 *
 * What says the filtering was taken over is the claim of its owner. A
 * consumer that owns the filtering and has applied nothing filters by
 * nothing, which is not the same as leaving the filtering to the data set.
 */
export function getOdataFiltersStrings(
	fdsState: IConnectedFDSState
): Array<string> {
	if (fdsState.filteringOwnerAppId) {
		return (fdsState.connectionFilters ?? [])
			.map(({odataFilterString}) => odataFilterString)
			.filter(Boolean);
	}

	const activeFilters: Array<IBaseFilterState> =
		fdsState.filters.filter((filter) => filter.active) || [];

	return activeFilters.map((filter) => {
		const filterImplementation = FILTER_IMPLEMENTATIONS[filter.type];

		return filterImplementation.getOdataString(filter);
	});
}
