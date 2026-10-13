/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Icon from '@clayui/icon';
import ClayLayout from '@clayui/layout';
import classNames from 'classnames';
import React from 'react';

import {ClayAlertFooter} from './Footer';
import {ClayToastContainer} from './ToastContainer';
import {useAutoClose} from './useAutoClose';

export type DisplayType =
	| 'danger'
	| 'info'
	| 'secondary'
	| 'success'
	| 'warning';

export interface IClayAlertProps
	extends Omit<React.HTMLAttributes<HTMLDivElement>, 'role'> {

	/**
	 * A React Component to render the alert actions.
	 */
	actions?: React.ReactNode;

	/**
	 * Flag to indicate alert should automatically call `onClose`. It also
	 * accepts a duration (in ms) which indicates how long to wait. If `true`
	 * is passed in, the timeout will be 10000ms.
	 */
	autoClose?: boolean | number;

	/**
	 * Aria label for the dismissable alert close button.
	 */
	closeButtonAriaLabel?: string;

	/**
	 * Additional class name for the conditional container.
	 */
	containerClassName?: string;

	/**
	 * Determines the style of the alert.
	 */
	displayType?: 'danger' | 'info' | 'secondary' | 'success' | 'warning';

	/**
	 * Flag to indicate if close icon should be show. This prop is used in
	 * conjunction with the `onClose`prop;
	 */
	hideCloseIcon?: boolean;

	/**
	 * Callback function for when the 'x' is clicked.
	 */
	onClose?: () => void;

	/**
	 * The alert role is for important, and usually time-sensitive, information.
	 */
	role?: string | null;

	/**
	 * Path to the spritemap that Icon should use when referencing symbols.
	 */
	spritemap?: string;

	/**
	 * The icon's symbol name in the spritemap.
	 */
	symbol?: string;

	/**
	 * The summary of the Alert, often is something like 'Error' or 'Info'.
	 */
	title?: string;

	/**
	 * Determines the variant of the alert.
	 */
	variant?: 'feedback' | 'stripe' | 'inline';
}

const ICON_MAP = {
	danger: 'exclamation-full',
	info: 'info-circle',
	secondary: 'password-policies',
	success: 'check-circle-full',
	warning: 'warning-full',
};

const VARIANTS = ['inline', 'feedback'];

function ClayAlert({
	actions,
	autoClose,
	children,
	className,
	closeButtonAriaLabel = 'Close',
	containerClassName,
	displayType = 'info',
	hideCloseIcon = false,
	onClose,
	role = 'alert',
	spritemap,
	symbol,
	title,
	variant,
	...otherProps
}: IClayAlertProps) {
	const {pauseAutoCloseTimer, startAutoCloseTimer} = useAutoClose({
		autoClose,
		onClose,
	});
	const ConditionalContainer = ({children}: any) =>
		variant === 'stripe' ? (
			<div className={classNames('container', containerClassName)}>
				{children}
			</div>
		) : (
			<>{children}</>
		);
	const showDismissible = onClose && !hideCloseIcon;
	const AlertIndicator = () => (
		<span className="alert-indicator">
			<Icon
				spritemap={spritemap}
				symbol={symbol || ICON_MAP[displayType]}
			/>
		</span>
	);

	return (
		<div
			{...otherProps}
			className={classNames(className, 'alert', {
				'alert-dismissible': showDismissible,
				'alert-feedback alert-indicator-start': variant === 'feedback',
				'alert-fluid': variant === 'stripe',
				'alert-inline': variant === 'inline',
				[`alert-${displayType}`]: displayType,
			})}
			onMouseOut={startAutoCloseTimer}
			onMouseOver={pauseAutoCloseTimer}
		>
			<ConditionalContainer>
				<ClayLayout.ContentRow
					className="alert-autofit-row"
					role={role as string}
				>
					{!VARIANTS.includes(variant as string) && (
						<ClayLayout.ContentCol>
							<ClayLayout.ContentSection>
								<AlertIndicator />
							</ClayLayout.ContentSection>
						</ClayLayout.ContentCol>
					)}

					<ClayLayout.ContentCol expand>
						<ClayLayout.ContentSection>
							{VARIANTS.includes(variant as string) && (
								<AlertIndicator />
							)}

							{title && <strong className="lead">{title}</strong>}

							{children}

							{variant !== 'inline' && actions && (
								<ClayAlertFooter>{actions}</ClayAlertFooter>
							)}
						</ClayLayout.ContentSection>
					</ClayLayout.ContentCol>

					{variant === 'inline' && actions && (
						<ClayLayout.ContentCol>
							<ClayLayout.ContentSection>
								{actions}
							</ClayLayout.ContentSection>
						</ClayLayout.ContentCol>
					)}
				</ClayLayout.ContentRow>

				{showDismissible && (
					<button
						aria-label={closeButtonAriaLabel}
						className="close"
						onClick={onClose}
						type="button"
					>
						<Icon spritemap={spritemap} symbol="times" />
					</button>
				)}
			</ConditionalContainer>
		</div>
	);
}

ClayAlert.displayName = 'ClayAlert';
ClayAlert.Footer = ClayAlertFooter;
ClayAlert.ToastContainer = ClayToastContainer;

export default ClayAlert;
