/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useRef, useState} from 'react';

import {Overlay} from '../state/types';

export function useOverlaySelection(
	overlays: Overlay[],
	announce: (message: string) => void
) {
	const [multiSelectedIds, setMultiSelectedIds] = useState<string[]>([]);

	const [layerProportional, setLayerProportional] = useState(false);

	const [selectedOverlayId, setSelectedOverlayId] = useState<string | null>(
		null
	);

	const selectOverlay = (id: string | null) => {
		setSelectedOverlayId(id);

		setMultiSelectedIds((currentIds) =>
			id !== null && currentIds.includes(id) ? currentIds : []
		);
	};

	const toggleMultiSelect = (id: string) => {
		const removing = multiSelectedIds.includes(id);

		const base = multiSelectedIds.length
			? multiSelectedIds
			: selectedOverlayId && selectedOverlayId !== id
				? [selectedOverlayId]
				: [];

		const next = removing
			? multiSelectedIds.filter((candidate) => candidate !== id)
			: [...base, id];

		if (next.length >= 2) {
			setMultiSelectedIds(next);
		}
		else {
			setMultiSelectedIds([]);

			if (multiSelectedIds.length >= 2) {
				announce(
					Liferay.Language.get(
						'the-annotations-are-no-longer-grouped'
					)
				);
			}
		}

		setSelectedOverlayId(id);
	};

	const previousSelectedIdRef = useRef<string | null>(null);

	useEffect(() => {
		if (previousSelectedIdRef.current === selectedOverlayId) {
			return;
		}

		previousSelectedIdRef.current = selectedOverlayId;

		const overlay = overlays.find(
			(candidate) => candidate.id === selectedOverlayId
		);

		setLayerProportional(overlay?.kind === 'image');
	}, [overlays, selectedOverlayId]);

	return {
		layerProportional,
		multiSelectedIds,
		selectOverlay,
		selectedOverlayId,
		setLayerProportional,
		setSelectedOverlayId,
		toggleMultiSelect,
	};
}
