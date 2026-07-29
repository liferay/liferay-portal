import * as breadcrumbs from 'shared/util/breadcrumbs';
import AccountDropdown from 'shared/components/AccountDropdown';
import BasePage from 'shared/components/base-page';
import BundleRouter from 'route-middleware/BundleRouter';
import ClayLink from '@clayui/link';
import DownloadCSVReport from 'shared/components/download-report/DownloadCSVReport';
import DownloadPDFReport from 'shared/components/download-report/DownloadPDFReport';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import SegmentDropdown from 'shared/components/SegmentDropdown';
import StatesRenderer from 'shared/components/states-renderer/StatesRenderer';
import URLConstants from 'shared/util/url-constants';
import {CSVType} from 'shared/components/download-report/utils';
import {getMatchedRoute, Routes, toRoute} from 'shared/util/router';
import {pickBy} from 'lodash';
import {Router} from 'shared/types';
import {Switch, useParams} from 'react-router-dom';
import {useAccountFilter} from 'shared/hooks/useAccountFilter';
import {useChannelContext} from 'shared/context/channel';
import {useCurrentUser} from 'shared/hooks/useCurrentUser';
import {useDataSources} from 'shared/context/dataSources';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useSegmentFilter} from 'shared/hooks/useSegmentFilter';

const InterestDetails = lazy(
	() =>
		import(

			/* webpackChunkName: "SitesDashboardInterestDetails" */ './InterestDetails'
		)
);
const Interests = lazy(
	() =>
		import(/* webpackChunkName: "SitesDashboardInterests" */ './Interests')
);
const Overview = lazy(
	() => import(/* webpackChunkName: "SitesDashboardOverview" */ './Overview')
);
const SearchTermsPage = lazy(
	() =>
		import(

			/* webpackChunkName: "SitesDashboardSearchTerms" */ './SearchTermsPage'
		)
);
const Touchpoints = lazy(
	() =>
		import(

			/* webpackChunkName: "SitesDashboardTouchpoints" */ './Touchpoints'
		)
);

const NAV_ITEMS = [
	{
		exact: true,
		label: Liferay.Language.get('overview'),
		route: Routes.SITES,
	},
	{
		exact: true,
		label: Liferay.Language.get('pages'),
		route: Routes.SITES_TOUCHPOINTS,
	},
	{
		exact: false,
		label: Liferay.Language.get('interests'),
		route: Routes.SITES_INTERESTS,
	},
	{
		exact: true,
		label: Liferay.Language.get('search-terms'),
		route: Routes.SITES_SEARCH_TERMS,
	},
];

interface IDashboardProps extends React.HTMLAttributes<HTMLDivElement> {
	router: Router;
}

export const Dashboard: React.FC<IDashboardProps> = ({router}) => {
	const {channelId, groupId = ''} = useParams<{
		channelId: string;
		groupId: string;
	}>();
	const {accountId, accountName, setAccount} = useAccountFilter();
	const {segmentId, segmentName, setSegment} = useSegmentFilter();
	const dataSourceStates = useDataSources();
	const LDPEnabled = useLDPEnabled({groupId});
	const {selectedChannel} = useChannelContext();
	const currentUser = useCurrentUser();

	if (!channelId) {
		return null;
	}

	const authorized = currentUser.isAdmin();
	const selectedChannelName = selectedChannel && selectedChannel.name;
	const matchedRoute = getMatchedRoute(NAV_ITEMS);

	return (
		<BasePage
			className="sites-dashboard-root"
			documentTitle={Liferay.Language.get('sites')}
		>
			<BasePage.Header
				breadcrumbs={[
					breadcrumbs.getHome({
						channelId,
						groupId,
						label: selectedChannelName,
					}),
				]}
				groupId={groupId}
			>
				<BasePage.Header.TitleSection
					className={getCN({'no-sites-connected': !selectedChannel})}
					title={
						selectedChannel
							? Liferay.Language.get('sites')
							: Liferay.Language.get('no-sites-connected')
					}
				/>

				<BasePage.Header.NavBar
					items={NAV_ITEMS}
					routeParams={{channelId, groupId}}
					routeQueries={pickBy({
						accountId,
						accountName,
						segmentId,
						segmentName,
					})}
				/>
			</BasePage.Header>

			<BasePage.SubHeader>
				{LDPEnabled && (
					<AccountDropdown
						className="mr-2"
						initialAccountId={accountId}
						initialAccountName={accountName}
						onFilterChange={setAccount}
					/>
				)}

				{LDPEnabled && (
					<SegmentDropdown
						className="mr-2"
						initialSegmentId={segmentId}
						initialSegmentName={segmentName}
						onFilterChange={setSegment}
					/>
				)}

				<div className="d-flex justify-content-end w-100">
					{matchedRoute === Routes.SITES && (
						<DownloadPDFReport
							disabled={!!dataSourceStates.empty}
							subtitle={selectedChannelName ?? undefined}
							title={Liferay.Language.get('sites-dashboard')}
						/>
					)}

					{matchedRoute === Routes.SITES_SEARCH_TERMS && (
						<DownloadCSVReport
							disabled={!!dataSourceStates.empty}
							type={CSVType.SearchTerms}
							typeLang={Liferay.Language.get('search-terms')}
						/>
					)}

					{matchedRoute === Routes.SITES_TOUCHPOINTS && (
						<DownloadCSVReport
							disabled={!!dataSourceStates.empty}
							type={CSVType.Page}
							typeLang={Liferay.Language.get('pages')}
						/>
					)}
				</div>
			</BasePage.SubHeader>

			<BasePage.Context.Provider
				value={{
					accountId,
					accountName,
					filters: {},
					router,
					segmentId,
					segmentName,
				}}
			>
				<BasePage.Body>
					<Suspense fallback={<Loading center />}>
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
											href={
												URLConstants.DataSourceConnection
											}
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
									'no-sites-synced-from-data-sources'
								)}
							/>

							<StatesRenderer.Success>
								<Switch>
									<BundleRouter
										data={InterestDetails}
										destructured={false}
										exact
										path={Routes.SITES_INTEREST_DETAILS}
									/>

									<BundleRouter
										data={Interests}
										destructured={false}
										exact
										path={Routes.SITES_INTERESTS}
									/>

									<BundleRouter
										data={Touchpoints}
										destructured={false}
										exact
										path={Routes.SITES_TOUCHPOINTS}
									/>

									<BundleRouter
										componentProps={{
											channelName: selectedChannelName,
										}}
										data={Overview}
										destructured={false}
										exact
										path={Routes.SITES}
									/>

									<BundleRouter
										data={SearchTermsPage}
										destructured={false}
										exact
										path={Routes.SITES_SEARCH_TERMS}
									/>

									<RouteNotFound />
								</Switch>
							</StatesRenderer.Success>
						</StatesRenderer>
					</Suspense>
				</BasePage.Body>
			</BasePage.Context.Provider>
		</BasePage>
	);
};

export default Dashboard;
