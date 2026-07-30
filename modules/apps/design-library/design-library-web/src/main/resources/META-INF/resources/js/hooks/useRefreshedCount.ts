/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

/**
 * Keeps a section-header count in sync with its data set(s). The server renders
 * the initial count, but the `fds-update-display` refresh fired after a
 * mutation only re-fetches the list — the FDS then emits `fds-display-updated`
 * (carrying just the data set id), so this re-fetches the count for the matching
 * ids to keep the header from going stale.
 */
export default function useRefreshedCount(
	initialCount: number,
	dataSetIds: string[],
	fetchCount: () => Promise<number>
): number {
	const [count, setCount] = useState(initialCount);

	useEffect(() => {
		const onDisplayUpdated = (event: {id: string}) => {
			if (dataSetIds.includes(event.id)) {
				fetchCount()
					.then(setCount)
					.catch(() => {});
			}
		};

		Liferay.on('fds-display-updated', onDisplayUpdated);

		return () => {
			Liferay.detach('fds-display-updated', onDisplayUpdated);
		};
	}, [dataSetIds, fetchCount]);

	return count;
}
