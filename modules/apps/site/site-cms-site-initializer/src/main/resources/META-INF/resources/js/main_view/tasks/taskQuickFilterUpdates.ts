/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import toDatePart from '../../common/utils/toDatePart';
import {QuickFilterUpdates} from '../quick_filters/utils';

export type TaskQuickFilterType = 'completed' | 'overdue' | 'pending';

function getCompletedSelectedData(completed: boolean) {
	return {
		exclude: false,
		selectedItems: [
			{
				label: completed
					? Liferay.Language.get('completed')
					: Liferay.Language.get('pending'),
				value: String(completed),
			},
		],
	};
}

export const TASK_QUICK_FILTER_UPDATES: Record<
	TaskQuickFilterType,
	() => QuickFilterUpdates
> = {
	completed: () => ({
		completed: getCompletedSelectedData(true),
	}),
	overdue: () => ({
		completed: getCompletedSelectedData(false),
		dueDate: {
			exclude: false,
			from: null,
			to: toDatePart(new Date()),
		},
	}),
	pending: () => ({
		completed: getCompletedSelectedData(false),
	}),
};
