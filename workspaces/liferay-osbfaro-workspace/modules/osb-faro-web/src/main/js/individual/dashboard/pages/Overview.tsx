import * as API from 'shared/api';
import ActiveIndividualsCard from '../hocs/ActiveIndividualsCard';
import BasePage from 'shared/components/base-page';
import ClayLink from '@clayui/link';
import Constants from 'shared/util/constants';
import DistributionCard from '../hocs/DistributionCard';
import EnrichedProfilesCard from '../hocs/EnrichedProfilesCard';
import InterestsCard from '../hocs/InterestsCard';
import React, {useEffect, useState} from 'react';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import TypeTrendCard from '../hocs/TypeTrendCard';
import URLConstants from 'shared/util/url-constants';
import {DataSource} from 'shared/util/records';
import {fromJS} from 'immutable';
import {Routes, toRoute} from 'shared/util/router';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useParams} from 'react-router-dom';

const {
	pagination: {cur},
} = Constants;

const MAX_DELTA = 500;

const Overview = () => {
	const [dataSources, setDataSources] = useState<DataSource[] | null>(null);
	const {channelId = '', groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();
	const currentUser = useCurrentUser();
	const authorized = currentUser.isAdmin();
	const dataSourceStates = useDataSources();

	useEffect(() => {
		API.dataSource
			.search({
				channelId,
				delta: MAX_DELTA,
				groupId,
				page: cur,
				query: '',
			})
			.then(({items}: {items: unknown[]}) => {
				setDataSources(
					items.map((item) => new DataSource(fromJS(item)))
				);
			});
	}, []);

	return (
		<BasePage.Body pageContainer>
			<StatesRenderer {...dataSourceStates}>
				<StatesRenderer.Empty
					description={
						<>
							{authorized
								? Liferay.Language.get(
										'connect-a-data-source-with-sites-data'
									)
								: Liferay.Language.get(
										'please-contact-your-workspace-administrator-to-add-data-sources'
									)}

							<ClayLink
								className="d-block mb-3"
								href={URLConstants.DataSourceConnection}
								key="DOCUMENTATION"
								target="_blank"
							>
								{Liferay.Language.get(
									'access-our-documentation-to-learn-more'
								)}
							</ClayLink>

							{authorized && (
								<ClayLink
									button
									className="button-root"
									displayType="primary"
									href={toRoute(
										Routes.SETTINGS_DATA_SOURCE_LIST,
										{
											groupId,
										}
									)}
								>
									{Liferay.Language.get(
										'connect-data-source'
									)}
								</ClayLink>
							)}
						</>
					}
					displayCard
					title={Liferay.Language.get(
						'no-sites-have-been-synced-from-data-sources'
					)}
				/>

				<StatesRenderer.Success>
					<div className="individuals-dashboard-overview-root overview-root">
						<div className="row">
							<div className="col-xl-8">
								<TypeTrendCard />
							</div>

							<div className="col-xl-4">
								<EnrichedProfilesCard
									dataSources={dataSources ?? []}
								/>
							</div>
						</div>

						<div className="row">
							<div className="col-xl-12">
								<ActiveIndividualsCard />
							</div>
						</div>

						<div className="row">
							<div className="col-xl-12">
								<InterestsCard />
							</div>
						</div>

						<div className="row">
							<div className="col-xl-12">
								<DistributionCard
									showAddDataSource={
										!!dataSources && !dataSources.length
									}
								/>
							</div>
						</div>
					</div>
				</StatesRenderer.Success>
			</StatesRenderer>
		</BasePage.Body>
	);
};

export default Overview;
