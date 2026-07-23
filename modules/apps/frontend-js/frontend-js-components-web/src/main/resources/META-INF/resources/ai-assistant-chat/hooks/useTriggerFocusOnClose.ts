/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useIsFirstRender, usePrevious} from '@clayui/shared';
import React, {useEffect} from 'react';

export default function useTriggerFocusOnClose(
	open: boolean,
	triggerRef:
		| React.RefObject<HTMLElement | null>
		| React.MutableRefObject<HTMLElement | null>
) {
	const isFirstRender = useIsFirstRender();
	const previousOpen = usePrevious(open);

	useEffect(() => {
		if (!isFirstRender && previousOpen && !open) {
			triggerRef.current?.focus();
		}
	}, [isFirstRender, previousOpen, open, triggerRef]);
}
