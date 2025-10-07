/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';

// @ts-ignore

import {commerceEvents} from 'commerce-frontend-js';
import React from 'react';

// @ts-ignore

import {showErrorNotification} from '../../../utilities/notifications.js';

// @ts-ignore

import {selectAccount} from '../../account_selector/util/index.js';
import AccountCreationModal from './AccountCreationModal';

interface CreateAccountProps {
	accountEntryAllowedTypes: string[];
	commerceChannelId: number;
	currentAccountURL: string;
	hasAddAccountsPermission: boolean;
	label: string;
}

export interface OnAccountChangeParams {
	account: any;
	doCheckout: boolean;
}

const CreateAccount = ({
	accountEntryAllowedTypes,
	commerceChannelId,
	currentAccountURL,
	hasAddAccountsPermission,
	label,
}: CreateAccountProps) => {
	const {observer, onOpenChange, open} = useModal();

	const onAccountChange = ({account}: OnAccountChangeParams) => {
		selectAccount(account.id, currentAccountURL)
			.then(() => {
				Liferay.fire(commerceEvents.CURRENT_ACCOUNT_UPDATED, {
					id: account.id,
				});
			})
			.catch(showErrorNotification);
	};

	return (
		<>
			<ClayButton
				className="btn-create-account"
				disabled={!hasAddAccountsPermission}
				onClick={() => onOpenChange(true)}
			>
				{label}
			</ClayButton>

			{open && (
				<AccountCreationModal
					accountTypes={accountEntryAllowedTypes}
					closeModal={() => onOpenChange(false)}
					commerceChannelId={commerceChannelId}
					observer={observer}
					onAccountChange={onAccountChange}
				/>
			)}
		</>
	);
};

export default CreateAccount;
