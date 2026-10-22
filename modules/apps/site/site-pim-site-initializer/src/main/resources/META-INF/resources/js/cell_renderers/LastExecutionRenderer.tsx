/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

const LAST_EXECUTIONS = {
	'failed': {
		displayType: 'danger',
		label: Liferay.Language.get('failed'),
	},
	'never-executed': {
		displayType: 'secondary',
		label: Liferay.Language.get('never-executed'),
	},
	'success': {
		displayType: 'success',
		label: Liferay.Language.get('success'),
	},
} as const;

export default function LastExecutionRenderer({
	value,
}: {
	value: keyof typeof LAST_EXECUTIONS;
}) {
	const {displayType, label} =
		LAST_EXECUTIONS[value] ?? LAST_EXECUTIONS['never-executed'];

	return (
		<ClayLabel className="font-weight-normal" displayType={displayType}>
			{label}
		</ClayLabel>
	);
}
