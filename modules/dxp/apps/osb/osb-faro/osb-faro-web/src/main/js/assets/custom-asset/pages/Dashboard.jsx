import * as breadcrumbs from 'shared/util/breadcrumbs';
import AddReport from '../components/AddReport';
import AssetCard from '../hocs/AssetCard';
import autobind from 'autobind-decorator';
import BasePage from 'shared/components/base-page';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayPopover from '@clayui/popover';
import CustomAssetsDashboardQuery from 'shared/queries/CustomAssetsDashboardQuery';
import CustomAssetsReportMutation from 'shared/queries/CustomAssetsReportMutation';
import getQuery from 'shared/queries/custom-asset-query';
import React from 'react';
import TimeRangeQuery from 'shared/queries/TimeRangeQuery';
import URLConstants from 'shared/util/url-constants';
import withCurrentUser from 'shared/hoc/WithCurrentUser';
import {addAlert} from 'shared/actions/alerts';
import {Alert} from 'shared/types';
import {ChannelContext} from 'shared/context/channel';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {getRangeSelectorsFromQuery} from 'shared/util/util';
import {graphql} from '@apollo/react-hoc';
import {hasChanges} from 'shared/util/react';
import {PropTypes} from 'prop-types';
import {sub} from 'shared/util/lang';

/**
 * Blogs Dashboard Page
 * @class
 */
class CustomAssetsDashboardPage extends React.Component {
	static contextType = ChannelContext;

	static propTypes = {
		/**
		 * @type {function}
		 * @default undefined
		 */
		addAlert: PropTypes.func.isRequired,

		/**
		 * @type {object}
		 * @default undefined
		 */
		currentUser: PropTypes.object.isRequired,

		/**
		 * @type {string}
		 * @default false
		 */
		definition: PropTypes.string,

		/**
		 * @type {function}
		 * @default undefined
		 */
		mutate: PropTypes.func.isRequired,

		/**
		 * @type {object}
		 * @default undefined
		 */
		router: PropTypes.object.isRequired
	};

	state = {
		definition: {rows: []},

		/**
		 * @type {object}
		 * @default {}
		 */
		filters: {}
	};

	componentDidMount() {
		const {definition} = this.props;

		if (definition) {
			this.updateDefinition(definition);
		}
	}

	componentDidUpdate(prevProps) {
		const {definition} = this.props;

		if (hasChanges(prevProps, this.props, 'definition') && definition) {
			this.updateDefinition(definition);
		}
	}

	getDefinition({chartType, metric, title}) {
		let {definition} = this.state;

		definition = {
			rows: [
				...definition.rows,
				{
					panels: [
						{
							chartType,
							metric,
							title,
							width: 100
						}
					]
				}
			]
		};

		return JSON.stringify(definition);
	}

	updateDefinition(definition) {
		this.setState({
			definition: JSON.parse(definition)
		});
	}

	getColumn(width) {
		return `col-sm-${(12 * width) / 100}`;
	}

	@autobind
	handleDeleteReport(id) {
		const {
			props: {
				addAlert,
				currentUser: {id: modifiedByUserId, name: modifiedByUserName},
				mutate,
				router: {
					params: {id: dashboardId}
				}
			},
			state: {definition}
		} = this;

		const excludedItem = definition.rows[id];
		const previousDefinition = {...definition};
		const rows = definition.rows.filter((item, index) => index !== id);

		this.setState({definition: {rows}});

		mutate({
			variables: {
				dashboardId,
				definition: JSON.stringify({rows}),
				modifiedByUserId,
				modifiedByUserName
			}
		})
			.then(
				({
					data: {
						dashboard: {definition}
					}
				}) => {
					this.setState({
						definition: JSON.parse(definition)
					});

					addAlert({
						alertType: Alert.Types.Success,
						message: sub(
							Liferay.Language.get(
								'x-has-been-deleted-from-this-dashboard'
							),
							[excludedItem.panels[0].title]
						)
					});
				}
			)
			.catch(() => {
				this.setState({definition: previousDefinition});

				addAlert({
					alertType: Alert.Types.Error,
					message: Liferay.Language.get(
						'an-unexpected-error-occurred'
					)
				});
			});
	}

	@autobind
	handleGetReport(report) {
		const {currentUser, mutate, router} = this.props;
		const {id: dashboardId} = router.params;
		const {id: modifiedByUserId, name: modifiedByUserName} = currentUser;

		mutate({
			variables: {
				dashboardId,
				definition: this.getDefinition(report),
				modifiedByUserId,
				modifiedByUserName
			}
		}).then(({data}) => {
			const {definition} = data.dashboard;

			this.setState({
				definition: JSON.parse(definition)
			});
		});
	}

	renderDefinitions() {
		const {
			props: {router},
			state: {definition}
		} = this;

		const {id: dashboardId} = router.params;

		return definition.rows.map(({panels}, rowIndex) => (
			<div className='row' key={rowIndex}>
				{panels.map(({chartType, metric, title, width}, panelIndex) => (
					<div className={this.getColumn(width)} key={panelIndex}>
						<AssetCard
							assetId={dashboardId}
							id={rowIndex}
							itemQuery={getQuery(metric)}
							label={title}
							legacyDropdownRangeKey={false}
							onRemoveAsset={this.handleDeleteReport}
							panel={{chartType, metric}}
							rangeSelectors={getRangeSelectorsFromQuery(
								router.query
							)}
							router={router}
						/>
					</div>
				))}
			</div>
		));
	}

	renderAddReport() {
		const {definition} = this.state;

		return (
			<div className='row'>
				<div className='col-sm-12'>
					<AddReport
						isEmptyDashboard={!definition.rows.length}
						onGetReport={this.handleGetReport}
					/>
				</div>
			</div>
		);
	}

	renderLimitExecededText() {
		return (
			<div className='row'>
				<div className='col-sm-12'>
					<div className='mt-3 mb-3 text-secondary text-center'>
						<ClayIcon className='icon-root mr-2' symbol='warning' />

						{Liferay.Language.get(
							'this-dashboard-has-reached-the-limit-of-10-reports'
						)}
					</div>
				</div>
			</div>
		);
	}

	/**
	 * Lifecycle Render - ReactJS
	 */
	render() {
		const {
			context: {selectedChannel},
			props: {router},
			state: {definition, filters}
		} = this;

		const {
			params: {channelId, groupId, title}
		} = router;

		const decodedTitle = decodeURIComponent(title);

		return (
			<BasePage documentTitle={Liferay.Language.get('assets')}>
				<BasePage.Header
					breadcrumbs={[
						breadcrumbs.getHome({
							channelId,
							groupId,
							label: selectedChannel && selectedChannel.name
						}),
						breadcrumbs.getAssets({channelId, groupId}),
						breadcrumbs.getCustomContent({channelId, groupId}),
						breadcrumbs.getEntityName({label: decodedTitle})
					]}
					groupId={groupId}
				>
					<BasePage.Header.TitleSection
						deprecated
						title={decodedTitle}
					>
						<ClayPopover
							alignPosition='bottom'
							closeOnClickOutside
							header={Liferay.Language.get('deprecated-feature')}
							trigger={
								<ClayButton
									data-tooltip
									data-tooltip-align='top'
									displayType='warning'
									title={Liferay.Language.get(
										'open-deprecated-definition'
									)}
									translucent
								>
									{Liferay.Language.get(
										'deprecated'
									).toUpperCase()}
									<span className='inline-item inline-item-before pl-2'>
										<ClayIcon symbol='warning-full' />
									</span>
								</ClayButton>
							}
						>
							{Liferay.Language.get('this-feature-is-deprecated')}

							<ClayLink
								className='ml-1'
								decoration='underline'
								href={
									URLConstants.MaintenanceModeAndDeprecationDocumentation
								}
								key='deprecated'
								target='_blank'
							>
								{Liferay.Language.get(
									'learn-more-about-deprecated-features'
								)}
							</ClayLink>
						</ClayPopover>
					</BasePage.Header.TitleSection>
				</BasePage.Header>

				<BasePage.Context.Provider value={{filters, router}}>
					<BasePage.Body>
						{this.renderDefinitions()}

						{definition.rows.length < 10
							? this.renderAddReport()
							: this.renderLimitExecededText()}
					</BasePage.Body>
				</BasePage.Context.Provider>
			</BasePage>
		);
	}
}

const withTimeRangeQuery = () =>
	graphql(TimeRangeQuery, {
		props: ({data: {loading}}) => ({
			loadingTimeRange: loading
		})
	});

const withCustomAssetsDashboardData = () =>
	graphql(CustomAssetsDashboardQuery, {
		options: ({router}) => {
			const {id: dashboardId} = router.params;

			return {
				variables: {
					dashboardId
				}
			};
		},
		props: ({data: {dashboard = {}, loading}}) => {
			const {definition} = dashboard;

			return {
				definition,
				loadingDefinition: loading
			};
		}
	});

const withCustomAssetsReportMutation = () =>
	graphql(CustomAssetsReportMutation, {});

const WrappedPageComponent = ({
	loadingDefinition,
	loadingTimeRange,
	...props
}) => {
	if (loadingTimeRange || loadingDefinition) return null;

	return <CustomAssetsDashboardPage {...props} />;
};

export default compose(
	withTimeRangeQuery(),
	withCustomAssetsDashboardData(),
	withCustomAssetsReportMutation(),
	withCurrentUser,
	connect(null, {addAlert})
)(WrappedPageComponent);
