/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AudiencesCriteria} from '../types';

const BOOLEAN_OPTIONS = [
	{label: Liferay.Language.get('true'), value: 'true'},
	{label: Liferay.Language.get('false'), value: 'false'},
];

export function getValueOptions(
	audiencesCriteria: AudiencesCriteria
): AudiencesCriteria['options'] {
	if (audiencesCriteria.inputType === 'boolean') {
		return BOOLEAN_OPTIONS;
	}

	return audiencesCriteria.options;
}
