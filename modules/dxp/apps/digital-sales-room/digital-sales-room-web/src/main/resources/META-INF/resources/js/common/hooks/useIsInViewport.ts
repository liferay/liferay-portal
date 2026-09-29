/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useEffect, useState} from 'react';

export default function useIsInViewport(element: HTMLElement | null) {
	const [visible, setVisible] = useState(false);

	useEffect(() => {
		if (!element) {
			return;
		}

		const observer = new IntersectionObserver(([entry]) => {
			setVisible(entry.isIntersecting);
		});

		observer.observe(element);

		return () => observer.disconnect();
	}, [element]);

	return visible;
}
