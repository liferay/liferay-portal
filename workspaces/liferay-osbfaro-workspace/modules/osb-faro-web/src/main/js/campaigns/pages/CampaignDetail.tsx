import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import React, {useContext} from 'react';
import TouchedAccountsCard from '../components/TouchedAccountsCard';
import {ChannelContext} from 'shared/context/channel';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const CampaignDetail: React.FC = () => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

	const {data: campaign} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaign,
		variables: {channelId: channelId!, groupId: groupId!, id: id!},
	});

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
					}),
					breadcrumbs.getEntityName({label: title}),
				]}
				groupId={groupId!}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection title={title} />
				</BasePage.Row>
			</BasePage.Header>

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
