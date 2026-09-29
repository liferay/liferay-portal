/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

import {IRoomObjectEntry} from '../../../common/utils/types';

interface ActionItem {
	data: {id: string};
	href?: string;
}

const RoomNameRenderer = ({
	actions,
	itemData,
	options,
}: {
	actions: ActionItem[];
	itemData: IRoomObjectEntry;
	options: {actionId: string};
}) => {
	const {name, r_accountToDSRRooms_accountEntry: account} = itemData;
	const {actionId} = options;

	const selectedAction =
		actions?.length && actionId ? findAction(actions, actionId) : null;

	const formattedHref =
		selectedAction?.href && replaceTokens(selectedAction.href, itemData);

	return (
		<div>
			{formattedHref ? (
				<ClayLink
					className="component-title"
					data-senna-off
					href={formattedHref}
				>
					<div className="text-3 text-weight-semi-bold">{name}</div>
				</ClayLink>
			) : (
				<div className="text-weight-semi-bold">{name}</div>
			)}

			{account?.name && (
				<div className="text-2 text-truncate">{account.name}</div>
			)}
		</div>
	);
};

export default RoomNameRenderer;
