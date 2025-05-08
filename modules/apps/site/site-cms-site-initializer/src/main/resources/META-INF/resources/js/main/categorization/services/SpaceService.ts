/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../../services/ApiHelper';

async function getSpaces(): Promise<
	{
		id: string;
		name: string;
	}[]
> {
	const {items} = await ApiHelper.get(
		'/o/headless-asset-library/v1.0/asset-libraries'
	);

	return items;
}

export default {
	getSpaces,
};
