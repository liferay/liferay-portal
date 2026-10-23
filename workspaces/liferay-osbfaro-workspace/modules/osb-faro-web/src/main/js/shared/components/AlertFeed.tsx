import Alert, {ALERT_CONFIG_MAP, AlertTypes} from './Alert';
import getCN from 'classnames';
import React from 'react';
import {Alert as AlertType} from 'shared/types';
import {connect, ConnectedProps} from 'react-redux';
import {CSSTransition, TransitionGroup} from 'react-transition-group';
import {List, Map} from 'immutable';
import {removeAlert} from '../actions/alerts';
import {RootState} from 'shared/store';

const {danger, info, secondary, success, warning} = ALERT_CONFIG_MAP;

type AlertDisplaysType = {
	[alertType: string]: {
		iconSymbol?: string;
		title?: string;
		type?: AlertTypes;
	};
};

const ALERT_DISPLAYS: AlertDisplaysType = {
	[AlertType.Types.Default]: info,
	[AlertType.Types.Error]: danger,
	[AlertType.Types.Pending]: secondary,
	[AlertType.Types.Success]: success,
	[AlertType.Types.Warning]: warning,
};

const connector = connect(
	(state: RootState) => ({
		alertsIMap: state.get('alerts'),
	}),
	{
		removeAlert,
	}
);

type PropsFromRedux = ConnectedProps<typeof connector>;

interface IAlertFeedProps
	extends React.HTMLAttributes<HTMLElement>,
		PropsFromRedux {}

export const AlertFeed: React.FC<IAlertFeedProps> = ({
	alertsIMap,
	className,
	removeAlert,
}) => (
	<div className={getCN(className, 'alert-feed-root alert-notifications')}>
		<TransitionGroup>
			{alertsIMap
				.map((alertIMap: Map<string, any>) => {
					const {
						iconSymbol: symbol,
						title: label,
						type: display,
					} = ALERT_DISPLAYS[alertIMap.get('alertType')];

					const id = alertIMap.get('id');
					const message = alertIMap.get('message');

					return (
						<CSSTransition
							appear
							classNames="transition-slide-up"
							key={id}
							timeout={{enter: 150, exit: 150}}
						>
							<Alert
								iconSymbol={symbol}
								id={id}
								onClose={removeAlert}
								title={label}
								type={display}
							>
								{List.isList(message)
									? message.toJS()
									: message}
							</Alert>
						</CSSTransition>
					);
				})
				.toArray()}
		</TransitionGroup>
	</div>
);

export default connector(AlertFeed);
