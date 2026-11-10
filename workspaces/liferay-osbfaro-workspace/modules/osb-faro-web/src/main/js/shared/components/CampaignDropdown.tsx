import * as API from 'shared/api';
import FilterPicker, {IFilterPickerItem} from './FilterPicker';
import React, {useMemo} from 'react';
import {IPaginatedDataSourceParams} from 'shared/hooks/usePaginatedRequest';
import {useParams} from 'react-router-dom';

interface ICampaignDropdownProps {
	className?: string;
	initialCampaignId?: string | null;
	initialCampaignName?: string | null;
	onFilterChange: (item: IFilterPickerItem | null) => void;
}

const CampaignDropdown: React.FC<ICampaignDropdownProps> = ({
	className,
	initialCampaignId,
	initialCampaignName,
	onFilterChange,
}) => {
	const {channelId, groupId} = useParams<{
		channelId: string;
		groupId: string;
	}>();

	// The campaign comes from the URL, which may name one that is not on the
	// fetched page, so it is passed as the selection rather than looked up.

	const selected = useMemo(
		() =>
			initialCampaignId
				? {
						id: initialCampaignId,
						name: initialCampaignName || initialCampaignId,
					}
				: null,
		[initialCampaignId, initialCampaignName]
	);

	const paginatedDataSourceFn = ({
		page,
		pageSize,
		query,
	}: IPaginatedDataSourceParams) =>
		API.campaigns
			.searchCampaignNames({
				channelId,
				groupId: groupId as string,
				page,
				pageSize,
				query,
			})
			.then(({items, totalCount}) => ({items, total: totalCount}));

	return (
		<FilterPicker
			className={className}
			entityLabel={Liferay.Language.get('campaigns')}
			onFilterChange={onFilterChange}
			paginatedDataSourceFn={paginatedDataSourceFn}
			selected={selected}
		/>
	);
};

export default CampaignDropdown;
