/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getOperatorLabel} from '../constants/operators';
import {AudiencesCriteria, Rule} from '../types';
import {getValueOptions} from './getValueOptions';

export function getConditionLabel(
	rule: Rule,
	audiencesCriteria: AudiencesCriteria
): string {
	const {inputType, label} = audiencesCriteria;

	const valueOptions = getValueOptions(audiencesCriteria);

	const value = valueOptions.length
		? valueOptions.find((option) => option.value === rule.value)?.label
		: rule.value;

	return [label, getOperatorLabel(rule.operator, inputType), value]
		.filter(Boolean)
		.join(' ');
}
