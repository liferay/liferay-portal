import * as API from 'shared/api';
import FilterPicker, {IFilterPickerItem} from './FilterPicker';
import React, {useMemo} from 'react';
import {getSafeDecodedURIComponent, getSafeTouchpoint} from 'shared/util/util';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const NO_ITEMS: IFilterPickerItem[] = [];

interface IAccountDropdownProps {
	assetType?: string;
	className?: string;
	initialAccountId?: string | null;
	initialAccountName?: string | null;
	onFilterChange: (item: IFilterPickerItem | null) => void;
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

	const {data, loading} = useRequest({
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

	// The account comes from the URL, which may name one that is not on the
	// fetched page, so it is passed as the selection rather than looked up.

	const selected = useMemo(
		() =>
			initialAccountId
				? {
						id: initialAccountId,
						name: initialAccountName || initialAccountId,
					}
				: null,
		[initialAccountId, initialAccountName]
	);

	return (
		<FilterPicker
			className={className}
			entityLabel={Liferay.Language.get('accounts')}
			items={data?.items ?? NO_ITEMS}
			loading={loading}
			onFilterChange={onFilterChange}
			selected={selected}
		/>
	);
};

export default AccountDropdown;
