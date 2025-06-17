import * as API from 'shared/api';
import autobind from 'autobind-decorator';
import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import getCN from 'classnames';
import Loading from 'shared/components/Loading';
import React from 'react';
import TextTruncate from 'shared/components/TextTruncate';
import {autoCancel, hasRequest} from 'shared/util/request-decorator';
import {ProjectStates} from 'shared/util/constants';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';
import {withHistory} from 'shared/hoc';

@withHistory
@hasRequest
export default class WorkspaceListItem extends React.Component {
	static propTypes = {
		accountName: PropTypes.string,
		configured: PropTypes.bool,
		corpProjectName: PropTypes.string,
		disabled: PropTypes.bool,
		groupId: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
		history: PropTypes.object,
		href: PropTypes.string,
		isJoinableProjects: PropTypes.bool,
		name: PropTypes.string,
		planInfo: PropTypes.string,
		projectState: PropTypes.oneOf(Object.values(ProjectStates)),
		requested: PropTypes.bool
	};

	state = {
		loading: false,
		requested: false
	};

	constructor(props) {
		super(props);

		this.state = {
			...this.state,
			projectState: this.props.projectState,
			requested: this.props.requested
		};
	}

	@autobind
	handleActivate() {
		const {groupId, history} = this.props;

		this.setState({loading: true});

		return API.projects
			.activate({groupId})
			.then(() => {
				this.setState(
					{
						loading: false
					},
					() => {
						history.push(
							toRoute(Routes.WORKSPACE_WITH_ID, {groupId})
						);
					}
				);
			})
			.catch(err => {
				if (!err.IS_CANCELLATION_ERROR) {
					this.setState({
						loading: false
					});
				}
			});
	}

	@autoCancel
	@autobind
	handleGetProjectState() {
		const {groupId} = this.props;

		this.setState({
			loading: true
		});

		return API.projects
			.fetch({groupId})
			.then(({state}) => {
				this.setState({
					loading: false,
					projectState: state
				});
			})
			.catch(err => {
				if (!err.IS_CANCELLATION_ERROR) {
					this.setState({
						loading: false
					});
				}
			});
	}

	@autoCancel
	@autobind
	handleSendRequestAccess() {
		const {groupId} = this.props;

		return API.projects
			.sendRequestAccess(groupId)
			.then(() => {
				this.setState({
					requested: true
				});
			})
			.catch(() => {
				this.setState({
					requested: false
				});
			});
	}

	renderAction() {
		const {
			props: {isJoinableProjects},
			state: {projectState}
		} = this;

		const available = projectState !== ProjectStates.Unavailable;
		const deactivated = projectState === ProjectStates.Deactivated;

		if (isJoinableProjects) {
			return this.renderProjectJoin();
		} else if (deactivated) {
			return (
				<ClayButton
					className='button-root'
					displayType='secondary'
					onClick={this.handleActivate}
					size='sm'
				>
					{Liferay.Language.get('activate')}
				</ClayButton>
			);
		}

		return (
			<ClayIcon
				className='icon-root'
				symbol={available ? 'angle-right-small' : 'reload'}
			/>
		);
	}

	@autobind
	renderContent() {
		const {configured, corpProjectName, name} = this.props;

		return (
			<div className='autofit-col autofit-col-expand'>
				<div className='col-content-wrapper'>
					<div className='text-truncate'>
						<div className='workspace-name'>
							{!configured && (
								<div className='info-wrapper configuration-required'>
									{Liferay.Language.get(
										'finish-setting-up-workspace'
									)}
								</div>
							)}

							<TextTruncate title={name} />
						</div>

						{corpProjectName && (
							<div className='info-wrapper corporation-project-name'>
								{corpProjectName}
							</div>
						)}

						{this.renderMessage()}
					</div>

					{this.renderAction()}
				</div>
			</div>
		);
	}

	renderMessage() {
		const {
			props: {accountName, isJoinableProjects, planInfo},
			state: {projectState}
		} = this;

		const available = projectState !== ProjectStates.Unavailable;
		const deactivated = projectState === ProjectStates.Deactivated;

		if (isJoinableProjects) {
			return (
				<div className='workspace-info'>
					{Liferay.Language.get('requires-access')}
				</div>
			);
		} else if (deactivated) {
			return (
				<div className='workspace-info'>
					<div className='text-danger'>
						{Liferay.Language.get(
							'workspace-has-been-deactivated.-please-click-activate-to-reactivate'
						)}
					</div>
				</div>
			);
		}

		return (
			<div className='workspace-info'>
				{!available ? (
					<div className='text-danger'>
						{Liferay.Language.get(
							'workspace-unavailable-;-click-to-reload'
						)}
					</div>
				) : (
					planInfo || accountName
				)}
			</div>
		);
	}

	@autobind
	renderProjectJoin() {
		const {requested} = this.state;

		if (requested) {
			return (
				<div className='icon-request-workspace'>
					<span className='pr-2'>
						{Liferay.Language.get('access-requested')}
					</span>
					<ClayIcon
						className='icon-root icon-size-md'
						symbol='envelope_close'
					/>
				</div>
			);
		} else {
			return (
				<ClayButton
					className='button-root'
					displayType='secondary'
					onClick={this.handleSendRequestAccess}
					size='sm'
				>
					{Liferay.Language.get('request-access')}
				</ClayButton>
			);
		}
	}

	render() {
		const {
			props: {className, disabled, href, isJoinableProjects, name},
			state: {loading, projectState}
		} = this;

		const available = projectState !== ProjectStates.Unavailable;
		const deactivated = projectState === ProjectStates.Deactivated;

		const classes = getCN(
			'list-group-item',
			'list-group-item-action',
			'list-group-item-flex',
			className,
			{
				disabled
			}
		);

		const contentClasses = getCN('button-root', 'workspace-link', {
			'border-button': !isJoinableProjects,
			'request-workspace': isJoinableProjects,
			'workspace-unavailable': !available || deactivated
		});

		const buttonAction = available
			? {href}
			: {onClick: this.handleGetProjectState};

		const Button = href ? ClayLink : ClayButton;

		return (
			<li className={classes} key={name}>
				{isJoinableProjects || deactivated ? (
					<div className={contentClasses}>{this.renderContent()}</div>
				) : (
					<Button
						button
						className={contentClasses}
						disabled={disabled || loading}
						displayType='unstyled'
						{...buttonAction}
					>
						{this.renderContent()}
					</Button>
				)}
				{loading && <Loading />}
			</li>
		);
	}
}
