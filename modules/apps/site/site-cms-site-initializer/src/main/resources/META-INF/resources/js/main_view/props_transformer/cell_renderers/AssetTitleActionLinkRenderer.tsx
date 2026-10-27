/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

const AssetTitleActionLinkRenderer = ({
	actions,
	itemData,
	value,
}: {
	actions: Array<{data?: {id?: string}; href?: string}>;
	itemData: object;
	value: string;
}) => {
	const selectedAction = findAction(actions, 'actionLinkWorkflowTask');

	const href = selectedAction?.href
		? replaceTokens(selectedAction.href, itemData)
		: null;

	if (!href) {
		return <>{value}</>;
	}

	return (
		<div className="align-items-center d-flex table-list-title">
			<ClayLink aria-label={value} data-senna-off href={href}>
				{value}
			</ClayLink>
		</div>
	);
};

export default AssetTitleActionLinkRenderer;
