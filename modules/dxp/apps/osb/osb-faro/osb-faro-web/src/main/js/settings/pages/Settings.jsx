import BundleRouter from 'route-middleware/BundleRouter';
import checkProjectState from 'shared/hoc/CheckProjectState';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React, {Fragment, lazy, Suspense} from 'react';
import RouteNotFound from 'shared/components/RouteNotFound';
import {compose} from 'shared/hoc';
import {connect} from 'react-redux';
import {ENABLE_CSVFILE} from 'shared/util/constants';
import {ENABLE_SALESFORCE} from 'shared/util/constants';
import {Link, matchPath, Switch, withRouter} from 'react-router-dom';
import {Project, User} from 'shared/util/records';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';
import {withOnboarding} from 'shared/hoc';

// APIS

const Apis = lazy(() => import(/* webpackChunkName: "Apis" */ '../apis/pages'));

// CSV data source

const ConfigureCSV = lazy(() =>
	import(/* webpackChunkName: "ConfigureCSV" */ './data-source/ConfigureCSV')
);
const UploadCSV = lazy(() =>
	import(/* webpackChunkName: "UploadCSV" */ './data-source/UploadCSV')
);

// Data Privacy

const DataPrivacy = lazy(() =>
	import(/* webpackChunkName: "DataPrivacy" */ '../data-privacy/pages')
);

// Data source

const AddDataSource = lazy(() =>
	import(/* webpackChunkName: "AddDataSource" */ './AddDataSource')
);
const DataSource = lazy(() =>
	import(/* webpackChunkName: "DataSource" */ './data-source/View')
);
const DataSourceEdit = lazy(() =>
	import(/* webpackChunkName: "DataSourceEdit" */ './data-source/Edit')
);
const DataSourceList = lazy(() =>
	import(/* webpackChunkName: "DataSourceList" */ './DataSourceList')
);
const DeleteDataSource = lazy(() =>
	import(/* webpackChunkName: "DeleteDataSource" */ './data-source/Delete')
);

// Definitions

const Definitions = lazy(() =>
	import(/* webpackChunkName: "Definitions" */ '../definitions/pages')
);

// Salesforce data source

const NewSalesforceDataSource = lazy(() =>
	import(
		/* webpackChunkName: "NewSalesforceDataSource" */ './data-source/salesforce/NewSalesforce'
	)
);

// Channels

const ChannelList = lazy(() =>
	import(
		/* webpackChunkName: "ChannelList" */ '../channels/pages/ChannelList'
	)
);

const ChannelView = lazy(() =>
	import(/* webpackChunkName: "ChannelView" */ '../channels/pages/View')
);

// Recommendations
const RecommendationList = lazy(() =>
	import(
		/* webpackChunkName: "RecommendationList" */ '../recommendations/pages/Recommendations'
	)
);

const RecommendationCreateItemSimilarity = lazy(() =>
	import(
		/* webpackChunkName: "RecommendationCreateItemSimilarity" */ '../recommendations/pages/CreateItemSimilarity'
	)
);

const RecommendationEdit = lazy(() =>
	import(
		/* webpackChunkName: "RecommendationEdit" */ '../recommendations/pages/Edit'
	)
);

const RecommendationView = lazy(() =>
	import(
		/* webpackChunkName: "RecommendationView" */ '../recommendations/pages/View'
	)
);

// Other

const UsageOverview = lazy(() =>
	import(/* webpackChunkName: "UsageOverview" */ './UsageOverview')
);

const UsageOverviewSaaS = lazy(() =>
	import(/* webpackChunkName: "UsageOverviewSaaS" */ './UsageOverviewSaaS')
);

const Users = lazy(() => import(/* webpackChunkName: "Users" */ './user'));

const WorkspaceSettings = lazy(() =>
	import(/* webpackChunkName: "WorkspaceSettings" */ './Workspace')
);

export class Settings extends React.Component {
	static defaultProps = {
		pageActions: []
	};

	static propTypes = {
		backURL: PropTypes.string,
		breadcrumb: PropTypes.shape({
			label: PropTypes.string,
			url: PropTypes.string
		}),
		currentUser: PropTypes.instanceOf(User),
		documentTitle: PropTypes.string,
		groupId: PropTypes.string.isRequired,
		location: PropTypes.object.isRequired,
		pageActions: PropTypes.array,
		pageDescription: PropTypes.node,
		pageTitle: PropTypes.node,
		passedChildren: PropTypes.node,
		project: PropTypes.instanceOf(Project),
		recommendationsEnabled: PropTypes.bool
	};

	getSidebarSections() {
		const {currentUser, groupId, recommendationsEnabled} = this.props;

		return [
			{
				items: [
					currentUser.isAdmin() && {
						icon: 'ac_api',
						label: Liferay.Language.get('apis'),
						route: Routes.SETTINGS_APIS_TOKEN_LIST,
						url: toRoute(Routes.SETTINGS_APIS_TOKEN_LIST, {
							groupId
						})
					},
					{
						icon: 'definitions',
						label: Liferay.Language.get('definitions'),
						route: Routes.SETTINGS_DEFINITIONS,
						url: toRoute(Routes.SETTINGS_DEFINITIONS, {groupId})
					},
					{
						icon: 'data_privacy_lock',
						label: Liferay.Language.get('data-control-&-privacy'),
						route: Routes.SETTINGS_DATA_PRIVACY,
						url: toRoute(Routes.SETTINGS_DATA_PRIVACY, {groupId})
					},
					{
						icon: 'faro_data_source',
						label: Liferay.Language.get('data-sources'),
						route: Routes.SETTINGS_DATA_SOURCE_LIST,
						url: toRoute(Routes.SETTINGS_DATA_SOURCE_LIST, {
							groupId
						})
					},
					recommendationsEnabled && {
						icon: 'ac_star',
						label: Liferay.Language.get('recommendations'),
						route: Routes.SETTINGS_RECOMMENDATIONS,
						url: toRoute(Routes.SETTINGS_RECOMMENDATIONS, {
							groupId
						})
					}
				].filter(Boolean),
				label: Liferay.Language.get('workspace-data')
			},
			{
				items: [
					{
						icon: 'user_management',
						label: Liferay.Language.get('user-management'),
						route: Routes.SETTINGS_USERS,
						url: toRoute(Routes.SETTINGS_USERS, {
							groupId
						})
					},
					{
						icon: 'ac_page',
						label: Liferay.Language.get('properties'),
						route: Routes.SETTINGS_CHANNELS,
						url: toRoute(Routes.SETTINGS_CHANNELS, {
							groupId
						})
					},
					{
						icon: 'usage',
						label: Liferay.Language.get('subscription-&-usage'),
						route: Routes.SETTINGS_USAGE,
						url: toRoute(Routes.SETTINGS_USAGE, {groupId})
					},
					{
						icon: 'cog',
						label: Liferay.Language.get('workspace'),
						route: Routes.SETTINGS_WORKSPACE,
						url: toRoute(Routes.SETTINGS_WORKSPACE, {groupId})
					}
				],
				label: Liferay.Language.get('workspace-settings')
			}
		];
	}

	render() {
		const {
			backURL,
			groupId,
			location: {pathname},
			project,
			recommendationsEnabled
		} = this.props;

		const IS_PROJECT_SAAS = project.faroSubscription
			?.get('name')
			?.includes('SaaS');

		return (
			<div className='settings-root'>
				<div className='settings-header'>
					<div className='section-side'>
						<Link
							className='back-arrow'
							to={
								backURL ||
								toRoute(Routes.WORKSPACE_WITH_ID, {groupId})
							}
						>
							<span className='icon-wrapper'>
								<ClayIcon
									className='icon-root'
									symbol='angle-left-small'
								/>
							</span>

							{Liferay.Language.get('exit-settings')}
						</Link>
					</div>

					<div className='section-main'>
						<h2 className='title'>
							{Liferay.Language.get('settings')}
						</h2>
					</div>
				</div>

				<div className='content-wrapper'>
					<nav className='section-side settings-side-bar'>
						{this.getSidebarSections().map(
							({items, label}, sectionIndex) => (
								<div key={sectionIndex}>
									{label && (
										<h5 className='section-title'>
											{label}
										</h5>
									)}

									<ul className='nav'>
										{items.map(
											({icon, label, route, url}) => (
												<li
													className={getCN('item', {
														active: !!matchPath(
															pathname,
															{path: route}
														)
													})}
													key={url}
												>
													<ClayLink
														className='button-root'
														href={url}
													>
														<span className='icon-wrapper'>
															<ClayIcon
																className='icon-root'
																symbol={icon}
															/>
														</span>

														{label}
													</ClayLink>
												</li>
											)
										)}
									</ul>
								</div>
							)
						)}
					</nav>

					<div className='content section-main'>
						<Suspense fallback={<Loading />}>
							<Switch>
								<BundleRouter
									data={DataSourceList}
									exact
									path={Routes.SETTINGS_DATA_SOURCE_LIST}
								/>

								<BundleRouter
									data={AddDataSource}
									exact
									path={Routes.SETTINGS_ADD_DATA_SOURCE}
								/>

								{ENABLE_SALESFORCE && (
									<BundleRouter
										data={NewSalesforceDataSource}
										exact
										path={Routes.SETTINGS_SALESFORCE_ADD}
									/>
								)}

								<BundleRouter
									data={DeleteDataSource}
									path={Routes.SETTINGS_DATA_SOURCE_DELETE}
								/>

								<BundleRouter
									data={DataSourceEdit}
									exact
									path={Routes.SETTINGS_DATA_SOURCE_EDIT}
								/>

								{ENABLE_CSVFILE && (
									<BundleRouter
										data={ConfigureCSV}
										exact
										path={
											Routes.SETTINGS_CSV_UPLOAD_CONFIGURE
										}
									/>
								)}

								{ENABLE_CSVFILE && (
									<BundleRouter
										data={UploadCSV}
										exact
										path={Routes.SETTINGS_CSV_UPLOAD}
									/>
								)}

								<BundleRouter
									data={DataSource}
									path={Routes.SETTINGS_DATA_SOURCE}
								/>

								<BundleRouter
									data={Users}
									path={Routes.SETTINGS_USERS}
								/>

								{!IS_PROJECT_SAAS && (
									<BundleRouter
										data={UsageOverview}
										exact
										path={Routes.SETTINGS_USAGE}
									/>
								)}

								{IS_PROJECT_SAAS && (
									<BundleRouter
										data={UsageOverviewSaaS}
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

								<BundleRouter
									data={Apis}
									path={Routes.SETTINGS_APIS}
								/>

								{recommendationsEnabled && (
									<Fragment key='RECOMMENDATIONS'>
										<BundleRouter
											data={RecommendationList}
											destructured={false}
											exact
											path={
												Routes.SETTINGS_RECOMMENDATIONS
											}
										/>

										<BundleRouter
											data={
												RecommendationCreateItemSimilarity
											}
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
											path={
												Routes.SETTINGS_RECOMMENDATION_EDIT
											}
										/>

										<BundleRouter
											data={RecommendationView}
											destructured={false}
											exact
											path={
												Routes.SETTINGS_RECOMMENDATION_MODEL_VIEW
											}
										/>
									</Fragment>
								)}

								<RouteNotFound />
							</Switch>
						</Suspense>
					</div>
				</div>
			</div>
		);
	}
}

export default compose(
	withRouter,
	checkProjectState,
	connect((store, {groupId}) => ({
		backURL: store.getIn(['settings', 'backURL']),
		project: store.getIn(['projects', groupId, 'data']),
		recommendationsEnabled: store.getIn(
			['projects', groupId, 'data', 'recommendationsEnabled'],
			false
		)
	})),
	withOnboarding
)(Settings);
