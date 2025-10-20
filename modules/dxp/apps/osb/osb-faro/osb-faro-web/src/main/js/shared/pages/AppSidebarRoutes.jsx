import BundleRouter from '../../route-middleware/BundleRouter';
import Loading from 'shared/components/Loading';
import React, {lazy, Suspense} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {ChannelContext} from 'shared/context/channel';
import {connect} from 'react-redux';
import {DEVELOPER_MODE, ENABLE_ACCOUNTS} from 'shared/util/constants';
import {DownloadReportProvider} from 'shared/components/download-report/DownloadReportContext';
import {Routes} from 'shared/util/router';
import {Switch, withRouter} from 'react-router-dom';
import {withOnboarding, withUnassignedSegments} from 'shared/hoc';
import {withSidebar} from 'shared/hoc';

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
const IndividualsDashboard = lazy(() =>
	import(
		/* webpackChunkName: "IndividualsDashboard" */ '../../individual/dashboard/pages'
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

const AssetsList = lazy(() =>
	import(/* webpackChunkName: "AssetsList" */ 'assets/pages')
);

const Blog = lazy(() =>
	import(/* webpackChunkName: "Blog" */ 'assets/blog/pages')
);

const CustomAssetsDashboard = lazy(() =>
	import(
		/* webpackChunkName: "CustomAssetsDashboard" */ 'assets/custom-asset/pages/Dashboard'
	)
);

const DocumentAndMedia = lazy(() =>
	import(
		/* webpackChunkName: "DocumentAndMedia" */ 'assets/document-and-media/pages'
	)
);

const Form = lazy(() =>
	import(/* webpackChunkName: "Form" */ 'assets/form/pages')
);

const WebContent = lazy(() =>
	import(/* webpackChunkName: "WebContent" */ 'assets/web-content/pages')
);

/* Commmerce */

const CommerceDashboard = lazy(() =>
	import(/* webpackChunkName: "CommerceDashboard" */ 'commerce/pages')
);

const ROUTES = [
	ENABLE_ACCOUNTS && {
		data: AccountsList,
		path: Routes.CONTACTS_LIST_ACCOUNT
	},
	{
		data: AccountProfileRoutes,
		exact: false,
		path: Routes.CONTACTS_ACCOUNT
	},
	{
		data: IndividualProfileRoutes,
		exact: false,
		path: Routes.CONTACTS_INDIVIDUAL
	},
	{
		data: IndividualsDashboard,
		destructured: false,
		exact: false,
		path: Routes.CONTACTS_INDIVIDUALS
	},
	{
		data: SegmentsList,
		path: Routes.CONTACTS_LIST_SEGMENT
	},
	{
		data: SegmentEdit,
		path: Routes.CONTACTS_SEGMENT_EDIT
	},
	{
		data: SegmentEdit,
		path: Routes.CONTACTS_SEGMENT_CREATE
	},
	{
		data: SegmentProfileRoutes,
		exact: false,
		path: Routes.CONTACTS_SEGMENT
	},
	{
		data: Blog,
		destructured: false,
		path: Routes.ASSETS_BLOGS_ROUTES
	},
	{
		data: CustomAssetsDashboard,
		destructured: false,
		path: Routes.ASSETS_CUSTOM_DASHBOARD
	},
	{
		data: DocumentAndMedia,
		destructured: false,
		exact: false,
		path: Routes.ASSETS_DOCUMENTS_AND_MEDIA_ROUTES
	},
	{
		data: Form,
		destructured: false,
		exact: false,
		path: Routes.ASSETS_FORMS_ROUTES
	},
	{
		data: WebContent,
		destructured: false,
		exact: false,
		path: Routes.ASSETS_WEB_CONTENT_ROUTES
	},
	{
		data: TouchpointRoutes,
		destructured: false,
		exact: false,
		path: Routes.SITES_TOUCHPOINTS_ROUTES
	},
	{
		data: EventAnalysisList,
		destructured: false,
		exact: true,
		path: Routes.EVENT_ANALYSIS
	},
	{
		data: EventAnalysisCreate,
		destructured: false,
		exact: true,
		path: Routes.EVENT_ANALYSIS_CREATE
	},
	{
		data: EventAnalysisEdit,
		destructured: false,
		exact: true,
		path: Routes.EVENT_ANALYSIS_EDIT
	},
	{
		data: ExperimentsList,
		destructured: false,
		path: Routes.TESTS
	},
	{
		data: ExperimentOverview,
		destructured: false,
		path: Routes.TESTS_OVERVIEW
	},
	{
		data: AssetsList,
		destructured: false,
		exact: false,
		path: Routes.ASSETS
	},
	{
		data: SitesDashboard,
		destructured: false,
		exact: false,
		path: Routes.SITES
	},
	{
		data: SitesDashboard,
		destructured: false,
		path: Routes.CHANNEL
	},
	DEVELOPER_MODE && {
		data: CommerceDashboard,
		destructured: false,
		path: Routes.COMMERCE
	}
].filter(Boolean);

@withRouter
@withSidebar
@withOnboarding
@withUnassignedSegments
@connect((store, {groupId}) => ({
	project: store.getIn(['projects', groupId, 'data'])
}))
export default class AppSidebarRoutes extends React.PureComponent {
	static contextType = ChannelContext;

	render() {
		const {currentUser, groupId} = this.props;
		const {selectedChannel} = this.context;

		return (
			<DownloadReportProvider>
				<Suspense fallback={<Loading />}>
					<Switch>
						{!selectedChannel && (
							<BundleRouter
								componentProps={{currentUser, groupId}}
								data={NoPropertiesAvailable}
								exact={false}
								path={Routes.WORKSPACE_WITH_ID}
							/>
						)}

						{ROUTES.map(
							({data, exact = true, path, ...otherProps}) => (
								<BundleRouter
									{...otherProps}
									data={data}
									exact={exact}
									key={path}
									path={path}
								/>
							)
						)}

						{DEVELOPER_MODE && (
							<BundleRouter
								data={UIKit}
								exact
								path={Routes.UI_KIT}
							/>
						)}

						<RouteNotFound />
					</Switch>
				</Suspense>
			</DownloadReportProvider>
		);
	}
}
