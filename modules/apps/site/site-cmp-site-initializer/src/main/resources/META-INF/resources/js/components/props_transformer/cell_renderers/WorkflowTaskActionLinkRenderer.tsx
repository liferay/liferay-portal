/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

import {getWorkflowTaskAssetTitle} from '../../../utils/getWorkflowTaskAssetTitle';
import {WorkflowTaskItemData} from '../../../utils/types';

type WorkflowAction = {
	data?: {id?: string};
	href?: string;
};

const WorkflowTaskActionLinkRenderer = ({
	actionId,
	actions,
	itemData,
}: {
	actionId: string;
	actions: WorkflowAction[];
	itemData: WorkflowTaskItemData;
}) => {
	const title = getWorkflowTaskAssetTitle(itemData);

	const selectedAction = findAction(actions, actionId);

	const href = selectedAction?.href
		? replaceTokens(selectedAction.href, itemData)
		: null;

	return href ? (
		<div className="align-items-center d-flex table-list-title">
			<ClayLink aria-label={title} data-senna-off href={href}>
				{title}
			</ClayLink>
		</div>
	) : (
		<>{title}</>
	);
};

export default WorkflowTaskActionLinkRenderer;
