/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import React from 'react';

export const WORKFLOW_STATUS_APPROVED = 0;
export const WORKFLOW_STATUS_DENIED = 4;
export const WORKFLOW_STATUS_DRAFT = 2;
export const WORKFLOW_STATUS_EXPIRED = 3;
export const WORKFLOW_STATUS_IN_TRASH = 8;
export const WORKFLOW_STATUS_INCOMPLETE = 6;
export const WORKFLOW_STATUS_PENDING = 1;
export const WORKFLOW_STATUS_SCHEDULED = 7;

export function WorkflowStatusLabel({workflowStatus}) {
	let displayType = null;
	let label = null;

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
	else if (workflowStatus === WORKFLOW_STATUS_IN_TRASH) {
		displayType = 'secondary';
		label = Liferay.Language.get('in-trash');
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
