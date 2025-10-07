/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';

import {PaymentStatus as PaymentStatusCode} from '../../../../enums/Order';
import i18n from '../../../../i18n';

import './index.scss';

const paymentStatusLabel = {
	[PaymentStatusCode.CANCELED]: i18n.translate('canceled'),
	[PaymentStatusCode.FAILED]: i18n.translate('failed'),
	[PaymentStatusCode.PAID]: i18n.translate('paid'),
	[PaymentStatusCode.PAYMENT_PENDING]: i18n.translate('unpaid'),
	[PaymentStatusCode.PENDING]: i18n.translate('unpaid'),
};

const PaymentStatus = ({paymentStatus}: {paymentStatus: number}) => (
	<div>
		<ClayIcon
			className={classNames('mr-2 payment-status-icon', {
				'text-danger': [
					PaymentStatusCode.CANCELED,
					PaymentStatusCode.FAILED,
				].includes(paymentStatus),
				'text-success': paymentStatus === PaymentStatusCode.PAID,
				'text-warning': [
					PaymentStatusCode.PAYMENT_PENDING,
					PaymentStatusCode.PENDING,
				].includes(paymentStatus),
			})}
			symbol="circle"
		/>

		<span className="finance-dashboard-secondary-text">
			{paymentStatusLabel[paymentStatus as PaymentStatusCode]}
		</span>
	</div>
);

export default PaymentStatus;
