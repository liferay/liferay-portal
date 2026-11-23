/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionFDSData,
	deleteAssetEntriesBulkAction,
} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';

import {getTasksByParentTaskIds} from './api';

type Task = {embedded: {id: number; scopeKey: string}};

let countingTasks = false;

async function addSubtaskIds(
	allTaskIds: Set<number>,
	scopeKey: string,
	taskIds: number[]
) {
	const {data} = await getTasksByParentTaskIds({scopeKey, taskIds});

	const subtaskIds = (data?.items ?? [])
		.map(({id}) => id)
		.filter((id) => !allTaskIds.has(id));

	if (!subtaskIds.length) {
		return;
	}

	subtaskIds.forEach((subtaskId) => allTaskIds.add(subtaskId));

	await addSubtaskIds(allTaskIds, scopeKey, subtaskIds);
}

async function getCount(tasks: Task[]) {
	const allTaskIds = new Set(tasks.map(({embedded}) => embedded.id));

	if (!Liferay.FeatureFlags['LPD-107678']) {
		return allTaskIds.size;
	}

	const taskIdsByScopeKey = new Map<string, number[]>();

	tasks.forEach(({embedded: {id, scopeKey}}) => {
		taskIdsByScopeKey.set(scopeKey, [
			...(taskIdsByScopeKey.get(scopeKey) ?? []),
			id,
		]);
	});

	await Promise.all(
		[...taskIdsByScopeKey].map(([scopeKey, taskIds]) =>
			addSubtaskIds(allTaskIds, scopeKey, taskIds)
		)
	);

	return allTaskIds.size;
}

async function getTitle(selectedData: IBulkActionFDSData) {
	if (selectedData.selectAll) {
		return Liferay.Language.get('delete-all-tasks');
	}

	const count = await getCount(selectedData.items ?? []);

	if (count > 1) {
		return sub(Liferay.Language.get('delete-x-tasks'), [count]);
	}

	return Liferay.Language.get('delete-task');
}

export default async function deleteTasksBulkAction({
	apiURL,
	dataSetId,
	selectedData,
}: {
	apiURL: string;
	dataSetId: string;
	selectedData: IBulkActionFDSData;
}) {
	if (countingTasks) {
		return;
	}

	countingTasks = true;

	try {
		const title = await getTitle(selectedData);

		deleteAssetEntriesBulkAction({
			apiURL,
			dataSetId,
			getCustomBulkDeleteMessage: () => ({
				messages: [Liferay.Language.get('delete-tasks-confirmation')],
				title,
			}),
			selectedData,
			showConfirmationModal: true,
		});
	}
	finally {
		countingTasks = false;
	}
}
