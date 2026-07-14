/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

export interface Space {
	id: number;
	name: string;
	siteId: number;
}

export async function getSpaces(): Promise<Space[]> {
	const response = await fetch(
		`/o/headless-asset-library/v1.0/asset-libraries?pageSize=-1&filter=${encodeURIComponent(
			"type eq 'Space'"
		)}`,
		{
			headers: new Headers({Accept: 'application/json'}),
		}
	);

	if (!response.ok) {
		throw new Error(`Unable to load spaces: ${response.statusText}`);
	}

	const {items = []} = await response.json();

	return items;
}
