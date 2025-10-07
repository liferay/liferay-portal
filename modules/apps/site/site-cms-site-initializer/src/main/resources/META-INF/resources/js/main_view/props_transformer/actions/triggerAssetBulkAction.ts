/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	IBulkActionTaskStarterDTO,
	IBulkActionTaskType,
} from '../../../common/types/BulkActionTask';
import {START_TASK} from '../../../common/utils/events';

export function triggerAssetBulkAction<T extends keyof IBulkActionTaskType>(
	dto: IBulkActionTaskStarterDTO<T>
): void {
	Liferay.fire(START_TASK, dto);
}
