/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';

import {EXTEND_TRIAL_STATUS_LABEL} from '../../constants';
import {ExtendRequestStatus} from '../../enums/SSATrials';

import './ExtensionStatus.scss';

type ExtensionStatusProps = {
	className?: string;
	extensionStatus?: keyof typeof EXTEND_TRIAL_STATUS_LABEL;
};

const ExtensionStatus = ({
	className,
	extensionStatus,
}: ExtensionStatusProps) => (
	<div className={className}>
		<span
			className={classNames('extension-status', {
				'extension-status-approved': [
					ExtendRequestStatus.APPROVED,
					ExtendRequestStatus.AUTO_APPROVED,
				].includes(extensionStatus as ExtendRequestStatus),
				'extension-status-expired': [
					ExtendRequestStatus.EXTENSION_EXPIRED,
					ExtendRequestStatus.REJECTED,
				].includes(extensionStatus as ExtendRequestStatus),
				'extension-status-not-requested':
					extensionStatus === ExtendRequestStatus.NOT_REQUESTED ||
					!extensionStatus,
				'extension-status-pending':
					extensionStatus === ExtendRequestStatus.PENDING,
			})}
		>
			{EXTEND_TRIAL_STATUS_LABEL[extensionStatus ?? 'not-requested']}
		</span>
	</div>
);
export default ExtensionStatus;
