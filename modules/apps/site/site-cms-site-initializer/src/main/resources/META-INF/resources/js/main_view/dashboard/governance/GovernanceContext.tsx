/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {createContext, useEffect, useMemo, useState} from 'react';

import {SpaceOption, initialSpace} from '../common/SpacePicker';
import GovernanceService, {AssetStatistics} from './GovernanceService';

type State = {
	loadingStatistics: boolean;
	setSpace: (space: SpaceOption) => void;
	space: SpaceOption;
	statistics?: AssetStatistics;
};

const GovernanceContext = createContext<State>({
	loadingStatistics: true,
	setSpace: () => {},
	space: initialSpace,
});

GovernanceContext.displayName = 'GovernanceContext';

function GovernanceContextProvider({children}: {children: React.ReactNode}) {
	const [space, setSpace] = useState<SpaceOption>(initialSpace);
	const [statistics, setStatistics] = useState<AssetStatistics>();
	const [loadingStatistics, setLoadingStatistics] = useState(true);

	useEffect(() => {
		const controller = new AbortController();

		async function fetchStatistics() {
			setLoadingStatistics(true);

			const {data} = await GovernanceService.getAssetStatistics(
				space.value === 'all' ? undefined : space.value,
				controller.signal
			);

			if (!controller.signal.aborted) {
				setStatistics(data ?? undefined);
				setLoadingStatistics(false);
			}
		}

		fetchStatistics();

		return () => controller.abort();
	}, [space]);

	const value = useMemo(
		() => ({loadingStatistics, setSpace, space, statistics}),
		[loadingStatistics, space, statistics]
	);

	return (
		<GovernanceContext.Provider value={value}>
			{children}
		</GovernanceContext.Provider>
	);
}

export {GovernanceContext, GovernanceContextProvider};
