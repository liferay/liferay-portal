/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {config} from '../config';
import {PageVersion, Status} from '../types/PageVersion';
import ApiHelper from './ApiHelper';

type PageVersionResponse = Omit<PageVersion, 'status'> & {
	status: Capitalize<Status>;
};

async function deletePageVersion(url: string) {
	return ApiHelper.del(url);
}

async function getPageVersions(signal?: AbortSignal) {
	const {data, error} = await ApiHelper.get<{items: PageVersionResponse[]}>(
		config.pageSpecificationVersionsURL,
		signal
	);

	return {
		data: data && {
			items: data.items.map((item) => ({
				...item,
				status: item.status.toLowerCase() as Status,
			})),
		},
		error,
	};
}

export default {deletePageVersion, getPageVersions};
