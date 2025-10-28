/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';
import React from 'react';

import {IOrderItem} from './Types';

interface IOrderItemModalDetailProps {
	observer: Observer;
	orderItem: IOrderItem;
	spritemap?: string;
}

const parseValue = (value: any) => {
	return Array.isArray(value)
		? value.filter((item) => item === 0 || item).join(', ')
		: value;
};

const OrderItemDetailModal = ({
	observer,
	orderItem: orderItem,
	spritemap = '',
}: IOrderItemModalDetailProps) => {
	return (
		<ClayModal
			id="orderItemDetailModal"
			observer={observer}
			spritemap={spritemap}
		>
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{`${Liferay.Language.get('view')} ${orderItem.sku} ${Liferay.Language.get('details')}`}
			</ClayModal.Header>

			<ClayModal.Body>
				<div className="text-weight-bold">{orderItem.name}</div>

				{(orderItem.skuUnitOfMeasure?.key || '') !== '' && (
					<div className="mt-3">
						{`${Liferay.Language.get('uom')}: ${orderItem.skuUnitOfMeasure?.key || ''}`}
					</div>
				)}

				{(orderItem.options || '[]') !== '[]' && (
					<div className="mt-3">
						{`${Liferay.Language.get('options')}:`}

						<div>
							{(
								JSON.parse(
									orderItem.options || '[]'
								) as Array<any>
							).map((option, index) => {
								const {
									skuId,
									skuOptionName,
									skuOptionValueNames,
									value,
								} = option;

								const childItem = (
									orderItem.cartItems || []
								).find(
									(childItem) =>
										childItem.skuId === parseInt(skuId, 10)
								);

								const {name, quantity, skuUnitOfMeasure} =
									childItem || {};

								return (
									<div className="pt-2" key={index}>
										<div className="h6">
											{skuOptionName}
										</div>

										<p>
											<span>
												{parseValue(
													skuOptionValueNames
												) || parseValue(value)}
											</span>

											{name && (
												<span className="pl-2">
													{`(${quantity} \u00D7 ${name} ${
														skuUnitOfMeasure?.key ||
														''
													})`}
												</span>
											)}
										</p>
									</div>
								);
							})}
						</div>
					</div>
				)}
			</ClayModal.Body>
		</ClayModal>
	);
};

export default OrderItemDetailModal;
