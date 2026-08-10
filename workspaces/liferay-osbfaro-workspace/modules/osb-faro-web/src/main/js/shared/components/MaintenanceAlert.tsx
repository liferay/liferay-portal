import Alert, {AlertTypes} from 'shared/components/Alert';
import getCN from 'classnames';
import moment from 'moment';
import React from 'react';
import {compose} from 'redux';
import {connect} from 'react-redux';
import {getCustomDateFormat} from 'shared/util/date';
import {Project} from 'shared/util/records';
import {ProjectStates} from 'shared/util/constants';
import {setMaintenanceSeen} from 'shared/actions/maintenance-seen';
import {sub} from 'shared/util/lang';
import {withRouter} from 'react-router-dom';

interface IMaintenanceAlertProps {
	alertDismissed: boolean;
	className: string;
	currentUserId: string;
	groupId: string;
	project: Project;
	setMaintenanceSeen: (params: any) => void;
	stripe: boolean;
}

export class MaintenanceAlert extends React.Component<IMaintenanceAlertProps> {
	static defaultProps = {
		stripe: false,
	};

	state = {
		showMessage: false,
	};

	constructor(props: IMaintenanceAlertProps) {
		super(props);
		this.handleDismissClick = this.handleDismissClick.bind(this);
	}

	handleDismissClick() {
		const {
			currentUserId,
			groupId,
			project: {stateStartDate},
			setMaintenanceSeen,
		} = this.props;

		setMaintenanceSeen({
			currentUserId,
			groupId,
			stateStartDate,
		});
	}

	render() {
		const {
			alertDismissed,
			className,
			project: {state, stateStartDate},
			stripe,
		} = this.props;

		const showAlert = state === ProjectStates.Scheduled && !alertDismissed;

		return (
			<div className={getCN('maintenance-alert-root', className)}>
				{showAlert && (
					<Alert
						iconSymbol="warning-full"
						onClose={this.handleDismissClick}
						stripe={stripe}
						title={Liferay.Language.get('scheduled-maintenance')}
						type={AlertTypes.Warning}
					>
						{sub(
							Liferay.Language.get(
								'a-system-wide-maintenance-has-been-scheduled-to-take-place-on-x-at-x'
							),
							[
								moment(stateStartDate).format(
									getCustomDateFormat()
								),
								moment(stateStartDate).format('LT'),
							]
						)}
					</Alert>
				)}
			</div>
		);
	}
}

export const mapState = (
	store: any,
	{
		match: {
			params: {groupId},
		},
	}: any
) => {
	const currentUserId = store.getIn(['currentUser', 'data']);

	const project = store.getIn(['projects', groupId, 'data'], new Project());

	const prevStateStartDate = store.getIn([
		'maintenanceSeen',
		`${groupId}-${currentUserId}`,
	]);

	return {
		alertDismissed: prevStateStartDate === project.stateStartDate,
		currentUserId,
		groupId,
		project: store.getIn(['projects', groupId, 'data'], new Project()),
	};
};

export default compose<any>(
	withRouter,
	connect(mapState, {setMaintenanceSeen})
)(MaintenanceAlert);
