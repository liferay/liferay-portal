/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useRef, useState} from 'react';

export function useUnifiedSidebarAnnouncement(visible: boolean) {
	const shouldAnnounceRef = useRef(false);

	const [announcement, setAnnouncement] = useState('');

	useEffect(() => {
		if (shouldAnnounceRef.current) {
			shouldAnnounceRef.current = false;

			setAnnouncement(
				visible
					? Liferay.Language.get('the-product-menu-is-open')
					: Liferay.Language.get('the-product-menu-is-closed')
			);
		}
		else {
			setAnnouncement('');
		}
	}, [visible]);

	const announceNextChange = useCallback(() => {
		shouldAnnounceRef.current = true;
	}, []);

	return {announceNextChange, announcement};
}
