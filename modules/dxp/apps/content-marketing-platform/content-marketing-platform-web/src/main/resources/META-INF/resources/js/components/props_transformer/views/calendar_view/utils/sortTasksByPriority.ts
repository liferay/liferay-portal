/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Immutable} from '@liferay/frontend-js-state-web';
import {isOverdue} from '@liferay/site-cms-site-initializer';

import {ITaskObjectEntry} from '../../../../../utils/types';

const OVERDUE_PRIORITY = 0;

const STATE_PRIORITY: {[key: string]: number} = {
	blocked: 1,
	done: 4,
	inProgress: 2,
	notStarted: 3,
};

const UNKNOWN_PRIORITY = 5;

function getTaskPriority(task: Immutable<ITaskObjectEntry>): number {
	if (isOverdue(task)) {
		return OVERDUE_PRIORITY;
	}

	return STATE_PRIORITY[task.state?.key] ?? UNKNOWN_PRIORITY;
}

export default function sortTasksByPriority(
	tasks: Immutable<ITaskObjectEntry>[]
): Immutable<ITaskObjectEntry>[] {
	return [...tasks].sort(
		(taskA, taskB) => getTaskPriority(taskA) - getTaskPriority(taskB)
	);
}
