import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountDropdown from 'shared/components/AccountDropdown';
import BasePage from 'shared/components/base-page';
import CampaignsDataSet from '../components/CampaignsDataSet';
import DataSourceEmptyState from 'shared/components/DataSourceEmptyState';
import Loading from 'shared/components/Loading';
import OverviewSection from '../components/OverviewSection';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useAccountFilter} from 'shared/hooks/useAccountFilter';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useParams} from 'react-router-dom';
import {useRangeQueryValues} from 'shared/hooks/useRangeQueryValues';
import {useRangeSelectorChange} from 'shared/hooks/useRangeSelectorChange';
import {useRequest} from 'shared/hooks/useRequest';

const Campaigns: React.FC = () => {
	const {accountId, accountName, setAccount} = useAccountFilter();
	const currentUser = useCurrentUser();
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

	const {apiRangeParams, rangeSelectors} = useRangeQueryValues();

	const {data: metrics, loading} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaignMetrics,
		skipRequest: noDataSources,
		variables: {
			accountId,
			channelId: channelId!,
			groupId: groupId!,
			rangeQueryValues: apiRangeParams,
		},
	});

	const {handleRangeSelectorChange} = useRangeSelectorChange();

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
				<OverviewSection
					loading={loading}
					metrics={metrics}
					rangeSelectors={rangeSelectors}
				/>

				<SectionHeader icon="megaphone" title={title} />

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

			{!dataSourcesLoading && !noDataSources && (
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
			)}

			<BasePage.Body>{renderBody()}</BasePage.Body>
		</BasePage>
	);
};

export default Campaigns;
