import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {mockCampaigns} from '../utils/mock-campaigns';
import {useParams} from 'react-router-dom';

const CampaignDetail: React.FC = () => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId, id} = useParams();

	// The campaign the row was built from, looked up in the mock the list
	// screen renders. The integration task replaces this with the fetch the
	// endpoint serves, at which point the name arrives with the campaign.

	const campaign = mockCampaigns.find((campaign) => campaign.id === id);

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

			<BasePage.Body />
		</BasePage>
	);
};

export default CampaignDetail;
