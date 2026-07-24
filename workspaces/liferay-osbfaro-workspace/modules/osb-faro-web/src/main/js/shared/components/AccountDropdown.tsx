import * as API from 'shared/api';
import classNames from 'classnames';
import FilterPicker, {FilterPickerItem} from './FilterPicker';
import React, {useMemo} from 'react';
import {getSafeDecodedURIComponent, getSafeTouchpoint} from 'shared/util/util';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const NO_ITEMS: FilterPickerItem[] = [];

type Item = {
	id: string;
	name: string;
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
		},
	});

	const preloadedItem = useMemo(
		() =>
			initialAccountId
				? {
						id: String(initialAccountId),
						name: initialAccountName || String(initialAccountId),
					}
				: null,
		[initialAccountId, initialAccountName]
	);

	return (
		<FilterPicker
			allItemsLabel={Liferay.Language.get('all-accounts')}
			className={classNames('account-filter-dropdown', className)}
			items={data?.items ?? NO_ITEMS}
			onFilterChange={onFilterChange}
			preloadedItem={preloadedItem}
		/>
	);
};

export default AccountDropdown;
