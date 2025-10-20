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
import {
	AccountSelectionModal,
	AccountUtils,
	CommerceNotificationUtils,
	CommerceServiceProvider,
	commerceEvents,
	commerceTypes,

	// @ts-ignore

} from 'commerce-frontend-js';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

const ACCOUNT_ENTRY_ID_DEFAULT = 0;
const DeliveryCatalogResource =
	CommerceServiceProvider.DeliveryCatalogAPI('v1');

function AccountSelectorButton({
	account,
	configuration,
	currentAccountPostURL,
	hasManageAccountsPermission,
	order,
	...props
}: TAccountSelectorButtonProps) {
	const accountEntryAllowedTypes = useMemo(
		() => Liferay.CommerceContext?.accountEntryAllowedTypes,
		[]
	);
	const commerceChannelId = useMemo(
		() => Liferay.CommerceContext?.commerceChannelId,
		[]
	);
	const currentAccount = useMemo(
		() =>
			({
				...account,
				id: account?.id ?? ACCOUNT_ENTRY_ID_DEFAULT,
			}) as commerceTypes.TAccount,
		[account]
	);

	const [availableAccounts, setAvailableAccounts] = useState([]);
	const [currentOrder, setCurrentOrder] = useState({
		...order,
		id: order?.id,
	});
	const [currentUser, setCurrentUser] = useState({} as any);

	const changeAccount = useCallback(
		async (account: commerceTypes.TAccount) => {
			try {
				await AccountUtils.selectAccount(
					account.id,
					currentAccountPostURL
				);

				Liferay.fire(commerceEvents.CURRENT_ACCOUNT_UPDATED, {
					id: account.id,
				});
			}
			catch (error) {
				CommerceNotificationUtils.showErrorNotification(error);
			}
		},
		[currentAccountPostURL]
	);

	const updateCurrentOrder = useCallback(
		({order}: any) => {
			if (!currentOrder || currentOrder.id !== order?.id) {
				setCurrentOrder((current: commerceTypes.TOrder) => ({
					...current,
					...order,
				}));
			}
		},
		[currentOrder, setCurrentOrder]
	);

	useEffect(() => {
		DeliveryCatalogResource.getAccountsByChannelId(commerceChannelId)
			.then((response: any) => {
				setCurrentUser(response);

				if (response.items.length) {
					setAvailableAccounts(response.items);
				}
			})
			.catch((error: {message: string}) =>
				CommerceNotificationUtils.showErrorNotification(error.message)
			);
	}, [commerceChannelId]);

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
		<>
			<ClayButton
				{...props}
				className={classnames(
					'align-items-center btn-account-selector d-flex',
					currentAccount?.id === ACCOUNT_ENTRY_ID_DEFAULT &&
						'account-selected'
				)}
				displayType="unstyled"
			>
				{currentAccount?.id ? (
					<>
						{configuration.showAccountImage ? (
							<ClaySticker
								className="current-account-thumbnail"
								shape="user-icon"
							>
								{currentAccount.logoURL ? (
									<ClaySticker.Image
										alt={currentAccount?.name}
										src={currentAccount?.logoURL}
									/>
								) : (
									currentAccount?.name
										.split(' ')
										.map((chunk: string) =>
											chunk.charAt(0).toUpperCase()
										)
										.join('')
								)}
							</ClaySticker>
						) : null}

						<div className="d-flex flex-column mx-2">
							{configuration.showAccountInfo ? (
								<div className="account-name">
									<span className="text-truncate-inline">
										<span className="text-truncate">
											{currentAccount?.name}
										</span>
									</span>
								</div>
							) : null}

							{configuration.showOrderInfo ? (
								<div className="d-flex order-info">
									{currentOrder?.id ? (
										<>
											<span className="order-id">
												{currentOrder?.id}
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
							<span className="text-truncate">
								{configuration.label}
							</span>
						</span>
					</div>
				)}

				{configuration.showButtonIcon ? (
					<ClayIcon
						className="account-selector-button-icon"
						symbol="angle-down"
					/>
				) : null}
			</ClayButton>

			{!!availableAccounts.length &&
			AccountUtils.shouldSelectAccount(
				currentAccount?.id,
				currentOrder.id
			) ? (
				<AccountSelectionModal
					accountEntryAllowedTypes={accountEntryAllowedTypes}
					availableAccounts={availableAccounts}
					changeAccount={changeAccount}
					commerceChannelId={commerceChannelId}
					hasCreatePermission={!!currentUser.actions?.create}
					hasManagePermission={hasManageAccountsPermission}
				/>
			) : null}
		</>
	);
}

export default AccountSelectorButton;
