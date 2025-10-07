/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Button from '@clayui/button';

import BackLink from '../../../../components/BackLink';
import i18n from '../../../../i18n';
import PaymentStatus from '../PaymentStatus/PaymentStatus';

type DetailsHeaderProps = {
	backLink: string;
	onClick: () => void;
	paymentStatusCode: number;
	showButton: boolean;
	title: string;
};

const DetailsHeader = ({
	backLink,
	onClick,
	paymentStatusCode,
	showButton,
	title,
}: DetailsHeaderProps) => {
	return (
		<div className="align-items-center d-flex justify-content-between">
			<div>
				<BackLink path={backLink}>
					{i18n.translate('back-to-last-transaction')}
				</BackLink>

				<h2>{title}</h2>

				<PaymentStatus paymentStatus={paymentStatusCode} />
			</div>

			{showButton && (
				<Button displayType="secondary" onClick={onClick}>
					{i18n.translate('mark-as-paid')}
				</Button>
			)}
		</div>
	);
};

export default DetailsHeader;
