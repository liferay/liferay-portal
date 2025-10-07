/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLabel from '@clayui/label';
import classNames from 'classnames';

import purchasedAppIcon from '../../../assets/icons/purchased_app_icon.svg';
import OrderStatus from '../../../components/OrderStatus';
import {OrderTypes} from '../../../enums/Order';

enum OrderAppTypeEnum {
	DXPAPP = 'DXP APP',
	CLOUDAPP = 'CLOUD APP',
}

type OrderDetailsStatusDescriptionProps = {
	order?: Cart;
	productOwner?: string;
};

const getOrderDetailsType = (orderTypeExternalReferenceCode: string) => {
	if (orderTypeExternalReferenceCode === OrderTypes.DXPAPP) {
		return OrderAppTypeEnum.DXPAPP;
	}

	if (orderTypeExternalReferenceCode === OrderTypes.CLOUDAPP) {
		return OrderAppTypeEnum.CLOUDAPP;
	}
};

const OrderDetailsStatusDescription = ({
	order,
	productOwner,
}: OrderDetailsStatusDescriptionProps) => {
	const orderType = getOrderDetailsType(
		order?.orderTypeExternalReferenceCode as string
	);

	return (
		<div className="align-items-center d-flex">
			<div
				className={classNames(classNames, {
					'order-details-publisher mr-3': productOwner,
				})}
			>
				{productOwner}
			</div>

			{order && (
				<div className="align-items-center app-details-status d-flex mr-3">
					<OrderStatus orderStatus={order?.orderStatusInfo.label}>
						{order?.orderStatusInfo.label}
					</OrderStatus>
				</div>
			)}

			{orderType && (
				<ClayLabel className="rounded" displayType="info" large>
					<div className="align-items-center d-flex">
						<img
							alt="Purchased Order Icon"
							className="mr-1"
							src={purchasedAppIcon}
						/>

						{orderType}
					</div>
				</ClayLabel>
			)}
		</div>
	);
};

export default OrderDetailsStatusDescription;
