/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton, {ClayButtonWithIcon} from '@clayui/button';
import ClayDropDown from '@clayui/drop-down';
import {useModal} from '@clayui/modal';
import React, {useRef, useState} from 'react';

import ServiceProvider from '../../../ServiceProvider/index';
import Sticker from '../Sticker';
import {VIEWS} from '../util/constants';
import AccountCreationModal from './AccountCreationModal';
import EmptyListView from './EmptyListView';
import ListView from './ListView';

const DeliveryCatalogAPIServiceProvider =
	ServiceProvider.DeliveryCatalogAPI('v1');

export default function AccountsListView({
	accountEntryAllowedTypes,
	changeAccount,
	commerceChannelId,
	currentAccount,
	currentUser,
	disabled,
	orderSelectionDisabled,
	setCurrentView,
}) {
	const [modalVisible, setModalVisible] = useState(false);

	const {observer, onClose} = useModal({
		onClose: () => setModalVisible(false),
	});

	const accountsListRef = useRef();

	const apiUrl = new URL(
		`${themeDisplay.getPathContext()}${DeliveryCatalogAPIServiceProvider.baseURL(
			commerceChannelId
		)}`,
		themeDisplay.getPortalURL()
	);

	apiUrl.searchParams.append('sort', 'name');

	const filterString = accountEntryAllowedTypes
		.map((accountEntryAllowedType) => `'${accountEntryAllowedType}'`)
		.join(', ');

	if (filterString) {
		apiUrl.searchParams.append('filter', `type in (` + filterString + ')');
	}

	return (
		<ClayDropDown.ItemList className="accounts-list-container">
			<ClayDropDown.Section className="item-list-head">
				<span className="text-truncate-inline">
					<span className="text-truncate">
						{Liferay.Language.get('select-account')}
					</span>
				</span>

				{!!currentAccount.id && !orderSelectionDisabled && (
					<ClayButtonWithIcon
						displayType="unstyled"
						onClick={() => setCurrentView(VIEWS.ORDERS_LIST)}
						symbol="angle-right-small"
					/>
				)}
			</ClayDropDown.Section>

			<ClayDropDown.Divider />

			<ClayDropDown.Section>
				<ListView
					apiUrl={apiUrl.toString()}
					contentWrapperRef={accountsListRef}
					customView={({items, loading}) => {
						if (!items || !items.length) {
							return (
								<EmptyListView
									caption={Liferay.Language.get(
										'no-accounts-were-found'
									)}
									loading={loading}
								/>
							);
						}

						return (
							<ClayDropDown.ItemList className="accounts-list">
								{items.map((account) => (
									<ClayDropDown.Item
										key={account.id}
										onClick={() => changeAccount(account)}
									>
										<Sticker
											className="current-account-thumbnail mr-2"
											logoURL={
												themeDisplay.getPathContext() +
												account.logoURL
											}
											name={account.name}
											size={account.size}
										/>

										<span className="ml-2 text-truncate-inline">
											<span className="text-truncate">
												{account.name}
											</span>
										</span>
									</ClayDropDown.Item>
								))}
							</ClayDropDown.ItemList>
						);
					}}
					disabled={disabled}
					placeholder={Liferay.Language.get('search')}
				/>
			</ClayDropDown.Section>

			<ClayDropDown.Divider />

			<ClayDropDown.ItemList className="accounts-list">
				<ClayDropDown.Section>
					<div ref={accountsListRef} />
				</ClayDropDown.Section>
			</ClayDropDown.ItemList>

			{!!currentUser.actions?.create && (
				<ClayDropDown.Section>
					<ClayButton onClick={() => setModalVisible(true)}>
						{Liferay.Language.get('create-new-account')}
					</ClayButton>
				</ClayDropDown.Section>
			)}

			{modalVisible && (
				<AccountCreationModal
					accountTypes={accountEntryAllowedTypes}
					closeModal={onClose}
					commerceChannelId={commerceChannelId}
					handleAccountChange={changeAccount}
					observer={observer}
				/>
			)}
		</ClayDropDown.ItemList>
	);
}
