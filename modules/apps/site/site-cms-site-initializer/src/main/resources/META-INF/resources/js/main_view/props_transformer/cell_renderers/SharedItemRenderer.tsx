/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React, {useMemo} from 'react';

import formatActionURL from '../../../common/utils/formatActionURL';

interface ActionItem {
	data: {id: string};
	href?: string;
}

export default function SharedItemRenderer({
	actions,
	itemData,
	options,
	value,
}: {
	actions: ActionItem[];
	itemData: any;
	options: {actionId: string};
	value: string;
}) {
	const linkHref = useMemo(() => {
		const {actionId} = options;

		if (!actions.length || !actionId) {
			return null;
		}

		const selectedAction = actions.find(({data}) => data?.id === actionId);

		if (!selectedAction?.href) {
			return null;
		}

		return formatActionURL(itemData, selectedAction.href);
	}, [actions, itemData, options]);

	return (
		<span className="align-items-center d-flex table-list-title">
			{linkHref ? (
				<ClayLink aria-label={value} data-senna-off href={linkHref}>
					{value}
				</ClayLink>
			) : (
				<span>{value}</span>
			)}

			{itemData.shareable && (
				<ClayTooltipProvider>
					<span
						data-tooltip-align="top"
						title={sub(
							Liferay.Language.get('shared-from-x'),
							itemData.siteName
						)}
					>
						<ClayIcon
							className="c-ml-2 text-secondary"
							symbol="users"
						/>
					</span>
				</ClayTooltipProvider>
			)}
		</span>
	);
}
