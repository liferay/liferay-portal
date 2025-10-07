/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import React from 'react';

type Item = {
	href?: string;
	icon?: string;
	label: string;
	value: string;
};

export interface IActionsDropdown {
	items: Item[];
	onChange?: (value: string) => void;
}

const ActionsDropdown: React.FC<IActionsDropdown> = ({items, onChange}) => {
	return (
		<ClayDropdown
			closeOnClick
			items={items}
			trigger={
				<ClayButtonWithIcon
					aria-label={Liferay.Language.get('actions')}
					borderless
					displayType="secondary"
					size="sm"
					symbol="ellipsis-v"
				/>
			}
		>
			{items.map((item) => (
				<ClayDropdown.Item
					data-testid={`actions-item-${item.value}`}
					href={item?.href}
					key={item.value}
					onClick={() => onChange?.(item.value)}
				>
					{item.icon && (
						<ClayIcon className="mr-2" symbol={item.icon} />
					)}

					{item.label}
				</ClayDropdown.Item>
			))}
		</ClayDropdown>
	);
};

export {ActionsDropdown};
