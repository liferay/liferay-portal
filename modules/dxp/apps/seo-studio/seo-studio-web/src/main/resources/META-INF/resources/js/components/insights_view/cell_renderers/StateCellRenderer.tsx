/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

import {
	WORKFLOW_STATUS_APPROVED,
	WORKFLOW_STATUS_PENDING,
} from '../services/AutofixService';

type LabelDisplayType = React.ComponentProps<typeof ClayLabel>['displayType'];

const STATE_DISPLAY_TYPES: Record<number, LabelDisplayType> = {
	[WORKFLOW_STATUS_APPROVED]: 'success',
	[WORKFLOW_STATUS_PENDING]: 'info',
};

const STATE_LABELS: Record<number, string> = {
	[WORKFLOW_STATUS_APPROVED]: Liferay.Language.get('resolved'),
	[WORKFLOW_STATUS_PENDING]: Liferay.Language.get('open'),
};

export default function StateCellRenderer({value}: {value: number}) {
	const state = value ?? WORKFLOW_STATUS_PENDING;

	return (
		<ClayLabel displayType={STATE_DISPLAY_TYPES[state] ?? 'info'}>
			{STATE_LABELS[state] ?? STATE_LABELS[WORKFLOW_STATUS_PENDING]}
		</ClayLabel>
	);
}
