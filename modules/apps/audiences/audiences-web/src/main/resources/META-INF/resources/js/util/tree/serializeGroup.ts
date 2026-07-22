/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria, Group, SerializedGroup} from '../../types';
import {isGroup} from './isGroup';

export function serializeGroup(
	group: Group,
	audiencesCriteriasByKey: Record<string, AudiencesCriteria> = {}
): SerializedGroup {
	return {
		conjunction: group.conjunction,
		rules: group.items.map((node) =>
			isGroup(node)
				? serializeGroup(node, audiencesCriteriasByKey)
				: {
						attribute: node.attribute,
						operator: node.operator,
						value: serializeValue(
							node.value,
							audiencesCriteriasByKey[node.attribute]?.type
						),
					}
		),
	};
}

function serializeValue(
	value: string,
	type?: AudiencesCriteria['type']
): boolean | number | string {
	if (type === 'boolean') {
		return value === 'true';
	}

	if (type === 'number' && value !== '') {
		const number = Number(value);

		return Number.isNaN(number) ? value : number;
	}

	return value;
}
