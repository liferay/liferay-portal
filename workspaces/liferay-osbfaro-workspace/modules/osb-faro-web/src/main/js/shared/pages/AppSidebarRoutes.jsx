import BundleRouter from '../../route-middleware/BundleRouter';
import DataSourcesProvider from 'shared/context/dataSources';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense, useContext} from 'react';
import {ChannelContext} from 'shared/context/channel';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {DEVELOPER_MODE} from 'shared/util/constants';
import {DownloadReportProvider} from 'shared/components/download-report/DownloadReportContext';
import {
	matchPath,
	Route,
	Routes as RouterRoutes,
	useLocation
} from 'react-router-dom';
import {Routes} from 'shared/util/router';
import {
	withLDPEnabled,
	withOnboarding,
	withUnassignedSegments
} from 'shared/hoc';
import {withSidebar} from 'shared/hoc';

/**
 * Inject the current `channelId` from the URL.
 *
 * Under the React Router v7 descendant-`<Routes>` topology, `AppSidebarRoutes`
 * is mounted at the parent `*` splat, so `channelId` is not a named param at
 * this level and is absent from the params `BundleRouter` injects. The sidebar
 * chain (channel selector, nav links, `checkValidChannel`) needs it, so derive
 * it from the pathname. Re-runs on navigation via `useLocation`.
 */
const withChannelId = (WrappedComponent) => (props) => {
	const {pathname} = useLocation();

	const match = matchPath({end: false, path: Routes.CHANNEL}, pathname);

	return (
		<WrappedComponent {...props} channelId={match?.params?.channelId} />
	);
};

const UIKit = lazy(() =>
	import(/* webpackChunkName: "UIKit" */ '../../ui-kit/pages/index')
);

/* No Properties Available */
const NoPropertiesAvailable = lazy(() =>
	import(
		/* webpackChunkName: "NoPropertiesAvailable" */ './NoPropertiesAvailable'
	)
);

/* Segments */
const SegmentsList = lazy(() =>
	import(/* webpackChunkName: "SegmentsList" */ '../../segment/pages/List')
);
const SegmentProfileRoutes = lazy(() =>
	import(
		/* webpackChunkName: "SegmentProfileRoutes" */ '../../segment/pages/ProfileRoutes'
	)
);
const SegmentEdit = lazy(() =>
	import(/* webpackChunkName: "SegmentEdit" */ '../../segment/pages/Edit')
);

/* Accounts */

const AccountsList = lazy(() =>
	import(
		/* webpackChunkName: "AccountsList" */ '../../contacts/pages/account/List'
	)
);
const AccountProfileRoutes = lazy(() =>
	import(
		/* webpackChunkName: "AccountProfileRoutes" */ '../../contacts/pages/account/ProfileRoutes'
	)
);

/* Event Analysis */

const EventAnalysisCreate = lazy(() =>
	import(
		/* webpackChunkName: "EventAnalysisCreate" */ '../../event-analysis/pages/Create'
	)
);

const EventAnalysisEdit = lazy(() =>
	import(
		/* webpackChunkName: "EventAnalysisEdit" */ '../../event-analysis/pages/Edit'
	)
);

const EventAnalysisList = lazy(() =>
	import(
		/* webpackChunkName: "EventAnalysisList" */ '../../event-analysis/pages/List'
	)
);

/* Individuals */

const IndividualProfileRoutes = lazy(() =>
	import(
		/* webpackChunkName: "IndividualProfileRoutes" */ '../../individual/profile/pages/ProfileRoutes'
	)
);

const IndividualProfileRoutesCDP = lazy(() =>
	import(
		/* webpackChunkName: "IndividualProfileRoutesCDP" */ '../../individual/profile/pages/ProfileRoutesCDP'
	)
);

const IndividualsDashboard = lazy(() =>
	import(
		/* webpackChunkName: "IndividualsDashboard" */ '../../individual/dashboard/pages'
	)
);

const IndividualsDashboardCDP = lazy(() =>
	import(
		/* webpackChunkName: "IndividualsDashboardCDP" */ '../../individual/dashboard/pages/IndividualsDashboardCDP'
	)
);

/* Campaigns */

const CampaignsDashboard = lazy(() =>
	import(
		/* webpackChunkName: "CampaignsDashboard" */ '../../campaigns/pages'
	)
);

const CampaignDetail = lazy(() =>
	import(
		/* webpackChunkName: "CampaignDetail" */ '../../campaigns/pages/CampaignDetail'
	)
);

/* Lifecycle */
const LifecycleDashboard = lazy(() =>
	import(
		/* webpackChunkname: "LifecycleDashboard" */ '../../lifecycle/pages/BaseLifecycle'
	)
);

const LifecycleCreate = lazy(() =>
	import(
		/* webpackChunkName: "LifecycleCreate" */ '../../lifecycle/pages/CreateLifecycle'
	)
);

const LifecycleEdit = lazy(() =>
	import(
		/* webpackChunkName: "LifecycleEdit" */ '../../lifecycle/pages/EditLifecycle'
	)
);

/* Sites */

const SitesDashboard = lazy(() =>
	import(/* webpackChunkName: "SitesDashboard" */ '../../sites/pages')
);

/* Experiments */

const ExperimentsList = lazy(() =>
	import(
		/* webpackChunkName: "ExperimentsList" */ '../../experiments/pages/ExperimentsListPage'
	)
);

const ExperimentOverview = lazy(() =>
	import(
		/* webpackChunkName: "ExperimentsList" */ '../../experiments/pages/ExperimentOverviewPage'
	)
);

const TouchpointRoutes = lazy(() =>
	import(
		/* webpackChunkName: "TouchpointRoutes" */ 'sites/touchpoints/pages/TouchpointRoutes'
	)
);

/* Assets */

const NewAssetsList = lazy(() =>
	import(/* webpackChunkName: "NewAssetsList" */ 'assets/pages/List')
);

const AssetDashboard = lazy(() =>
	import(/* webpackChunkName: "AssetDashboard" */ 'assets/pages/Dashboard')
);

const AppSidebarRoutes = ({LDPEnabled, currentUser, groupId}) => {
	const {selectedChannel} = useContext(ChannelContext);

	return (
		<DataSourcesProvider groupId={groupId} skip={!selectedChannel}>
			<DownloadReportProvider>
				<Suspense fallback={<Loading />}>
					{selectedChannel ? (
						<RouterRoutes>
							<Route
								element={
									<BundleRouter
										data={
											LDPEnabled
												? IndividualProfileRoutesCDP
												: IndividualProfileRoutes
										}
									/>
								}
								path=":channelId?/contacts/individuals/known-individuals/:id/*"
							/>

							<Route
								element={
									<BundleRouter
										data={
											LDPEnabled
												? IndividualsDashboardCDP
												: IndividualsDashboard
										}
										destructured={false}
									/>
								}
								path=":channelId?/contacts/individuals/*"
							/>

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter data={AccountsList} />
									}
									path=":channelId?/contacts/accounts"
								/>
							)}

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter
											data={AccountProfileRoutes}
										/>
									}
									path=":channelId?/contacts/accounts/:id/*"
								/>
							)}

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter
											data={CampaignsDashboard}
											destructured={false}
										/>
									}
									path=":channelId?/campaigns"
								/>
							)}

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter
											data={CampaignDetail}
											destructured={false}
										/>
									}
									path=":channelId?/campaigns/:id"
								/>
							)}

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter
											data={LifecycleCreate}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle/new"
								/>
							)}

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter
											data={LifecycleEdit}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle/:lifecycleId/edit"
								/>
							)}

							{LDPEnabled && (
								<Route
									element={
										<BundleRouter
											data={LifecycleDashboard}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle"
								/>
							)}

							<Route
								element={<BundleRouter data={SegmentsList} />}
								path=":channelId?/contacts/segments"
							/>

							<Route
								element={<BundleRouter data={SegmentEdit} />}
								path=":channelId?/contacts/segments/:id/edit"
							/>

							<Route
								element={<BundleRouter data={SegmentEdit} />}
								path=":channelId?/contacts/segments/create"
							/>

							<Route
								element={
									<BundleRouter data={SegmentProfileRoutes} />
								}
								path=":channelId?/contacts/segments/:id/*"
							/>

							<Route
								element={
									<BundleRouter
										data={AssetDashboard}
										destructured={false}
									/>
								}
								path=":channelId?/assets/:assetType/:assetId/:tabId/:touchpoint/:title?/:type?"
							/>

							<Route
								element={
									<BundleRouter
										data={TouchpointRoutes}
										destructured={false}
									/>
								}
								path=":channelId?/sites/pages/:touchpointType/:touchpoint/:title?"
							/>

							<Route
								element={
									<BundleRouter
										data={EventAnalysisList}
										destructured={false}
									/>
								}
								path=":channelId?/event-analysis"
							/>

							<Route
								element={
									<BundleRouter
										data={EventAnalysisCreate}
										destructured={false}
									/>
								}
								path=":channelId?/event-analysis/create"
							/>

							<Route
								element={
									<BundleRouter
										data={EventAnalysisEdit}
										destructured={false}
									/>
								}
								path=":channelId?/event-analysis/:id"
							/>

							<Route
								element={
									<BundleRouter
										data={ExperimentsList}
										destructured={false}
									/>
								}
								path=":channelId?/tests"
							/>

							<Route
								element={
									<BundleRouter
										data={ExperimentOverview}
										destructured={false}
									/>
								}
								path=":channelId?/tests/overview/:id"
							/>

							<Route
								element={
									<BundleRouter
										data={NewAssetsList}
										destructured={false}
									/>
								}
								path=":channelId?/assets/*"
							/>

							<Route
								element={
									<BundleRouter
										data={SitesDashboard}
										destructured={false}
									/>
								}
								path=":channelId?/sites/*"
							/>

							<Route
								element={
									<BundleRouter
										data={SitesDashboard}
										destructured={false}
									/>
								}
								path=":channelId?"
							/>

							{DEVELOPER_MODE && (
								<Route
									element={<BundleRouter data={UIKit} />}
									path=":channelId?/ui-kit/:name?"
								/>
							)}

							<Route element={<ErrorPage />} path="*" />
						</RouterRoutes>
					) : (
						<RouterRoutes>
							<Route
								element={
									<BundleRouter
										componentProps={{currentUser, groupId}}
										data={NoPropertiesAvailable}
									/>
								}
								path="*"
							/>
						</RouterRoutes>
					)}
				</Suspense>
			</DownloadReportProvider>
		</DataSourcesProvider>
	);
};

export default compose(
	withChannelId,
	withSidebar,
	withOnboarding,
	withUnassignedSegments,
	withLDPEnabled,
	connect((store, {groupId}) => ({
		project: store.getIn(['projects', groupId, 'data'])
	}))
)(AppSidebarRoutes);
