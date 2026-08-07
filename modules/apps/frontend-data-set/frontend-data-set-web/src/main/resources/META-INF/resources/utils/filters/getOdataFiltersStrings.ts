/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {FILTER_IMPLEMENTATIONS} from '../../management_bar/controls/filters/Filter';
import {IBaseFilterState, IFDSState} from '../types';

import type {FDSConnectionFilter} from '@liferay/js-api/data-set';

/**
 * The data set state as a connection leaves it, which only this module reads.
 *
 * `connectionFilters` stays out of `IFDSState` so that no data set code can
 * write it: keeping it out of the type the data set writes makes any attempt
 * to change it a compile error rather than a convention. Its element type is
 * the published one, so what the data set reads here is exactly what a
 * consumer wrote through the connection.
 *
 * It lives here, rather than next to `IFDSState`, because the modules that
 * consume `@liferay/frontend-data-set-web` types do not depend on
 * `@liferay/js-api`, and this file is internal to the data set.
 */
export interface IConnectedFDSState extends IFDSState {

	/**
	 * Absent while no connection drives the filtering, which is the case
	 * for every data set that has no external consumer. Once present, it
	 * supersedes `filters` as the only contribution to the request.
	 */
	connectionFilters?: ReadonlyArray<FDSConnectionFilter>;
}

/**
 * The OData expressions the data set sends along with the request, one per
 * filter in play.
 *
 * A connection that has taken over the filtering owns the whole
 * expression: the configured filters are then informative only, and the
 * consumer applies the ones it wants to obey.
 */
export function getOdataFiltersStrings(
	fdsState: IConnectedFDSState
): Array<string> {
	if (fdsState.connectionFilters) {
		return fdsState.connectionFilters
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
