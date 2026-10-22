import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import React, {useContext} from 'react';
import TouchedAccountsCard from '../components/TouchedAccountsCard';
import {ChannelContext} from 'shared/context/channel';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {pickBy} from 'lodash';
import {RangeSelectors} from 'shared/types';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/router';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useParams} from 'react-router-dom';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useRequest} from 'shared/hooks/useRequest';

const CampaignDetail: React.FC = () => {
	const history = useHistoryAdapter();
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

	const rangeSelectors = useQueryRangeSelectors();

	const {data: campaign} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaign,
		variables: {channelId: channelId!, groupId: groupId!, id: id!},
	});

	const handleRangeSelectorChange = ({
		rangeEnd,
		rangeKey,
		rangeStart,
	}: RangeSelectors) => {

		// The bounds belong to a custom range only, so drop the previous ones
		// before writing rather than leaving them behind to narrow a preset.

		history.push(
			setUriQueryValues(
				pickBy({rangeEnd, rangeKey, rangeStart}),
				removeUriQueryParam(
					window.location.href,
					'rangeEnd',
					'rangeStart'
				)
			)
		);
	};

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
						queryValues: pickBy(rangeSelectors),
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
				<TouchedAccountsCard
					campaignId={id!}
					channelId={channelId!}
					groupId={groupId!}
				/>
			</BasePage.Body>
		</BasePage>
	);
};

export default CampaignDetail;
