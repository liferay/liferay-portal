/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RefObject, useEffect, useState} from 'react';

export function useElementWidth<T extends Element>(
	ref: RefObject<T>
): number | undefined {
	const [width, setWidth] = useState<number | undefined>();

	useEffect(() => {
		const element = ref.current;

		if (!element || !('ResizeObserver' in window)) {
			return;
		}

		setWidth(element.getBoundingClientRect().width);

		const resizeObserver = new ResizeObserver(([entry]) =>
			setWidth(entry.contentRect.width)
		);

		resizeObserver.observe(element);

		return () => resizeObserver.disconnect();
	}, [ref]);

	return width;
}
