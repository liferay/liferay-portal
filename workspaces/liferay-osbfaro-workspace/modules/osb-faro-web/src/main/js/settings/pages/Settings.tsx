import BundleRouter from 'route-middleware/BundleRouter';
import checkProjectState from 'shared/hoc/CheckProjectState';
import ErrorPage from 'shared/pages/ErrorPage';
import Loading from 'shared/components/Loading';
import React, {Fragment, lazy, Suspense} from 'react';
import {compose} from 'shared/hoc';
import {Route, Routes as RouterRoutes, useParams} from 'react-router-dom';
import {useLDPEnabled} from 'shared/hooks/useLDPEnabled';
import {useStore} from 'react-redux';
import {withOnboarding} from 'shared/hoc';

// APIS

const Apis = lazy(() => import(/* webpackChunkName: "Apis" */ '../apis/pages'));

// Data Privacy

const DataPrivacy = lazy(
	() => import(/* webpackChunkName: "DataPrivacy" */ '../data-privacy/pages')
);

// Data source

const DataSource = lazy(
	() => import(/* webpackChunkName: "DataSource" */ './data-source/View')
);
const DataSourceEdit = lazy(
	() => import(/* webpackChunkName: "DataSourceEdit" */ './data-source/Edit')
);
const DataSourceOnboarding = lazy(
	() =>
		import(

			/* webpackChunkName: "DataSourceEdit" */ './data-source/Onboarding'
		)
);
const DataSourceList = lazy(
	() => import(/* webpackChunkName: "DataSourceList" */ './DataSourceList')
);
const DeleteDataSource = lazy(
	() =>
		import(

			/* webpackChunkName: "DeleteDataSource" */ './data-source/Delete'
		) as any
);

// Definitions

const Definitions = lazy(
	() => import(/* webpackChunkName: "Definitions" */ '../definitions/pages')
);

// Channels

const ChannelList = lazy(
	() =>
		import(

			/* webpackChunkName: "ChannelList" */ '../channels/pages/ChannelList'
		)
);

const ChannelView = lazy(
	() => import(/* webpackChunkName: "ChannelView" */ '../channels/pages/View')
);

// Recommendations

const RecommendationList = lazy(
	() =>
		import(

			/* webpackChunkName: "RecommendationList" */ '../recommendations/pages/Recommendations'
		)
);

const RecommendationCreateItemSimilarity = lazy(
	() =>
		import(

			/* webpackChunkName: "RecommendationCreateItemSimilarity" */ '../recommendations/pages/CreateItemSimilarity'
		)
);

const RecommendationEdit = lazy(
	() =>
		import(

			/* webpackChunkName: "RecommendationEdit" */ '../recommendations/pages/Edit'
		)
);

const RecommendationView = lazy(
	() =>
		import(

			/* webpackChunkName: "RecommendationView" */ '../recommendations/pages/View'
		)
);

// Other

const FeatureFlags = lazy(
	() => import(/* webpackChunkName: "FeatureFlags" */ './FeatureFlags')
);

const UsageOverview = lazy(
	() => import(/* webpackChunkName: "UsageOverview" */ './UsageOverview')
);

const UsageOverviewExternal = lazy(
	() =>
		import(

			/* webpackChunkName: "UsageOverviewExternal" */ './UsageOverviewExternal'
		)
);

const Users = lazy(() => import(/* webpackChunkName: "Users" */ './user'));

const WorkspaceSettings = lazy(
	() => import(/* webpackChunkName: "WorkspaceSettings" */ './Workspace')
);

export const Settings = () => {
	const {groupId = ''} = useParams();
	const store = useStore();
	const isLDP = useLDPEnabled({groupId});

	const project = store.getState().getIn(['projects', groupId, 'data']);
	const recommendationsEnabled = store
		.getState()
		.getIn(['projects', groupId, 'data', 'recommendationsEnabled'], false);

	const subscriptionName = project?.faroSubscription?.get('name');

	const IS_PROJECT_SAAS = subscriptionName?.includes('SaaS') || isLDP;

	return (
		<Suspense fallback={<Loading />}>
			<RouterRoutes>
				<Route
					element={<BundleRouter data={DataSourceList} />}
					path="data-source"
				/>

				<Route
					element={<BundleRouter data={DataSourceOnboarding} />}
					path="data-source/:id/onboarding"
				/>

				<Route
					element={<BundleRouter data={DeleteDataSource} />}
					path="data-source/:id/delete"
				/>

				<Route
					element={<BundleRouter data={DataSourceEdit} />}
					path="data-source/:id/edit"
				/>

				<Route
					element={<BundleRouter data={DataSource} />}
					path="data-source/:id/*"
				/>

				<Route element={<BundleRouter data={Users} />} path="users/*" />

				{!IS_PROJECT_SAAS && (
					<Route
						element={<BundleRouter data={UsageOverview} />}
						path="usage"
					/>
				)}

				{IS_PROJECT_SAAS && (
					<Route
						element={<BundleRouter data={UsageOverviewExternal} />}
						path="usage"
					/>
				)}

				<Route
					element={<BundleRouter data={Definitions} />}
					path="definitions/*"
				/>

				<Route
					element={<BundleRouter data={DataPrivacy} />}
					path="data-privacy/*"
				/>

				<Route
					element={<BundleRouter data={WorkspaceSettings} />}
					path="workspace/*"
				/>

				<Route
					element={<BundleRouter data={ChannelView} />}
					path="properties/:id"
				/>

				<Route
					element={<BundleRouter data={ChannelList} />}
					path="properties/*"
				/>

				<Route element={<BundleRouter data={Apis} />} path="apis/*" />

				{recommendationsEnabled && (
					<Fragment key="RECOMMENDATIONS">
						<Route
							element={
								<BundleRouter
									data={RecommendationList}
									destructured={false}
								/>
							}
							path="recommendations"
						/>

						<Route
							element={
								<BundleRouter
									data={RecommendationCreateItemSimilarity}
									destructured={false}
								/>
							}
							path="recommendations/create-item-similarity-model"
						/>

						<Route
							element={
								<BundleRouter
									data={RecommendationEdit}
									destructured={false}
								/>
							}
							path="recommendations/:jobId/edit"
						/>

						<Route
							element={
								<BundleRouter
									data={RecommendationView}
									destructured={false}
								/>
							}
							path="recommendations/:jobId"
						/>
					</Fragment>
				)}

				<Route
					element={<BundleRouter data={FeatureFlags} />}
					path="feature-flags"
				/>

				<Route element={<ErrorPage />} path="*" />
			</RouterRoutes>
		</Suspense>
	);
};

export default compose<any>(checkProjectState, withOnboarding)(Settings);
