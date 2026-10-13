/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Launch} from '../api/launches';

export default function fillViewLaunchURL(
	viewLaunchURL: string,
	launch: Launch
): string {
	const url = new URL(viewLaunchURL, window.location.origin);

	for (const [key, value] of Array.from(url.searchParams)) {
		if (value === '{id}') {
			url.searchParams.set(key, String(launch.id));
		}
		else if (value === '{name}') {
			url.searchParams.set(key, launch.name);
		}
	}

	return url.toString();
}
