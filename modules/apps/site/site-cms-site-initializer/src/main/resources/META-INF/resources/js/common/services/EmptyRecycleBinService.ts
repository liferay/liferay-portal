/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';

async function emptyRecycleBin() {
	const filter = encodeURIComponent(
		"cmsRoot eq true and (cmsSection eq 'contents' or cmsSection eq 'files') and status eq 8"
	);

	return await ApiHelper.post(
		`/o/headless-cms/v1.0/bulk-action?filter=${filter}&nestedFields=embedded`,
		{
			selectAll: true,
			type: 'DeleteBulkAction',
		}
	);
}

export default {
	emptyRecycleBin,
};
