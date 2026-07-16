/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {v4 as uuidv4} from 'uuid';

import {getOperators} from '../../constants/operators';
import {AudiencesCriteria, Rule} from '../../types';

export function createRule(audiencesCriteria: AudiencesCriteria): Rule {
	return {
		attribute: audiencesCriteria.key,
		id: `rule-${uuidv4()}`,
		operator:
			getOperators(
				audiencesCriteria.inputType,
				audiencesCriteria.type
			)[0] || '',
		value: audiencesCriteria.options[0]?.value || '',
	};
}
