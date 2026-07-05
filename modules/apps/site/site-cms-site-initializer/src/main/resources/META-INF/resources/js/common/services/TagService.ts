/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {composeCreateTaskDTO} from '../../main_view/bulk_actions_monitor/util';
import {IBulkActionFDSData} from '../types/BulkActionTask';
import {Tag} from '../types/Tag';
import ApiHelper from './ApiHelper';

async function createTag({
	assetLibraryId,
	cmsGroupId,
	name,
}: {
	assetLibraryId: number | string | null | undefined;
	cmsGroupId: number | string;
	name: string;
}) {
	let requestBody;

	if (assetLibraryId === null || assetLibraryId === undefined) {
		requestBody = {name};
	}
	else {
		requestBody = {
			assetLibraries: [{id: assetLibraryId}],
			name,
		};
	}

	const url = `/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`;

	const {data, error} = await ApiHelper.get<{items: Tag[]}>(
		`${url}?filter=name eq '${name}'`
	);

	if (error) {
		throw new Error(error);
	}

	const tag = data?.items.find((item) => item.name === name);

	if (tag) {
		if (assetLibraryId) {
			return ApiHelper.patch<Tag>(requestBody, url);
		}

		return {data: tag, error: null, status: null};
	}

	return ApiHelper.post<Tag>(url, requestBody);
}

async function getTags(siteId: number | string) {
	return ApiHelper.get<{
		items: {assetLibraries: {id: number}[]; id: string; name: string}[];
	}>(`/o/headless-admin-taxonomy/v1.0/sites/${siteId}/keywords`);
}

async function getCommonTags(selectedData: IBulkActionFDSData) {
	return await ApiHelper.post<any>(
		`/o/bulk/v1.0/keywords/common`,
		composeCreateTaskDTO(
			'EditObjectTagsBulkSelectionAction',
			{},
			selectedData
		)
	);
}

export default {
	createTag,
	getCommonTags,
	getTags,
};
