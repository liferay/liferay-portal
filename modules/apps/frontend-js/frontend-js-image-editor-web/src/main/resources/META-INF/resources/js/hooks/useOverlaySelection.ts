/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useRef, useState} from 'react';

export function useOverlaySelection() {
	const [layerProportional, setLayerProportional] = useState(false);

	const [selectedOverlayId, setSelectedOverlayId] = useState<string | null>(
		null
	);

	const previousSelectedIdRef = useRef<string | null>(null);

	useEffect(() => {
		if (previousSelectedIdRef.current === selectedOverlayId) {
			return;
		}

		previousSelectedIdRef.current = selectedOverlayId;

		setLayerProportional(false);
	}, [selectedOverlayId]);

	return {
		layerProportional,
		selectOverlay: setSelectedOverlayId,
		selectedOverlayId,
		setLayerProportional,
		setSelectedOverlayId,
	};
}
