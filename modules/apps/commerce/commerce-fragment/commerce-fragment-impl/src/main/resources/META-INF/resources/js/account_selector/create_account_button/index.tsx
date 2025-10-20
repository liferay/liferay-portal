/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useModal} from '@clayui/modal';
import {
	AccountUtils,
	AccountCreationModal,
	CommerceNotificationUtils,
	commerceEvents,
	commerceTypes,

	// @ts-ignore

} from 'commerce-frontend-js';
import React, {useMemo} from 'react';

const CreateAccountButton = ({
	configuration,
	currentAccountPostURL,
	hasAddAccountsPermission,
}: TCreateAccountButtonProps) => {
	const accountEntryAllowedTypes = useMemo(
		() => Liferay.CommerceContext?.accountEntryAllowedTypes,
		[]
	);
	const commerceChannelId = useMemo(
		() => Liferay.CommerceContext?.commerceChannelId,
		[]
	);

	const {observer, onOpenChange, open} = useModal();

	const handleAccountChange = (account: commerceTypes.TAccount) => {
		AccountUtils.selectAccount(account.id, currentAccountPostURL)
			.then(() => {
				Liferay.fire(commerceEvents.CURRENT_ACCOUNT_UPDATED, {
					id: account.id,
				});
			})
			.catch(CommerceNotificationUtils.showErrorNotification);
	};

	return (
		<>
			<ClayButton
				className="btn-create-account"
				disabled={!hasAddAccountsPermission}
				onClick={() => onOpenChange(true)}
			>
				{configuration.label}
			</ClayButton>

			{open && (
				<AccountCreationModal
					accountEntryAllowedTypes={accountEntryAllowedTypes}
					closeModal={() => onOpenChange(false)}
					commerceChannelId={commerceChannelId}
					handleAccountChange={handleAccountChange}
					observer={observer}
				/>
			)}
		</>
	);
};

export default CreateAccountButton;
