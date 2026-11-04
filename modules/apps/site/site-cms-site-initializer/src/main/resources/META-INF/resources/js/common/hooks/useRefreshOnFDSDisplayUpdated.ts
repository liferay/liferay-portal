/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect} from 'react';

import {FDS_EVENT_DISPLAY_UPDATED} from '../utils/constants';

export default function useRefreshOnFDSDisplayUpdated(
	fdsName: string,
	refresh: () => void
) {
	useEffect(() => {
		refresh();

		const handleDisplayUpdated = (event?: {id?: string}) => {
			if (event?.id?.endsWith(fdsName)) {
				refresh();
			}
		};

		Liferay.on(FDS_EVENT_DISPLAY_UPDATED, handleDisplayUpdated);

		return () => {
			Liferay.detach(FDS_EVENT_DISPLAY_UPDATED, handleDisplayUpdated);
		};
	}, [fdsName, refresh]);
}
