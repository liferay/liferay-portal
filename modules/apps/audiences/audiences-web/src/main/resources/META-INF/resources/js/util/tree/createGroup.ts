/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {CriteriaNode, Group} from '../../types';

export function createGroup(
	conjunction = 'AND',
	items: CriteriaNode[] = []
): Group {
	return {conjunction, id: `group-${uuidv4()}`, items};
}
