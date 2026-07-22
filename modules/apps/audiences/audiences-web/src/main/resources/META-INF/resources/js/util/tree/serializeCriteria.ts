/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria, Group} from '../../types';
import {serializeGroup} from './serializeGroup';

export function serializeCriteria(
	root: Group,
	audiencesCriteriasByKey: Record<string, AudiencesCriteria> = {}
): string {
	return JSON.stringify(serializeGroup(root, audiencesCriteriasByKey));
}
