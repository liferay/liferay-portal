/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import {LinkOrButton} from '@clayui/shared';
import React from 'react';

import formatActionURL from '../utils/actionItems/formatActionURL';
import {IQuickActions} from '../utils/types';

function QuickActions({actions, itemData, itemId, onClick}: IQuickActions) {
	return (
		<div className="quick-action-menu">
			{actions.map((action) => {
				return (
					<LinkOrButton
						aria-label={action.label || action.icon}
						className="component-action quick-action-item"
						disabled={action.disabled}
						displayType="unstyled"
						href={
							action.href &&
							formatActionURL(
								action.href,
								itemData,
								action.target
							)
						}
						key={action.data?.id || action.label}
						monospaced={false}
						onClick={(event: any) => {
							event.stopPropagation();

							onClick({
								action,
								event,
								itemData,
								itemId,
							});
						}}
						symbol={action.icon}
						title={action.label}
					>
						{action.icon && <ClayIcon symbol={action.icon} />}
					</LinkOrButton>
				);
			})}
		</div>
	);
}

export default QuickActions;
