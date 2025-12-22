/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IVocabulary} from '../../common/types/IVocabulary';
import {composeCreateTaskDTO} from '../../main_view/bulk_actions_monitor/util';
import {IBulkActionFDSData} from '../types/BulkActionTask';
import ApiHelper from './ApiHelper';

async function createVocabulary(siteId: number, vocabulary: IVocabulary) {
	return await ApiHelper.post<IVocabulary>(
		`/o/headless-admin-taxonomy/v1.0/sites/${siteId}/taxonomy-vocabularies`,
		vocabulary
	);
}

async function fetchVocabulary(vocabularyId: number) {
	return await ApiHelper.get<IVocabulary>(
		`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabularyId}`
	);
}

async function updateVocabulary(vocabulary: IVocabulary) {
	return await ApiHelper.put<IVocabulary>(
		`/o/headless-admin-taxonomy/v1.0/taxonomy-vocabularies/${vocabulary.id}`,
		vocabulary
	);
}

async function getCommonCategories(
	groupId: number,
	selectedData: IBulkActionFDSData
) {
	return await ApiHelper.post<any>(
		`/o/bulk/v1.0/sites/${groupId}/taxonomy-vocabularies/common`,
		composeCreateTaskDTO('TaxonomyCategoryBulkAction', {}, selectedData)
	);
}

export default {
	createVocabulary,
	fetchVocabulary,
	getCommonCategories,
	updateVocabulary,
};
