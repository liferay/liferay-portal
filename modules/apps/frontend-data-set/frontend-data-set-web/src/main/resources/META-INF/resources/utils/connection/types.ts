/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IFDSState} from '../types';

import type {FDSState} from '@liferay/js-api/data-set/connection';

/**
 * The data set state as a connection leaves it, which only the data set
 * reads.
 *
 * What a connection writes is taken from the published contract rather than
 * declared again here, so the two cannot drift. Being readonly there is what
 * this file wants anyway: the members below stay out of `IFDSState` so that
 * no data set code can write them, which makes any attempt a compile error
 * rather than a convention.
 *
 * It lives here, rather than next to `IFDSState`, because the modules that
 * consume `@liferay/frontend-data-set-web` types do not depend on
 * `@liferay/js-api`. That is also why `offeredCustomConfigs` is not picked
 * from the contract: the data set writes it, so it stays declared on
 * `IFDSState`, and typing it there would put this dependency in reach of
 * every one of those modules.
 */
export interface IConnectedFDSState
	extends IFDSState,
		Pick<
			FDSState,
			'appliedCustomConfigs' | 'connectionFilters' | 'filteringOwnerAppId'
		> {}
