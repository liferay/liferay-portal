/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';

const BASE_PATH = '/o/headless-cms/v1.0';

async function resetAssetPermission({
	className,
	classPK,
}: {
	className: string;
	classPK: number;
}) {
	return await ApiHelper.post(`${BASE_PATH}/asset-permission/`, {
		className,
		classPK,
		type: 'ResetAssetPermissionAction',
	});
}

export default {
	resetAssetPermission,
};
