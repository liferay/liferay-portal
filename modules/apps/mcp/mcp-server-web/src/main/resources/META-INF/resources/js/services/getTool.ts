/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Tool} from '../types';
import ApiHelper from './ApiHelper';
import {TOOL_SETS_URL} from './constants';

export function getTool(toolSetName: string, toolName: string) {
	return ApiHelper.get<Pick<Tool, 'outputSchema'>>(
		`${TOOL_SETS_URL}/${encodeURIComponent(toolSetName)}/tools/${encodeURIComponent(toolName)}?fields=outputSchema&nestedFields=outputSchema`
	);
}
