import BundleRouter from 'route-middleware/BundleRouter';
import EditLayout from 'shared/components/EditLayout';
import ErrorPage from 'shared/pages/ErrorPage';
import LDPLayout, {LDPElement} from 'shared/components/LDPLayout';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import SegmentLinkRedirect from 'shared/components/SegmentLinkRedirect';
import SidebarLayout from 'shared/components/SidebarLayout';
import WorkspaceGate from 'shared/components/WorkspaceGate';
import WorkspaceHomeRedirect from 'shared/components/WorkspaceHomeRedirect';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {Route, Routes as RouterRoutes, useParams} from 'react-router-dom';
import {Project} from 'shared/util/records';
import {RootState} from 'shared/store';
import {useModalNotifications} from 'shared/hooks/useModalNotifications';
import {withHelpWidget} from 'shared/hoc';

const Settings = lazy(
	() => import(/* webpackChunkName: "Settings" */ 'settings/pages/Settings')
);

/* Accounts */

const AccountProfileRoutes = lazy(
	() =>
		import(

			/* webpackChunkName: "AccountProfileRoutes" */ 'contacts/pages/account/ProfileRoutes'
		)
);
const AccountsList = lazy(
	() =>
		import(

			/* webpackChunkName: "AccountsList" */ 'contacts/pages/account/List'
		)
);

/* Assets */

const AssetDashboard = lazy(
	() =>
		import(

			/* webpackChunkName: "AssetDashboard" */ 'assets/pages/Dashboard'
		)
);
const NewAssetsList = lazy(
	() => import(/* webpackChunkName: "NewAssetsList" */ 'assets/pages/List')
);

/* Campaigns */

const CampaignDetail = lazy(
	() =>
		import(

			/* webpackChunkName: "CampaignDetail" */ '../../campaigns/pages/CampaignDetail'
		)
);
const CampaignsDashboard = lazy(
	() =>
		import(

			/* webpackChunkName: "CampaignsDashboard" */ '../../campaigns/pages'
		)
);

/* Event Analysis */

const EventAnalysisCreate = lazy(
	() =>
		import(

			/* webpackChunkName: "EventAnalysisCreate" */ 'event-analysis/pages/Create'
		)
);
const EventAnalysisEdit = lazy(
	() =>
		import(

			/* webpackChunkName: "EventAnalysisEdit" */ 'event-analysis/pages/Edit'
		)
);
const EventAnalysisList = lazy(
	() =>
		import(

			/* webpackChunkName: "EventAnalysisList" */ 'event-analysis/pages/List'
		)
);

/* Experiments */

const ExperimentOverview = lazy(
	() =>
		import(

			/* webpackChunkName: "ExperimentsList" */ 'experiments/pages/ExperimentOverviewPage'
		)
);
const ExperimentsList = lazy(
	() =>
		import(

			/* webpackChunkName: "ExperimentsList" */ 'experiments/pages/ExperimentsListPage'
		)
);

/* Individuals */

const IndividualProfileRoutes = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualProfileRoutes" */ 'individual/profile/pages/ProfileRoutes'
		)
);
const IndividualProfileRoutesCDP = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualProfileRoutesCDP" */ 'individual/profile/pages/ProfileRoutesCDP'
		)
);
const IndividualsDashboard = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualsDashboard" */ 'individual/dashboard/pages'
		)
);
const IndividualsDashboardCDP = lazy(
	() =>
		import(

			/* webpackChunkName: "IndividualsDashboardCDP" */ 'individual/dashboard/pages/IndividualsDashboardCDP'
		)
);

/* Lifecycle */

const LifecycleCreate = lazy(
	() =>
		import(

			/* webpackChunkName: "LifecycleCreate" */ 'lifecycle/pages/CreateLifecycle'
		)
);
const LifecycleDashboard = lazy(
	() =>
		import(

			/* webpackChunkName: "LifecycleDashboard" */ 'lifecycle/pages/BaseLifecycle'
		)
);
const LifecycleEdit = lazy(
	() =>
		import(

			/* webpackChunkName: "LifecycleEdit" */ 'lifecycle/pages/EditLifecycle'
		)
);
const LifecycleRedirect = lazy(
	() =>
		import(

			/* webpackChunkName: "LifecycleRedirect" */ 'lifecycle/pages/LifecycleRedirect'
		)
);

/* Segments */

const SegmentEdit = lazy(
	() => import(/* webpackChunkName: "SegmentEdit" */ 'segment/pages/Edit')
);
const SegmentProfileRoutes = lazy(
	() =>
		import(

			/* webpackChunkName: "SegmentProfileRoutes" */ 'segment/pages/ProfileRoutes'
		)
);
const SegmentsList = lazy(
	() =>
		import(

			/* webpackChunkName: "SegmentsList" */ 'segment/pages/List'
		) as Promise<any>
);

/* Sites */

const SitesDashboard = lazy(
	() => import(/* webpackChunkName: "SitesDashboard" */ 'sites/pages')
);
const TouchpointRoutes = lazy(
	() =>
		import(

			/* webpackChunkName: "TouchpointRoutes" */ 'sites/touchpoints/pages/TouchpointRoutes'
		) as Promise<any>
);

const connector = connect((store: RootState, {groupId}: {groupId: string}) => {
	const project =
		store.getIn(['projects', groupId, 'data'], new Project()) ||
		new Project();

	const faroSubscriptionIMap = project.get('faroSubscription');

	return {
		currentUserId: String(store.getIn(['currentUser', 'data'])),
		groupId,
		serverLocation: project.get('serverLocation'),
		subscriptionName: faroSubscriptionIMap.get('name'),
		workspaceName: project.get('name'),
	};
});

const WorkspaceLayer = ({groupId}: {groupId: string}) => {
	const modalNotification = useModalNotifications(groupId);

	return (
		<>
			<Suspense fallback={<Loading />}>
				<RouterRoutes>
					<Route
						element={<BundleRouter data={Settings} />}
						path="settings/*"
					/>

					<Route element={<WorkspaceGate key={groupId} />}>
						<Route element={<EditLayout />}>
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

							<Route element={<LDPLayout />}>
								<Route
									element={
										<BundleRouter
											data={LifecycleCreate}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle/new"
								/>

								<Route
									element={
										<BundleRouter
											data={LifecycleEdit}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle/:lifecycleId/edit"
								/>
							</Route>
						</Route>

						<Route element={<SidebarLayout />}>
							<Route element={<WorkspaceHomeRedirect />} index />

							<Route
								element={
									<LDPElement
										fallback={
											<BundleRouter
												data={IndividualProfileRoutes}
											/>
										}
									>
										<BundleRouter
											data={IndividualProfileRoutesCDP}
										/>
									</LDPElement>
								}
								path=":channelId?/contacts/individuals/known-individuals/:id/*"
							/>

							<Route
								element={
									<LDPElement
										fallback={
											<BundleRouter
												data={IndividualsDashboard}
												destructured={false}
											/>
										}
									>
										<BundleRouter
											data={IndividualsDashboardCDP}
											destructured={false}
										/>
									</LDPElement>
								}
								path=":channelId?/contacts/individuals/*"
							/>

							<Route element={<LDPLayout />}>
								<Route
									element={
										<BundleRouter data={AccountsList} />
									}
									path=":channelId?/contacts/accounts"
								/>

								<Route
									element={
										<BundleRouter
											data={AccountProfileRoutes}
										/>
									}
									path=":channelId?/contacts/accounts/:id/*"
								/>

								<Route
									element={
										<BundleRouter
											data={CampaignsDashboard}
											destructured={false}
										/>
									}
									path=":channelId?/campaigns"
								/>

								<Route
									element={
										<BundleRouter
											data={CampaignDetail}
											destructured={false}
										/>
									}
									path=":channelId?/campaigns/:id"
								/>

								<Route
									element={
										<BundleRouter
											data={LifecycleRedirect}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle"
								/>

								<Route
									element={
										<BundleRouter
											data={LifecycleDashboard}
											destructured={false}
										/>
									}
									path=":channelId?/lifecycle/:lifecycleId"
								/>
							</Route>

							<Route
								element={<BundleRouter data={SegmentsList} />}
								path=":channelId?/contacts/segments"
							/>

							<Route
								element={
									<SegmentLinkRedirect>
										<BundleRouter
											data={SegmentProfileRoutes}
										/>
									</SegmentLinkRedirect>
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

							<Route
								element={<ErrorPage />}
								path=":channelId/*"
							/>
						</Route>
					</Route>
				</RouterRoutes>
			</Suspense>

			{modalNotification}
		</>
	);
};

const ConnectedWorkspaceLayer = compose<any>(
	connector,
	withHelpWidget
)(WorkspaceLayer);

const WorkspaceLayout = () => {
	const {groupId = '0'} = useParams<{groupId: string}>();

	return <ConnectedWorkspaceLayer groupId={groupId} />;
};

export default WorkspaceLayout;
