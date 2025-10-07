/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {Observer} from '@clayui/modal/lib/types';

// @ts-ignore

import {CommerceServiceProvider} from 'commerce-frontend-js';
import {debounce, fetch} from 'frontend-js-web';
import React, {useCallback, useMemo, useState} from 'react';

// @ts-ignore

import {showErrorNotification} from '../../../utilities/notifications';
import AccountCreationModalBody from './AccountCreationModalBody';
import {OnAccountChangeParams} from './CreateAccount';

const DeliveryCatalogAPIServiceProvider =
	CommerceServiceProvider.DeliveryCatalogAPI('v1');

interface AccountCreationModalProps {
	accountTypes: string[];
	closeModal: any;
	commerceChannelId: number;
	observer: Observer;
	onAccountChange: (data: OnAccountChangeParams) => void;
}

const AccountCreationModal = ({
	accountTypes,
	closeModal,
	commerceChannelId,
	observer,
	onAccountChange,
}: AccountCreationModalProps) => {
	const [accountData, setAccountData] = useState({
		description: '',
		externalReferenceCode: '',
		name: '',
		organizations: [],
		taxId: '',
		type: accountTypes?.length ? accountTypes?.[0] : '',
	});

	const apiUrl = new URL(
		`${Liferay.ThemeDisplay.getPathContext()}${DeliveryCatalogAPIServiceProvider.baseURL(
			commerceChannelId
		)}`,
		Liferay.ThemeDisplay.getPortalURL()
	).toString();

	const createAccount = useCallback(() => {
		const organizationIds = accountData.organizations.map(
			({value}) => value
		);

		fetch(apiUrl, {
			body: JSON.stringify({
				description: accountData.description,
				externalReferenceCode: accountData.externalReferenceCode,
				name: accountData.name,
				organizationIds,
				taxId: accountData.taxId,
				type: accountData.type,
			}),
			headers: {
				'Content-Type': 'application/json',
			},
			method: 'POST',
		})
			.then((response) => response.json())
			.then((response: any) => {
				onAccountChange({account: response, doCheckout: false});
				closeModal();
			})
			.catch(showErrorNotification);
	}, [accountData, apiUrl, closeModal, onAccountChange]);

	const debouncedCreateAccount = useMemo(
		() => debounce(async () => createAccount(), 500),
		[createAccount]
	);

	return (
		<ClayModal center className="commerce-modal" observer={observer}>
			<ClayForm
				onSubmit={(event) => {
					event.preventDefault();

					debouncedCreateAccount();
				}}
				style={{
					display: 'inherit',
					flexDirection: 'inherit',
					maxHeight: 'inherit',
				}}
			>
				<ClayModal.Header>
					{Liferay.Language.get('create-new-account')}
				</ClayModal.Header>

				<ClayModal.Body>
					<AccountCreationModalBody
						accountData={accountData}
						accountTypes={accountTypes}
						quickCreate={false}
						setAccountData={setAccountData}
					/>
				</ClayModal.Body>

				<ClayModal.Footer
					last={
						<ClayButton.Group spaced>
							<ClayButton
								displayType="secondary"
								onClick={closeModal}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>

							<ClayButton type="submit">
								{Liferay.Language.get('create')}
							</ClayButton>
						</ClayButton.Group>
					}
				/>
			</ClayForm>
		</ClayModal>
	);
};

export default AccountCreationModal;
