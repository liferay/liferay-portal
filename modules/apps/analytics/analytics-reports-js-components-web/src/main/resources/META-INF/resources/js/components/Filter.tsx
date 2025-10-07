/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {Text} from '@clayui/core';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {debounce} from 'frontend-js-web';
import React, {useState} from 'react';

type Item = {
	description?: string;
	label: string;
	value: string;
};

interface IFilterProps extends React.HTMLAttributes<HTMLElement> {
	active: string;
	filterByValue: string;
	icon: string;
	items: Item[];
	loading?: boolean;
	onSearch?: (value: string) => void;
	onSelectItem: (item: Item) => void;
	title?: string;
	triggerLabel: string;
}

const Filter: React.FC<IFilterProps> = ({
	active,
	className,
	filterByValue,
	icon,
	items,
	loading,
	onSearch,
	onSelectItem,
	title,
	triggerLabel,
}) => {
	const [value, setValue] = useState<string>('');

	return (
		<ClayDropdown
			className={classNames('filter', className)}
			closeOnClick
			hasLeftSymbols
			trigger={
				<ClayButton
					aria-label={triggerLabel}
					borderless
					data-testid={filterByValue}
					displayType="secondary"
					size="xs"
				>
					<ClayIcon symbol={icon} />

					<span className="filter__trigger-label ml-2">
						{triggerLabel}

						<ClayIcon className="ml-2" symbol="caret-bottom" />
					</span>
				</ClayButton>
			}
		>
			{onSearch && (
				<>
					<div className="dropdown-subheader pl-3">{title}</div>

					<ClayDropdown.Search
						className="my-2"
						onChange={(value: any) => {
							setValue(value);

							debounce(() => onSearch(value), 200)();
						}}
						placeholder={Liferay.Language.get('search')}
						value={value}
					/>
				</>
			)}

			{loading && <ClayLoadingIndicator data-testid="loading" />}

			{!loading && !items.length && (
				<ClayDropdown.Item className="px-0 text-center">
					<Text size={3} weight="semi-bold">
						{Liferay.Language.get('no-filters-were-found')}
					</Text>
				</ClayDropdown.Item>
			)}

			{!loading &&
				!!items.length &&
				items.map(({description, label, value}) => (
					<ClayDropdown.Item
						active={value === active}
						data-testid={`filter-item-${value}`}
						key={value}
						onClick={() => onSelectItem({label, value})}
						symbolLeft={value === active ? 'check' : ''}
					>
						{description ? (
							<div>
								<Text size={4}>{label}</Text>
							</div>
						) : (
							label
						)}

						{description && (
							<Text size={1}>
								<span className="text-uppercase">
									{description}
								</span>
							</Text>
						)}
					</ClayDropdown.Item>
				))}
		</ClayDropdown>
	);
};

export default Filter;
