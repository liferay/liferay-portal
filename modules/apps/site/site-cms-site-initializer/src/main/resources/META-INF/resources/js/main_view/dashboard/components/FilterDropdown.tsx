/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import {Text} from '@clayui/core';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import {debounce} from 'frontend-js-web';
import React, {useRef, useState} from 'react';

export type Item = {
	description?: string;
	hasChildren?: boolean;
	label: string;
	siteId?: number;
	value: string;
};

interface IFilterDropdown extends React.HTMLAttributes<HTMLElement> {
	active: boolean;
	borderless?: boolean;
	cancelLabel?: string;
	className?: string;
	filterByValue: string;
	icon?: string;
	items: Item[];
	loading?: boolean;
	onActiveChange: () => void;
	onCancel?: () => void;
	onSearch?: (value: string) => void;
	onSelectItem: (item: Item) => void;
	onTrigger?: () => void;
	selectedItem: Item;
	showLabelInSmallViewport?: boolean;
	title?: string;
}

const FilterDropdown: React.FC<IFilterDropdown> = ({
	active,
	borderless = true,
	cancelLabel,
	className,
	filterByValue,
	icon,
	items,
	loading,
	onActiveChange,
	onCancel,
	onSearch,
	onSelectItem,
	onTrigger,
	selectedItem,
	showLabelInSmallViewport = false,
	title,
}) => {
	const [value, setValue] = useState('');

	const triggerRef = useRef<HTMLButtonElement | null>(null);

	const triggerLabelClass = classNames(
		'filter-dropdown__trigger-label',
		'ml-2',
		{
			'd-md-inline d-none': !showLabelInSmallViewport,
		}
	);

	return (
		<ClayDropdown
			active={active}
			className={classNames('filter-dropdown', className)}
			closeOnClickOutside
			hasLeftSymbols
			hasRightSymbols
			onActiveChange={onActiveChange}
			trigger={
				<ClayButton
					aria-label={selectedItem.label}
					borderless={borderless}
					data-testid={filterByValue}
					displayType="secondary"
					onClick={() => {
						if (!active) {
							onTrigger?.();

							setValue('');
						}
					}}
					ref={(node: HTMLButtonElement) => {
						triggerRef.current = node;
					}}
					size="sm"
				>
					{icon && <ClayIcon symbol={icon} />}

					<span className={triggerLabelClass}>
						{selectedItem.label}

						<ClayIcon className="ml-2" symbol="caret-bottom" />
					</span>
				</ClayButton>
			}
		>
			{cancelLabel && (
				<>
					<div className="align-items-center d-flex dropdown-header pl-3">
						<ClayButtonWithIcon
							aria-label={Liferay.Language.get('cancel')}
							borderless
							className="mr-2"
							data-testid="cancel-button"
							displayType="secondary"
							monospaced
							onClick={() => onCancel?.()}
							size="sm"
							symbol="angle-left"
						/>

						<Text color="secondary" size={3}>
							{cancelLabel}
						</Text>
					</div>

					<ClayDropdown.Divider />
				</>
			)}

			{onSearch && (
				<>
					{!cancelLabel && (
						<div className="dropdown-subheader pl-3">{title}</div>
					)}

					<ClayDropdown.Search
						className="my-2"
						onChange={(value: string) => {
							setValue(value);

							debounce(() => onSearch(value), 200)();
						}}
						placeholder={Liferay.Language.get('search')}
						value={value}
					/>

					<ClayDropdown.Divider />
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
				items.map((item) => (
					<ClayDropdown.Item
						active={item.value === selectedItem.value}
						data-testid={`filter-dropdown-item-${item.value}`}
						key={item.value}
						onClick={() => {
							onSelectItem(item);
							setValue('');

							triggerRef?.current?.focus();
						}}
						symbolLeft={
							item.value === selectedItem.value ? 'check' : ''
						}
						symbolRight={item.hasChildren ? 'angle-right' : ''}
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
