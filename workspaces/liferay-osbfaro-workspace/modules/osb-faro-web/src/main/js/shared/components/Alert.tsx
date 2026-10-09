import ClayIcon from 'shared/components/Icon';
import getCN from 'classnames';
import React from 'react';

export enum AlertTypes {
	Danger = 'danger',
	Info = 'info',
	Secondary = 'secondary',
	Success = 'success',
	Warning = 'warning',
}

export const ALERT_CONFIG_MAP = {
	[AlertTypes.Danger]: {
		iconSymbol: 'exclamation-full',
		title: Liferay.Language.get('error'),
		type: AlertTypes.Danger,
	},
	[AlertTypes.Info]: {
		iconSymbol: 'info-circle',
		title: Liferay.Language.get('info'),
		type: AlertTypes.Info,
	},
	[AlertTypes.Secondary]: {
		iconSymbol: 'info-circle',
		title: Liferay.Language.get('pending'),
		type: AlertTypes.Secondary,
	},
	[AlertTypes.Success]: {
		iconSymbol: 'check-circle-full',
		title: Liferay.Language.get('success'),
		type: AlertTypes.Success,
	},
	[AlertTypes.Warning]: {
		iconSymbol: 'warning-full',
		title: Liferay.Language.get('warning'),
		type: AlertTypes.Warning,
	},
};

interface IAlertProps extends React.HTMLAttributes<HTMLDivElement> {
	iconSymbol?: string;
	id?: string;
	onClose?: (id: string) => void;
	stripe?: boolean;
	title?: string;
	type?: AlertTypes;
}

const Alert: React.FC<IAlertProps> = ({
	children,
	className,
	iconSymbol = 'info-circle',
	id,
	onClose,
	stripe = false,
	title = Liferay.Language.get('info'),
	type = AlertTypes.Info,
}) => {
	let content = (
		<>
			{iconSymbol && (
				<span className="alert-indicator">
					<ClayIcon className="icon-root" symbol={iconSymbol} />
				</span>
			)}

			{title && <strong className="lead">{`${title}:`}</strong>}

			{children}

			{onClose && (
				<button className="close" onClick={() => onClose(id!)}>
					<ClayIcon className="icon-root" symbol="times" />
				</button>
			)}
		</>
	);

	if (stripe) {
		content = <div className="container">{content}</div>;
	}

	return (
		<div
			className={getCN(
				'alert',
				{
					'alert-dismissible': onClose,
					'alert-fluid': stripe,
					[`alert-${type}`]: type,
				},
				className
			)}
			role="alert"
		>
			{content}
		</div>
	);
};

export default Alert;
