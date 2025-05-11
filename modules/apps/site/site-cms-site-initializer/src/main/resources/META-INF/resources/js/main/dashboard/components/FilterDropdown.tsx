/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import React from 'react';

export type Item = {
	description?: string;
	label: string;
	value: string;
};

interface IFilterDropdown extends React.HTMLAttributes<HTMLElement> {
	active: string;
	borderless?: boolean;
	filterByValue: string;
	icon?: string;
	items: Item[];
	onSelectItem: (item: Item) => void;
	triggerLabel: string;
}

const FilterDropdown: React.FC<IFilterDropdown> = ({
	active,
	borderless = true,
	className,
	filterByValue,
	icon,
	items,
	onSelectItem,
	triggerLabel,
}) => {
	return (
		<ClayDropdown
			className={classNames('filter-dropdown', className)}
			closeOnClick
			hasLeftSymbols
			trigger={
				<ClayButton
					aria-label={triggerLabel}
					borderless={borderless}
					data-testid={filterByValue}
					displayType="secondary"
					size="sm"
				>
					{icon && <ClayIcon symbol={icon} />}

					<span className="filter-dropdown__trigger-label ml-2">
						{triggerLabel}

						<ClayIcon className="ml-2" symbol="caret-bottom" />
					</span>
				</ClayButton>
			}
		>
			{items.map((item) => (
				<ClayDropdown.Item
					active={item.value === active}
					data-testid={`filter-dropdown-item-${item.value}`}
					key={item.value}
					onClick={() => onSelectItem(item)}
					symbolLeft={item.value === active ? 'check' : ''}
				>
					{item.description ? (
						<div>
							<Text size={4}>{item.label}</Text>
						</div>
					) : (
						item.label
					)}

					{item.description && (
						<Text size={1}>
							<span className="text-uppercase">
								{item.description}
							</span>
						</Text>
					)}
				</ClayDropdown.Item>
			))}
		</ClayDropdown>
	);
};

export {FilterDropdown};
