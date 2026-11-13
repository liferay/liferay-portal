import getCN from 'classnames';
import React from 'react';
import StepList from './StepList';
import WorkspacesBasePage from './BasePage';
import {Link} from 'react-router-dom';
import {ProjectStates} from 'shared/util/constants';
import {PropTypes} from 'prop-types';

export const NO_ACCOUNT = 'noAccount';
export const NO_SUBSCRIPTION = 'noSubscription';

const DETAILS_MAP = {
	[ProjectStates.Deactivated]: [
		Liferay.Language.get('this-workspace-is-currently-inactive')
	],
	[ProjectStates.Maintenance]: [
		Liferay.Language.get(
			'analytics-cloud-is-undergoing-scheduled-maintenance.-we-expect-to-be-back-online-in-a-couple-of-hours.-thank-you-for-your-patience'
		)
	],
	[NO_ACCOUNT]: [
		Liferay.Language.get(
			'you-must-have-an-analytics-cloud-subscription-or-a-dxp-subscription-to-access-analytics-cloud'
		)
	],
	[NO_SUBSCRIPTION]: [
		Liferay.Language.get(
			'you-must-be-a-current-dxp-subscriber-to-get-the-basic-tier-of-analytics-cloud'
		)
	],
	[ProjectStates.Unavailable]: [
		Liferay.Language.get(
			'analytics-cloud-is-temporarily-unavailable.-we-apologize-for-any-inconvenience,-we-plan-to-be-back-up-shortly'
		)
	]
};

const STEP_LIST_MAP = {
	[ProjectStates.Deactivated]: {
		secondaryInfo: Liferay.Language.get(
			'please-go-to-the-workspace-list-to-reactivate'
		)
	},
	[NO_ACCOUNT]: {
		hideBullets: true,
		secondaryInfo: Liferay.Language.get(
			'this-login-is-not-currently-associated-with-any-subscriptions'
		),
		steps: [
			Liferay.Language.get(
				'check-customer-portal-to-see-your-active-liferay-subscriptions'
			)
		]
	},
	[NO_SUBSCRIPTION]: {
		hideBullets: false,
		secondaryInfo: Liferay.Language.get(
			'this-login-is-not-currently-associated-with-a-dxp-subscription'
		),
		steps: [
			Liferay.Language.get(
				'log-in-with-an-account-that-is-associated-with-dxp'
			),
			Liferay.Language.get('contact-your-liferay-account-representative'),
			Liferay.Language.get(
				'check-customer-portal-to-see-your-active-liferay-subscriptions'
			),
			// TODO: this would ideally not be a static link
			<Link key='LEARN_MORE' to='https://www.liferay.com/products/dxp'>
				{Liferay.Language.get('learn-more-about-liferay-dxp')}
			</Link>
		]
	}
};

const TITLE_MAP = {
	[ProjectStates.Deactivated]: Liferay.Language.get('inactive-workspace'),
	[ProjectStates.Maintenance]: Liferay.Language.get('scheduled-maintenance'),
	[NO_ACCOUNT]: Liferay.Language.get('account-was-not-found'),
	[NO_SUBSCRIPTION]: Liferay.Language.get('dxp-subscription-was-not-found'),
	[ProjectStates.Unavailable]: Liferay.Language.get(
		'service-temporarily-unavailable'
	)
};

export default class WorkspacesErrorDisplay extends React.Component {
	static defaultProps = {
		errorType: NO_SUBSCRIPTION
	};

	static propTypes = {
		errorType: PropTypes.string
	};

	render() {
		const {className, errorType} = this.props;

		const stepListProps = STEP_LIST_MAP[errorType];

		return (
			<WorkspacesBasePage
				className={getCN('workspaces-error-display-root', {className})}
				details={DETAILS_MAP[errorType]}
				title={TITLE_MAP[errorType]}
			>
				{stepListProps && <StepList {...stepListProps} />}
			</WorkspacesBasePage>
		);
	}
}
