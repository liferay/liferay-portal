/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useContext, useEffect, useState} from 'react';

import SpaceService from '../../../../common/services/SpaceService';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';

export default function useConnectedSpaces() {
	const {space, spaceIds} = useContext(PerformanceContext);

	const [loading, setLoading] = useState(true);
	const [connectedSpaces, setConnectedSpaces] = useState<
		Record<string, boolean>
	>({});

	useEffect(() => {
		async function checkSpaces() {
			const spaces = (await SpaceService.getSpaces()).filter(({id}) =>
				spaceIds.includes(String(id))
			);

			const infos = await Promise.all(
				spaces.map(({siteId}) =>
					PerformanceService.getConnectionInfo({
						depotEntryGroupId: siteId,
					})
				)
			);

			const connectedSpaces: Record<string, boolean> = {};

			spaces.forEach(({id}, index) => {
				connectedSpaces[String(id)] =
					infos[index].data?.connectedToSpace ?? true;
			});

			setConnectedSpaces(connectedSpaces);
			setLoading(false);
		}

		checkSpaces();
	}, [spaceIds]);

	const connectedSpaceIds = Object.keys(connectedSpaces);

	const connected =
		space.value === 'all'
			? !connectedSpaceIds.length ||
				connectedSpaceIds.some((id) => connectedSpaces[id])
			: connectedSpaces[space.value] ?? true;

	return {connected, loading};
}
