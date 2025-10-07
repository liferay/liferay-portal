/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import {Col, Row} from '@clayui/layout';
import ClaySticker from '@clayui/sticker';
import {StatusRenderer} from '@liferay/frontend-data-set-web';
import classnames from 'classnames';
import {commerceEvents} from 'commerce-frontend-js';
import React, {useCallback, useEffect, useState} from 'react';

function AccountSelectorButton({
	account,
	label,
	order,
	showAccountImage,
	showAccountInfo,
	showButtonIcon,
	showOrderInfo,
	...props
}) {
	const [currentOrder, setCurrentOrder] = useState(order);

	const updateCurrentOrder = useCallback(
		({order}) => {
			if (!currentOrder || currentOrder.id !== order.id) {
				setCurrentOrder((current) => ({...current, ...order}));
			}
		},
		[currentOrder, setCurrentOrder]
	);

	useEffect(() => {
		Liferay.on(commerceEvents.CURRENT_ORDER_DELETED, updateCurrentOrder);
		Liferay.on(commerceEvents.CURRENT_ORDER_UPDATED, updateCurrentOrder);

		return () => {
			Liferay.detach(
				commerceEvents.CURRENT_ORDER_DELETED,
				updateCurrentOrder
			);
			Liferay.detach(
				commerceEvents.CURRENT_ORDER_UPDATED,
				updateCurrentOrder
			);
		};
	}, [updateCurrentOrder]);

	return (
		<ClayButton
			{...props}
			className={classnames(
				'align-items-center btn-account-selector d-flex',
				account?.id && 'account-selected'
			)}
			displayType="unstyled"
		>
			{account?.id ? (
				<>
					{showAccountImage ? (
						<ClaySticker
							className="current-account-thumbnail"
							shape="user-icon"
						>
							{account.logoURL ? (
								<ClaySticker.Image
									alt={account.name}
									src={account.logoURL}
								/>
							) : (
								account.name
									.split(' ')
									.map((chunk) =>
										chunk.charAt(0).toUpperCase()
									)
									.join('')
							)}
						</ClaySticker>
					) : null}

					<div className="d-flex flex-column mx-2">
						{showAccountInfo ? (
							<div className="account-name">
								<span className="text-truncate-inline">
									<span className="text-truncate">
										{account.name}
									</span>
								</span>
							</div>
						) : null}

						{showOrderInfo ? (
							<div className="d-flex order-info">
								{currentOrder?.id ? (
									<>
										<span className="order-id">
											{order.id}
										</span>
										<span className="col order-label">
											<StatusRenderer
												value={
													currentOrder?.workflowStatusInfo
												}
											/>
										</span>
									</>
								) : (
									<Row>
										<Col>
											{Liferay.Language.get(
												'there-is-no-order-selected'
											)}
										</Col>
									</Row>
								)}
							</div>
						) : null}
					</div>
				</>
			) : (
				<div className="mr-2 no-account-selected-placeholder">
					<span className="text-truncate-inline">
						<span className="text-truncate">{label}</span>
					</span>
				</div>
			)}

			{showButtonIcon ? (
				<ClayIcon
					className="account-selector-button-icon"
					symbol="angle-down"
				/>
			) : null}
		</ClayButton>
	);
}

export default AccountSelectorButton;
