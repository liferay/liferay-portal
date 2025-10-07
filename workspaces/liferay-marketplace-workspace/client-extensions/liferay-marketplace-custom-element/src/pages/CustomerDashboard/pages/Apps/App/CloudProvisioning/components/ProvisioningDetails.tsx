/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import classNames from 'classnames';

import i18n from '../../../../../../../i18n';
import OrderDetailsHeader from '../../../../../components/OrderDetailsHeader';
import AccountEmailInfo from '../../Licenses/CreateLicense/AccountInfo';
import {ProvisioningRow} from '../hooks/useProvisioningData';
import {InstallStatus} from '../types';

import './index.scss';

type ProvisioningDetailsProps = {
	account: Account;
	headerInfo?: {
		image?: string;
		licenseType?: string;
		name?: string;
	};
	onClose: () => void;
	provisioningRow: ProvisioningRow;
};

type InfoBadgeProps = {
	children?: string;
	status?: string;
	title: string;
};

const badgeStatus = {
	[InstallStatus.EXPIRED]: 'provisioning-details-info-badge-expired',
	[InstallStatus.IN_PROGRESS]:
		'provisioning-details-info-badge-ready-to-install',
	[InstallStatus.INSTALLED]: 'provisioning-details-info-badge-installed',
	[InstallStatus.READY_TO_INSTALL]:
		'provisioning-details-info-badge-ready-to-install',
};

const InfoBadge: React.FC<InfoBadgeProps> = ({children, status, title}) => (
	<div className="d-flex flex-column mb-4">
		<p className="font-weight-bold m-0 text-black-50">{title}</p>
		<div className="d-inline-flex">
			<div
				className={classNames(
					'font-weight-bold px-3 py-2 rounded-lg text-capitalize',
					{
						'provisioning-details-info-badge': !status,
					},
					status && badgeStatus[status as keyof typeof badgeStatus]
				)}
			>
				{children}
			</div>
		</div>
	</div>
);

const ProvisioningDetails: React.FC<ProvisioningDetailsProps> = ({
	account,
	headerInfo,
	onClose,
	provisioningRow,
}) => {
	return (
		<div className="d-flex flex-column mb-9 provisioning-details">
			<div className="align-items-center d-flex justify-content-between">
				<span className="font-weight-bold text-primary">
					{i18n.translate('provisioning-details').toUpperCase()}
				</span>

				<span>
					<ClayButtonWithIcon
						aria-label="Close"
						borderless
						className="text-dark"
						onClick={onClose}
						symbol="times"
						title="Close"
					/>
				</span>
			</div>

			<div className="d-flex justify-content-between mb-5">
				<OrderDetailsHeader
					hasOrderDescription={headerInfo?.licenseType}
					image={headerInfo?.image}
					name={headerInfo?.name}
				/>

				<AccountEmailInfo image={account.logoURL} name={account.name} />
			</div>

			<div className="d-flex flex-row mb-7 mt-5">
				<div className="col-6 p-0">
					<p className="font-weight-bold">
						{i18n.translate('client-extension')}
					</p>

					<InfoBadge title={i18n.translate('start-date')}>
						{provisioningRow?.startDate}
					</InfoBadge>

					<InfoBadge title={i18n.translate('expiration-date')}>
						{provisioningRow?.expirationDate}
					</InfoBadge>
				</div>

				<div className="col-6 p-0">
					<p className="font-weight-bold">
						{i18n.translate('installation-status')}
					</p>

					<InfoBadge
						status={provisioningRow?.status}
						title={i18n.translate('status')}
					>
						{i18n.translate(provisioningRow?.status as any)}
					</InfoBadge>

					<InfoBadge title={i18n.translate('project')}>
						{provisioningRow?.project}
					</InfoBadge>

					<InfoBadge title={i18n.translate('environment')}>
						{provisioningRow?.environment}
					</InfoBadge>
				</div>
			</div>
		</div>
	);
};

export default ProvisioningDetails;
