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

	return await ApiHelper.post<Tag>(
		`/o/headless-admin-taxonomy/v1.0/sites/${cmsGroupId}/keywords`,
		requestBody
	);
}

async function getCommonTags(selectedData: IBulkActionFDSData) {
	return await ApiHelper.post<any>(
		`/o/bulk/v1.0/keywords/common`,
		composeCreateTaskDTO('KeywordBulkAction', {}, selectedData)
	);
}

export default {
	createTag,
	getCommonTags,
};
