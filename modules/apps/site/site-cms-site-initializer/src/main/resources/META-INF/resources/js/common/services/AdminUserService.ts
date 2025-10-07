/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Role} from '../types/Role';
import ApiHelper from './ApiHelper';

async function getUserRoles({
	filter,
	page,
	pageSize,
}: {
	filter?: string;
	page?: number;
	pageSize?: number;
}): Promise<{
	items: Role[];
	lastPage: number;
	page: number;
	pageSize: number;
	totalCount: number;
}> {
	const urlParams = new URLSearchParams();

	if (filter) {
		urlParams.set('filter', filter);
	}

	if (page) {
		urlParams.set('page', String(page));
	}

	if (pageSize) {
		urlParams.set('pageSize', String(pageSize));
	}

	urlParams.set(
		'fields',
		['externalReferenceCode', 'id', 'name', 'name_i18n'].join(',')
	);

	const {data, error} = await ApiHelper.get<{
		items: Role[];
		lastPage: number;
		page: number;
		pageSize: number;
		totalCount: number;
	}>(`/o/headless-admin-user/v1.0/roles?${urlParams.toString()}`);

	if (data) {
		return data;
	}

	throw new Error(error);
}

export default {
	getUserRoles,
};
