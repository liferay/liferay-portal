/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

type DisplayType = 'danger' | 'info' | 'secondary' | 'success' | 'warning';

export const WORKFLOW_STATUS_APPROVED = 0;
export const WORKFLOW_STATUS_DENIED = 4;
export const WORKFLOW_STATUS_DRAFT = 2;
export const WORKFLOW_STATUS_EXPIRED = 3;
export const WORKFLOW_STATUS_IN_TRASH = 8;
export const WORKFLOW_STATUS_INACTIVE = 5;
export const WORKFLOW_STATUS_INCOMPLETE = 6;
export const WORKFLOW_STATUS_PENDING = 1;
export const WORKFLOW_STATUS_SCHEDULED = 7;

export default function WorkflowStatusLabel({
	workflowStatus,
}: {
	workflowStatus: number;
}) {
	let displayType: DisplayType | null = null;
	let label: string | null = null;

	if (workflowStatus === WORKFLOW_STATUS_APPROVED) {
		displayType = 'success';
		label = Liferay.Language.get('approved');
	}
	else if (workflowStatus === WORKFLOW_STATUS_DENIED) {
		displayType = 'danger';
		label = Liferay.Language.get('denied');
	}
	else if (workflowStatus === WORKFLOW_STATUS_DRAFT) {
		displayType = 'secondary';
		label = Liferay.Language.get('draft');
	}
	else if (workflowStatus === WORKFLOW_STATUS_EXPIRED) {
		displayType = 'warning';
		label = Liferay.Language.get('expired');
	}
	else if (workflowStatus === WORKFLOW_STATUS_IN_TRASH) {
		displayType = 'secondary';
		label = Liferay.Language.get('in-trash');
	}
	else if (workflowStatus === WORKFLOW_STATUS_INACTIVE) {
		displayType = 'secondary';
		label = Liferay.Language.get('inactive');
	}
	else if (workflowStatus === WORKFLOW_STATUS_INCOMPLETE) {
		displayType = 'secondary';
		label = Liferay.Language.get('pending-approval');
	}
	else if (workflowStatus === WORKFLOW_STATUS_PENDING) {
		displayType = 'info';
		label = Liferay.Language.get('pending');
	}
	else if (workflowStatus === WORKFLOW_STATUS_SCHEDULED) {
		displayType = 'info';
		label = Liferay.Language.get('scheduled');
	}

	return displayType && label ? (
		<ClayLabel displayType={displayType}>{label}</ClayLabel>
	) : null;
}
