/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm from '@clayui/form';
import ClayModal from '@clayui/modal';
import {debounce, fetch} from 'frontend-js-web';
import React, {useCallback, useMemo, useState} from 'react';

import ServiceProvider from '../../../ServiceProvider/index';
import AccountCreationModalBody from './AccountCreationModalBody';

const DeliveryCatalogAPIServiceProvider =
	ServiceProvider.DeliveryCatalogAPI('v1');

export default function AccountCreationModal({
	accountEntryAllowedTypes,
	closeModal,
	commerceChannelId,
	handleAccountChange,
	observer,
}) {
	const [accountData, setAccountData] = useState({
		description: '',
		externalReferenceCode: '',
		name: '',
		organizations: [],
		taxId: '',
		type: accountEntryAllowedTypes[0],
	});

	const apiUrl = new URL(
		`${themeDisplay.getPathContext()}${DeliveryCatalogAPIServiceProvider.baseURL(
			commerceChannelId
		)}`,
		themeDisplay.getPortalURL()
	);

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
			.then((response) => {
				handleAccountChange(response);

				closeModal();
			})
			.catch((error) => console.error(error));

		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [
		accountData.description,
		accountData.externalReferenceCode,
		accountData.name,
		accountData.organizations,
		accountData.taxId,
		accountData.type,
		closeModal,
		handleAccountChange,
	]);

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
				<ClayModal.Header
					closeButtonAriaLabel={Liferay.Language.get('close')}
				>
					{Liferay.Language.get('create-new-account')}
				</ClayModal.Header>

				<ClayModal.Body>
					<AccountCreationModalBody
						accountData={accountData}
						accountEntryAllowedTypes={accountEntryAllowedTypes}
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
}
