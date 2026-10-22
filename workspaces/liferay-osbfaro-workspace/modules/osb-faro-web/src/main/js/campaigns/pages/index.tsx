import * as API from 'shared/api';
import * as breadcrumbs from 'shared/util/breadcrumbs';
import BasePage from 'shared/components/base-page';
import CampaignsDataSet from '../components/CampaignsDataSet';
import DataSourceEmptyState from 'shared/components/DataSourceEmptyState';
import Loading from 'shared/components/Loading';
import OverviewSection from '../components/OverviewSection';
import React, {useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {DropdownRangeKey} from 'shared/components/dropdown-range-key/DropdownRangeKey';
import {omit, pickBy} from 'lodash';
import {RangeKeyTimeRanges} from 'shared/util/constants';
import {RangeSelectors} from 'shared/types';
import {removeUriQueryParam, setUriQueryValues} from 'shared/util/router';
import {SectionHeader} from 'shared/components/SectionHeader';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useHistoryAdapter} from 'shared/hooks/useHistoryAdapter';
import {useParams} from 'react-router-dom';
import {useQueryRangeSelectors} from 'shared/hooks/useQueryRangeSelectors';
import {useRequest} from 'shared/hooks/useRequest';

const Campaigns: React.FC = () => {
	const currentUser = useCurrentUser();
	const history = useHistoryAdapter();
	const {selectedChannel} = useContext(ChannelContext);

	const {channelId, groupId} = useParams();

	const {empty: noDataSources, loading: dataSourcesLoading} =
		useDataSources();

	const rangeSelectors = useQueryRangeSelectors();

	const rangeQueryValues = pickBy(rangeSelectors);

	// `CUSTOM` is a client side sentinel the API answers with a 500, so a
	// custom range travels as its bounds alone.

	const {data: metrics, loading} = useRequest({
		dataSourceFn: API.campaigns.fetchCampaignMetrics,
		skipRequest: noDataSources,
		variables: {
			channelId: channelId!,
			groupId: groupId!,
			rangeQueryValues:
				rangeQueryValues.rangeKey === RangeKeyTimeRanges.CustomRange
					? omit(rangeQueryValues, 'rangeKey')
					: rangeQueryValues,
		},
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
