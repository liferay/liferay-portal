/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useCallback, useEffect, useState} from 'react';

export function useUnifiedSidebarVisibility({
	initialVisible,
	onVisibleChange,
	sessionKey,
	stateChangedEventName,
	stateRequestedEventName,
}: {
	initialVisible: boolean;
	onVisibleChange?: (visible: boolean) => void;
	sessionKey: string;
	stateChangedEventName: string;
	stateRequestedEventName: string;
}) {
	const [visible, setVisible] = useState(initialVisible);

	const updateVisible = useCallback(
		(visible: boolean) => {
			setVisible(visible);

			onVisibleChange?.(visible);

			Liferay.fire(stateChangedEventName, {visible});

			Liferay.Util.Session.set(
				sessionKey,
				visible ? 'open' : 'closed'
			).catch(() => {});
		},
		[onVisibleChange, sessionKey, stateChangedEventName]
	);

	useEffect(
		function setupVisibilityRequestHandler() {
			function handleStateRequest({visible}: {visible: boolean}) {
				updateVisible(visible);
			}

			Liferay.on(stateRequestedEventName, handleStateRequest);

			return () =>
				Liferay.detach(stateRequestedEventName, handleStateRequest);
		},
		[stateRequestedEventName, updateVisible]
	);

	return {updateVisible, visible};
}
