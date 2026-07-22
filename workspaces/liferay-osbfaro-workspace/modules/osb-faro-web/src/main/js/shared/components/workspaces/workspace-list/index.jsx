import getCN from 'classnames';
import React from 'react';
import WorkspaceListItem from './ListItem';
import {DataSourceStates} from 'shared/util/constants';
import {
	getPlanLabel,
	isLDPPlan,
	SubscriptionNames
} from 'shared/util/subscriptions';
import {noop} from 'lodash';
import {Project} from 'shared/util/records';
import {PropTypes} from 'prop-types';
import {Routes, toRoute} from 'shared/util/router';

const isSubscriptionLimitReached = subscription => {
	if (
		subscription?.get('name') !== SubscriptionNames.LiferayDataPlatform &&
		subscription?.get('name') !==
			SubscriptionNames.LiferayDataPlatformPrivateBeta
	) {
		return false;
	}

	const hasReached = (limit, count) => limit > 0 && count >= limit;

	return (
		hasReached(
			subscription.get('individualsLimit'),
			subscription.get('individualsCountSinceLastAnniversary')
		) ||
		hasReached(
			subscription.get('pageViewsLimit'),
			subscription.get('pageViewsCountSinceLastAnniversary')
		)
	);
};

export default class WorkspaceList extends React.Component {
	static defaultProps = {
		checkDisabled: noop,
		displayAccountHeaders: false,
		displayPlanInfo: false,
		isJoinableProjects: false
	};

	static propTypes = {
		accounts: PropTypes.arrayOf(
			PropTypes.shape({
				accountName: PropTypes.string,
				projects: PropTypes.arrayOf(PropTypes.instanceOf(Project))
			})
		),
		checkDisabled: PropTypes.func,
		displayAccountHeaders: PropTypes.bool,
		displayPlanInfo: PropTypes.bool,
		isJoinableProjects: PropTypes.bool
	};

	getRoute({corpProjectUuid, friendlyURL, groupId, state}) {
		if (friendlyURL) {
			return toRoute(Routes.WORKSPACE_WITH_ID, {
				groupId: friendlyURL.replace('/', '')
			});
		} else if (!!groupId && state !== DataSourceStates.Unconfigured) {
			return toRoute(Routes.WORKSPACE_WITH_ID, {
				groupId
			});
		}

		return toRoute(Routes.WORKSPACE_ADD_WITH_CORP_PROJECT_UUID, {
			corpProjectUuid
		});
	}

	render() {
		const {
			accounts,
			checkDisabled,
			className,
			displayPlanInfo,
			isJoinableProjects
		} = this.props;

		return (
			<div className={getCN('workspace-list-root', className)}>
				<ul className={getCN('list-group', className)}>
					{accounts.map(project => {
						const {
							className,
							corpProjectName,
							faroSubscription,
							groupId,
							name,
							requested,
							state
						} = project;

						return (
							<WorkspaceListItem
								accountName={name}
								className={className}
								configured={
									!!groupId &&
									state !== DataSourceStates.Unconfigured
								}
								corpProjectName={corpProjectName}
								disabled={checkDisabled(project)}
								groupId={groupId}
								hasLimitReached={isSubscriptionLimitReached(
									faroSubscription
								)}
								href={this.getRoute(project)}
								isJoinableProjects={isJoinableProjects}
								key={name}
								ldpEnabled={isLDPPlan(
									faroSubscription?.get('name')
								)}
								name={name}
								planInfo={
									displayPlanInfo
										? `${getPlanLabel(
												faroSubscription.get('name')
										  )}`
										: ''
								}
								projectState={state}
								requested={requested}
							/>
						);
					})}
				</ul>
			</div>
		);
	}
}
