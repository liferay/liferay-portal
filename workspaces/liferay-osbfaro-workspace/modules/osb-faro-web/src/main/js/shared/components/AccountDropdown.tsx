import * as API from 'shared/api';
import classNames from 'classnames';
import FilterPickerTrigger from './FilterPickerTrigger';
import React, {useMemo, useState} from 'react';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {
	createOrderIOMap,
	getDefaultSortOrder,
	NAME,
} from 'shared/util/pagination';
import {
	getSafeDecodedURIComponent,
	getSafeTouchpoint,
	truncateText,
} from 'shared/util/util';
import {Option, Picker, Text} from '@clayui/core';
import {useParams} from 'react-router-dom';
import {useQueryPagination} from 'shared/hooks/useQueryPagination';
import {useRequest} from 'shared/hooks/useRequest';

type Item = {
	id: string;
	name: string;
};

interface IAccountItem {
	id: string | null;
	name: string;
	displayName?: string;
}

const ALL_ACCOUNTS_ITEM: IAccountItem = {
	id: null,
	name: Liferay.Language.get('all-accounts'),
};

interface IAccountDropdownProps {
	assetType?: string;
	className?: string;
	initialAccountId?: string | null;
	initialAccountName?: string | null;
	onFilterChange: (item: Item | null) => void;
}

const AccountDropdown: React.FC<IAccountDropdownProps> = ({
	assetType,
	className,
	initialAccountId,
	initialAccountName,
	onFilterChange,
}) => {
	const {assetId, channelId, groupId, title, touchpoint} = useParams<{
		assetId: string;
		channelId: string;
		groupId: string;
		title: string;
		touchpoint: string;
	}>();
	const {delta: pageSize} = useQueryPagination({
		initialOrderIOMap: createOrderIOMap(NAME, getDefaultSortOrder(NAME)),
	});
	const [selectedKey, setSelectedKey] = useState<string>(
		initialAccountId ? String(initialAccountId) : 'null'
	);

	const {data} = useRequest({
		dataSourceFn: API.accounts.searchAccounts,
		variables: {
			assetId: assetType
				? assetType === 'page'
					? getSafeTouchpoint(touchpoint)
					: getSafeDecodedURIComponent(assetId)
				: undefined,
			assetTitle: assetType
				? getSafeDecodedURIComponent(title)
				: undefined,
			assetType,
			channelId,
			groupId,
			pageSize,
		},
	});

	const displayItems = useMemo(() => {
		const apiItems: IAccountItem[] = data?.items ?? [];

		const hasSelectedItem = apiItems.some(
			(item) => String(item.id) === selectedKey
		);

		const preloadedItems: IAccountItem[] =
			selectedKey !== 'null' && !hasSelectedItem
				? [
						{
							id: selectedKey,
							name: initialAccountName || selectedKey,
						},
					]
				: [];

		return [ALL_ACCOUNTS_ITEM, ...preloadedItems, ...apiItems].map(
			(item) => ({
				...item,
				displayName: truncateText(item.name, 35, null),
				id: item.id === null ? 'null' : String(item.id),
			})
		);
	}, [data, initialAccountName, selectedKey]);

	const handleSelectionChange = (key: string) => {
		setSelectedKey(key);

		if (key === 'null') {
			onFilterChange(null);

			return;
		}

		const selectedItem = displayItems.find((item) => item.id === key);

		onFilterChange(
			selectedItem ? {id: selectedItem.id, name: selectedItem.name} : null
		);
	};

	return (
		<ClayTooltipProvider>
			<div className={classNames('account-filter-dropdown', className)}>
				<Picker
					aria-label={Liferay.Language.get('all-accounts')}
					as={FilterPickerTrigger}
					className="border-light form-control-sm"
					items={displayItems}
					onSelectionChange={(key) =>
						handleSelectionChange(String(key))
					}
					searchable
					selectedKey={selectedKey}
				>
					{(item: IAccountItem) => (
						<Option key={String(item.id)} textValue={item.name}>
							<div
								className="w-100"
								title={
									item.name.length > 35
										? item.name
										: undefined
								}
							>
								<Text size={3}>{item.displayName}</Text>
							</div>
						</Option>
					)}
				</Picker>
			</div>
		</ClayTooltipProvider>
	);
};

export default AccountDropdown;
