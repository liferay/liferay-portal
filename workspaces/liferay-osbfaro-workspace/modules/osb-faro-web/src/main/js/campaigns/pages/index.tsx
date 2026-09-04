import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import CampaignsDataSet from '../components/CampaignsDataSet';
import OverviewSection from '../components/OverviewSection';
import {mockCampaigns, mockCampaignMetrics} from '../utils/mock-campaigns';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {SectionHeader} from 'shared/components/SectionHeader';
import {Text} from '@clayui/core';
import {useParams} from 'react-router-dom';

const Campaigns: React.FC = () => {
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = useParams();

	const title = Liferay.Language.get('campaigns');

	return (
		<BasePage documentTitle={title}>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId: channelId!,
						groupId: groupId!,
						label: selectedChannel?.name,
					}),
				]}
				groupId={groupId!}
			>
				<BasePage.Row>
					<BasePage.Header.TitleSection title={title} />
				</BasePage.Row>
			</BasePage.Header>

			<BasePage.Body>
				<OverviewSection metrics={mockCampaignMetrics} />

				<SectionHeader
					icon="megaphone"
					rightContent={
						<Text color="secondary" size={3}>
							{Liferay.Language.get('all-time')}
						</Text>
					}
					title={title}
				/>

				<CampaignsDataSet items={mockCampaigns} />
			</BasePage.Body>
		</BasePage>
	);
};

export default Campaigns;
