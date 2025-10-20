/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayDropDown from '@clayui/drop-down';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import './account_selector.scss';
import ServiceProvider from '../../ServiceProvider/index';
import {
	CURRENT_ACCOUNT_UPDATED,
	CURRENT_ORDER_DELETED,
	CURRENT_ORDER_UPDATED,
} from '../../utilities/eventsDefinitions';
import {showErrorNotification} from '../../utilities/notifications';
import Trigger from './Trigger';
import {ACCOUNT_ENTRY_ID_DEFAULT, VIEWS} from './util/constants';
import {selectAccount, shouldSelectAccount} from './util/index';
import AccountSelectionModal from './views/AccountSelectionModal';
import AccountsListView from './views/AccountsListView';
import OrdersListView from './views/OrdersListView';

const DeliveryCatalogResource = ServiceProvider.DeliveryCatalogAPI('v1');

function AccountSelector({
	alignmentPosition,
	createNewOrderURL,
	currencyCode,
	currentCommerceAccount: account,
	currentCommerceOrder: order,
	hasAddCommerceOrderPermission,
	hasManageAccountsPermission,
	orderSelectionDisabled = false,
	refreshPageOnAccountSelected: forceRefresh,
	selectOrderURL,
	setCurrentAccountURL: selectAccountURL,
}) {
	const accountEntryAllowedTypes = useMemo(
		() => Liferay.CommerceContext?.accountEntryAllowedTypes,
		[]
	);
	const commerceChannelId = useMemo(
		() => Liferay.CommerceContext?.commerceChannelId,
		[]
	);

	const [active, setActive] = useState(false);
	const [availableAccounts, setAvailableAccounts] = useState([]);
	const [currentAccount, setCurrentAccount] = useState({
		...account,
		id: account?.id ?? ACCOUNT_ENTRY_ID_DEFAULT,
	});
	const [currentOrder, setCurrentOrder] = useState({
		...order,
		id: order?.orderId || 0,
	});
	const [currentView, setCurrentView] = useState(
		account?.id && !orderSelectionDisabled
			? VIEWS.ORDERS_LIST
			: VIEWS.ACCOUNTS_LIST
	);
	const [currentUser, setCurrentUser] = useState({});

	useEffect(() => {
		DeliveryCatalogResource.getAccountsByChannelId(commerceChannelId)
			.then((response) => {
				setCurrentUser(response);

				if (response.items.length) {
					setAvailableAccounts(response.items);
				}
			})
			.catch((error) => showErrorNotification(error.message));
	}, [commerceChannelId]);

	const changeAccount = (account) => {
		selectAccount(account.id, selectAccountURL)
			.then(() => {
				if (forceRefresh) {
					window.location.reload();
				}
				else {
					Liferay.fire(CURRENT_ACCOUNT_UPDATED, {id: account.id});

					setCurrentAccount(account);
					setCurrentView(VIEWS.ORDERS_LIST);
					setCurrentOrder(null);
				}
			})
			.catch(showErrorNotification);
	};

	const updateOrderModel = useCallback(
		({order}) => {
			if (!currentOrder || currentOrder.id !== order.id) {
				setCurrentOrder((current) =>
					order.id === 0 ? {id: order.id} : {...current, ...order}
				);
			}
		},
		[currentOrder, setCurrentOrder]
	);

	useEffect(() => {
		Liferay.on(CURRENT_ORDER_UPDATED, updateOrderModel);
		Liferay.on(CURRENT_ORDER_DELETED, updateOrderModel);

		return () => {
			Liferay.detach(CURRENT_ORDER_DELETED, updateOrderModel);
			Liferay.detach(CURRENT_ORDER_UPDATED, updateOrderModel);
		};
	}, [updateOrderModel]);

	return (
		<>
			<ClayDropDown
				active={active}
				alignmentPosition={alignmentPosition}
				className="account-selector account-selector-dropdown"
				data-permission={hasAddCommerceOrderPermission}
				menuElementAttrs={{className: 'account-selector-dropdown-menu'}}
				onActiveChange={setActive}
				trigger={
					<Trigger
						active={active}
						currentAccount={currentAccount}
						currentOrder={currentOrder}
					/>
				}
			>
				{currentView === VIEWS.ACCOUNTS_LIST && (
					<AccountsListView
						accountEntryAllowedTypes={accountEntryAllowedTypes}
						changeAccount={changeAccount}
						commerceChannelId={commerceChannelId}
						currentAccount={currentAccount}
						currentUser={currentUser}
						disabled={!active}
						orderSelectionDisabled={orderSelectionDisabled}
						setCurrentView={setCurrentView}
					/>
				)}

				{currentView === VIEWS.ORDERS_LIST &&
					!orderSelectionDisabled && (
						<OrdersListView
							commerceChannelId={commerceChannelId}
							createOrderURL={createNewOrderURL}
							currencyCode={currencyCode}
							currentAccount={currentAccount}
							disabled={!active}
							hasAddCommerceOrderPermission={
								hasAddCommerceOrderPermission
							}
							selectOrderURL={selectOrderURL}
							setCurrentView={setCurrentView}
						/>
					)}
			</ClayDropDown>

			{!!availableAccounts.length &&
			shouldSelectAccount(currentAccount.id, currentOrder.id) ? (
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

AccountSelector.propTypes = {
	alignmentPosition: PropTypes.number,
	createNewOrderURL: PropTypes.string.isRequired,
	currencyCode: PropTypes.string.isRequired,
	currentCommerceAccount: PropTypes.shape({
		id: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		logoURL: PropTypes.string,
		name: PropTypes.string,
	}),
	currentCommerceOrder: PropTypes.shape({
		orderId: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
		workflowStatusInfo: PropTypes.shape({
			label_i18n: PropTypes.string,
		}),
	}),
	hasAddCommerceOrderPermission: PropTypes.bool,
	hasManageAccountsPermission: PropTypes.bool,
	orderSelectionDisabled: PropTypes.bool,
	refreshPageOnAccountSelected: PropTypes.bool,
	selectOrderURL: PropTypes.string.isRequired,
	setCurrentAccountURL: PropTypes.string.isRequired,
};

AccountSelector.defaultProps = {
	alignmentPosition: 3,
	currentCommerceAccount: {
		id: ACCOUNT_ENTRY_ID_DEFAULT,
	},
	currentCommerceOrder: {
		orderId: 0,
	},
	hasAddCommerceOrderPermission: false,
	hasManageAccountsPermission: false,
	orderSelectionDisabled: false,
	refreshPageOnAccountSelected: false,
};

export default AccountSelector;
