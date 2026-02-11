/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useLayoutEffect, useState} from 'react';

export function useWindowSize() {
	const [size, setSize] = useState({
		height: 0,
		width: 0,
	});

	useLayoutEffect(() => {
		const handleResize = () => {
			setSize({
				height: window.innerHeight,
				width: window.innerWidth,
			});
		};

		handleResize();
		window.addEventListener('resize', handleResize);

		return () => {
			window.removeEventListener('resize', handleResize);
		};
	}, []);

	return size;
}
