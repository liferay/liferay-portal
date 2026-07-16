/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Group} from '../../types';
import {updateGroup} from './updateGroup';

export function setConjunction(
	root: Group,
	groupPath: number[],
	conjunction: string
): Group {
	return updateGroup(root, groupPath, (group) => ({
		...group,
		conjunction,
	}));
}
