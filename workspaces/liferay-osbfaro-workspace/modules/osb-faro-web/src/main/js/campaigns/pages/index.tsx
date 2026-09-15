import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import CampaignsDataSet from '../components/CampaignsDataSet';
import DataSourceEmptyState from 'shared/components/DataSourceEmptyState';
import Loading from 'shared/components/Loading';
import OverviewSection from '../components/OverviewSection';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {SectionHeader} from 'shared/components/SectionHeader';
import {Text} from '@clayui/core';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useParams} from 'react-router-dom';
import {useRequest} from 'shared/hooks/useRequest';

const Campaigns: React.FC = () => {
	const currentUser = useCurrentUser();
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

	const {data: metrics, loading} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaignMetrics,
		skipRequest: noDataSources,
		variables: {channelId: channelId!, groupId: groupId!},
	});

	const authorized = currentUser.isAdmin();

	const title = Liferay.Language.get('campaigns');

	const renderBody = () => {
		if (dataSourcesLoading) {
			return <Loading />;
		}

		if (noDataSources) {
			return (
				<DataSourceEmptyState
					authorized={authorized}
					description={
						authorized
							? Liferay.Language.get(
									'connect-a-data-source-containing-campaign-data'
								)
							: Liferay.Language.get(
									'please-contact-an-administrator-to-connect-a-data-source-containing-campaign-data'
								)
					}
					groupId={groupId!}
					title={Liferay.Language.get('no-campaign-data-available')}
				/>
			);
		}

		return (
			<>
				<OverviewSection loading={loading} metrics={metrics} />

				<SectionHeader
					icon="megaphone"
					rightContent={

						// Matches `TrailingNinetyDayRange`, which the section
						// above renders in the same slot. The design gives both
						// the same 16px secondary text.

						<span className="mr-2">
							<Text color="secondary" size={4}>
								{Liferay.Language.get('all-time')}
							</Text>
						</span>
					}
					title={title}
				/>

				<CampaignsDataSet channelId={channelId!} groupId={groupId!} />
			</>
		);
	};

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

			<BasePage.Body>{renderBody()}</BasePage.Body>
		</BasePage>
	);
};

export default Campaigns;
