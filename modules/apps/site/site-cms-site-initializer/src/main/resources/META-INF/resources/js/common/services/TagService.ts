/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Tag} from '../types/Tag';
import ApiHelper from './ApiHelper';

async function createTag({
	groupId,
	name,
}: {
	groupId: number | string;
	name: string;
}) {
	return await ApiHelper.post<Tag>(
		`/o/headless-admin-taxonomy/v1.0/sites/${groupId}/keywords`,
		{name}
	);
}

export default {
	createTag,
};
