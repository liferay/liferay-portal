/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFDSState} from '../types';

import type {FDSConnectionFilter} from '@liferay/js-api/data-set';

/**
 * The data set state as a connection leaves it, which only the data set
 * reads.
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
	 * supersedes `filters` as the only contribution to the request, and the
	 * data set stops showing a filter UI.
	 */
	connectionFilters?: ReadonlyArray<FDSConnectionFilter>;
}
