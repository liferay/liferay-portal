import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountDropdown from 'shared/components/AccountDropdown';
import BasePage from 'shared/components/base-page';
import MembershipsCard from '../components/MembershipsCard';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {pickBy} from 'lodash';
import {useAccountFilter} from 'shared/hooks/useAccountFilter';
import {useParams} from 'react-router-dom';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useRangeSelectorChange} from 'shared/hooks/useRangeSelectorChange';
import {useRequest} from 'shared/hooks/useRequest';

const CampaignDetail: React.FC = () => {
	const {accountId, accountName, accountQueryValues, setAccount} =
		useAccountFilter();
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

	const rangeSelectors = useQueryRangeSelectors();

	const {data: campaign} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaign,
		variables: {channelId: channelId!, groupId: groupId!, id: id!},
	});

	const {handleRangeSelectorChange} = useRangeSelectorChange();

	// Empty until the campaign arrives. The title doubles as the last
	// breadcrumb, so a placeholder would show up twice on the way in.

	const title = campaign?.campaignName ?? '';

	return (
		<BasePage documentTitle={title}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
					breadcrumbs.getCampaigns({
						channelId: channelId!,
						groupId: groupId!,
						queryValues: pickBy({
							...rangeSelectors,
							...accountQueryValues,
						}),
					}),
					breadcrumbs.getEntityName({label: title}),
				]}
				groupId={groupId!}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection title={title} />
				</BasePage.Row>
			</BasePage.Header>

			<BasePage.SubHeader>
				<AccountDropdown
					className="mr-2"
					initialAccountId={accountId}
					initialAccountName={accountName}
					onFilterChange={setAccount}
				/>

				<div className="d-flex justify-content-end w-100">
					<DropdownRangeKey
						bordered
						legacy={false}
						onRangeSelectorChange={handleRangeSelectorChange}
						rangeSelectors={rangeSelectors}
					/>
				</div>
			</BasePage.SubHeader>

			<BasePage.Body>
				<MembershipsCard
					campaignId={id!}
					channelId={channelId!}
					groupId={groupId!}
				/>
			</BasePage.Body>
		</BasePage>
	);
};

export default CampaignDetail;
