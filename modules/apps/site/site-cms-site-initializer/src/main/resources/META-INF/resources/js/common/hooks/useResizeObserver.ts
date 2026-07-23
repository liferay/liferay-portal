/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {RefObject, useEffect, useRef} from 'react';

export default function useResizeObserver(
	ref: RefObject<HTMLElement>,
	onResize: (element: HTMLElement, entry?: ResizeObserverEntry) => void
) {
	const onResizeRef = useRef(onResize);

	useEffect(() => {
		onResizeRef.current = onResize;
	}, [onResize]);

	useEffect(() => {
		const element = ref.current;

		if (!element) {
			return;
		}

		const resizeObserver = new ResizeObserver((entries) =>
			onResizeRef.current(element, entries[0])
		);

		resizeObserver.observe(element);

		onResizeRef.current(element);

		return () => resizeObserver.disconnect();
	}, [ref]);
}
