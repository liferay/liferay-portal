import BundleRouter from 'route-middleware/BundleRouter';
import checkProjectState from 'shared/hoc/CheckProjectState';
import Loading from 'shared/components/Loading';
import React, {Fragment, lazy, Suspense} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {compose} from 'shared/hoc';
import {Routes} from 'shared/util/router';
import {Switch, useParams} from 'react-router-dom';
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
	const {groupId} = useParams<{groupId: string}>();
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
			<Switch>
				<BundleRouter
					data={DataSourceList}
					exact
					path={Routes.SETTINGS_DATA_SOURCE_LIST}
				/>

				<BundleRouter
					data={DataSourceOnboarding}
					exact
					path={Routes.SETTINGS_DATA_SOURCE_ONBOARDING}
				/>

				<BundleRouter
					data={DeleteDataSource}
					path={Routes.SETTINGS_DATA_SOURCE_DELETE}
				/>

				<BundleRouter
					data={DataSourceEdit}
					exact
					path={Routes.SETTINGS_DATA_SOURCE_EDIT}
				/>

				<BundleRouter
					data={DataSource}
					path={Routes.SETTINGS_DATA_SOURCE}
				/>

				<BundleRouter data={Users} path={Routes.SETTINGS_USERS} />

				{!IS_PROJECT_SAAS && (
					<BundleRouter
						data={UsageOverview}
						exact
						path={Routes.SETTINGS_USAGE}
					/>
				)}

				{IS_PROJECT_SAAS && (
					<BundleRouter
						data={UsageOverviewExternal}
						exact
						path={Routes.SETTINGS_USAGE}
					/>
				)}

				<BundleRouter
					data={Definitions}
					path={Routes.SETTINGS_DEFINITIONS}
				/>

				<BundleRouter
					data={DataPrivacy}
					path={Routes.SETTINGS_DATA_PRIVACY}
				/>

				<BundleRouter
					data={WorkspaceSettings}
					path={Routes.SETTINGS_WORKSPACE}
				/>

				<BundleRouter
					data={ChannelView}
					exact
					path={Routes.SETTINGS_CHANNELS_VIEW}
				/>

				<BundleRouter
					data={ChannelList}
					path={Routes.SETTINGS_CHANNELS}
				/>

				<BundleRouter data={Apis} path={Routes.SETTINGS_APIS} />

				{recommendationsEnabled && (
					<Fragment key="RECOMMENDATIONS">
						<BundleRouter
							data={RecommendationList}
							destructured={false}
							exact
							path={Routes.SETTINGS_RECOMMENDATIONS}
						/>

						<BundleRouter
							data={RecommendationCreateItemSimilarity}
							destructured={false}
							exact
							path={
								Routes.SETTINGS_RECOMMENDATIONS_CREATE_ITEM_SIMILARITY_MODEL
							}
						/>

						<BundleRouter
							data={RecommendationEdit}
							destructured={false}
							exact
							path={Routes.SETTINGS_RECOMMENDATION_EDIT}
						/>

						<BundleRouter
							data={RecommendationView}
							destructured={false}
							exact
							path={Routes.SETTINGS_RECOMMENDATION_MODEL_VIEW}
						/>
					</Fragment>
				)}

				<BundleRouter
					data={FeatureFlags}
					exact
					path={Routes.SETTINGS_FEATURE_FLAGS}
				/>

				<RouteNotFound />
			</Switch>
		</Suspense>
	);
};

export default compose<any>(checkProjectState, withOnboarding)(Settings);
