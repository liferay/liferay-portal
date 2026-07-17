/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {DragPreview} from '@liferay/layout-js-components-web';
import React, {useEffect, useMemo} from 'react';

import {useMovementSource, useMovementTarget} from './KeyboardMovementContext';

export default function DragPreviewWrapper() {
	const source = useMovementSource();
	const target = useMovementTarget();

	const alignment = useMemo(() => {
		if (!source || !target.nodeId || !target.position) {
			return undefined;
		}

		const element = document.querySelector<HTMLElement>(
			`[data-keyboard-movement-id="${target.nodeId}"]`
		);

		if (!element) {
			return undefined;
		}

		return {element, position: target.position};
	}, [source, target]);

	useEffect(() => {
		alignment?.element.scrollIntoView?.({
			behavior: 'smooth',
			block: 'nearest',
		});
	}, [alignment]);

	return (
		<DragPreview
			alignment={alignment}
			getIcon={(item: {icon?: string; name?: string}) =>
				source?.icon ?? item?.icon ?? ''
			}
			getLabel={(item: {icon?: string; name?: string}) =>
				source?.name ?? item?.name ?? ''
			}
		/>
	);
}
