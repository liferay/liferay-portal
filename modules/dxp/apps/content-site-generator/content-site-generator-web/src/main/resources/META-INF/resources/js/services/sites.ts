/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch as liferayFetch} from 'frontend-js-web';

interface Site {
	friendlyUrlPath?: string;
}

export async function getSiteByExternalReferenceCode(
	externalReferenceCode: string
): Promise<Site | null> {
	const response = await liferayFetch(
		`/o/headless-admin-site/v1.0/sites/by-external-reference-code/${encodeURIComponent(
			externalReferenceCode
		)}`,
		{headers: {Accept: 'application/json'}}
	);

	if (!response.ok) {
		return null;
	}

	return response.json();
}
