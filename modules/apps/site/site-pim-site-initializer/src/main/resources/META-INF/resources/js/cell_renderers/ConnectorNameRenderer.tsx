/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

export default function ConnectorNameRenderer({
	actions,
	itemData,
	value,
}: {
	actions: any[];
	itemData: any;
	value: string;
}) {
	const editAction = findAction(actions, 'edit');

	const href =
		editAction?.href && itemData?.actions?.update
			? replaceTokens(editAction.href, itemData)
			: null;

	return (
		<span className="table-list-title">
			{href ? (
				<ClayLink href={href}>{value}</ClayLink>
			) : (
				<span>{value}</span>
			)}
		</span>
	);
}
