/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {
	createContext,
	useCallback,
	useEffect,
	useMemo,
	useState,
} from 'react';

import {SpaceOption, initialSpace} from '../common/SpacePicker';
import GovernanceService, {AssetStatistics} from './GovernanceService';

type State = {
	groupIds: number[];
	loadingStatistics: boolean;
	reloadStatistics: () => void;
	setSpace: (space: SpaceOption) => void;
	space: SpaceOption;
	spaceIds: string[];
	statistics?: AssetStatistics;
};

const GovernanceContext = createContext<State>({
	groupIds: [],
	loadingStatistics: true,
	reloadStatistics: () => {},
	setSpace: () => {},
	space: initialSpace,
	spaceIds: [],
});

GovernanceContext.displayName = 'GovernanceContext';

function GovernanceContextProvider({
	allSpacesAdministered,
	children,
	spaceGroupIds,
	spaceIds,
}: {
	allSpacesAdministered: boolean;
	children: React.ReactNode;
	spaceGroupIds: string[];
	spaceIds: string[];
}) {
	const [space, setSpace] = useState<SpaceOption>(initialSpace);
	const [statistics, setStatistics] = useState<AssetStatistics>();
	const [loadingStatistics, setLoadingStatistics] = useState(true);
	const [reloadCount, setReloadCount] = useState(0);

	useEffect(() => {
		setLoadingStatistics(true);
	}, [space]);

	useEffect(() => {
		const controller = new AbortController();

		async function fetchStatistics() {
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
	}, [reloadCount, space]);

	const reloadStatistics = useCallback(
		() => setReloadCount((count) => count + 1),
		[]
	);

	const groupIds = useMemo(() => {
		if (space.value !== initialSpace.value) {
			return space.siteId ? [space.siteId] : [];
		}

		if (allSpacesAdministered) {
			return [];
		}

		return spaceGroupIds.map(Number);
	}, [allSpacesAdministered, space, spaceGroupIds]);

	const value = useMemo(
		() => ({
			groupIds,
			loadingStatistics,
			reloadStatistics,
			setSpace,
			space,
			spaceIds,
			statistics,
		}),
		[
			groupIds,
			loadingStatistics,
			reloadStatistics,
			space,
			spaceIds,
			statistics,
		]
	);

	return (
		<GovernanceContext.Provider value={value}>
			{children}
		</GovernanceContext.Provider>
	);
}

export {GovernanceContext, GovernanceContextProvider};
