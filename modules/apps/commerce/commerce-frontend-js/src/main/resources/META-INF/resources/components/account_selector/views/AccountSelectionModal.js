/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import ClayForm, {ClayRadio, ClayRadioGroup, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayModal, {useModal} from '@clayui/modal';
import PropTypes from 'prop-types';
import React, {useCallback, useEffect, useState} from 'react';

import ServiceProvider from '../../../ServiceProvider/index';
import {showErrorNotification} from '../../../utilities/notifications';
import AccountCreationModalBody from './AccountCreationModalBody';

const DeliveryCatalogResource = ServiceProvider.DeliveryCatalogAPI('v1');

function AccountSelectionModal({
	accountEntryAllowedTypes,
	availableAccounts,
	changeAccount,
	commerceChannelId,
	hasCreatePermission,
	hasManagePermission,
}) {
	const [accountFields, setAccountFields] = useState({
		description: '',
		externalReferenceCode: '',
		name: '',
		organizations: [],
		taxId: '',
		type: accountEntryAllowedTypes[0],
	});
	const [canContinue, setCanContinue] = useState(false);
	const [isCreate, setIsCreate] = useState(false);
	const [selectedAccountId, setSelectedAccountId] = useState(null);

	const {observer, onOpenChange, open} = useModal({defaultOpen: true});

	const createAccount = useCallback(() => {
		const {organizations, ...accountJSON} = accountFields;

		const organizationIds = organizations.map(({value}) => value);

		DeliveryCatalogResource.createAccountByChannelId(commerceChannelId, {
			...accountJSON,
			organizationIds,
		})
			.then((newAccount) => changeAccount(newAccount))
			.catch(showErrorNotification);
	}, [accountFields, changeAccount, commerceChannelId]);

	useEffect(() => {
		setCanContinue(
			isCreate
				? !!accountFields?.name && !!accountFields?.type
				: !!selectedAccountId
		);
	}, [accountFields, commerceChannelId, isCreate, selectedAccountId]);

	return (
		<>
			{open ? (
				<ClayModal
					center
					disableAutoClose={true}
					id="account-selection-modal"
					observer={observer}
				>
					<ClayForm>
						<ClayModal.Header>
							{Liferay.Language.get('sign-in-to-checkout')}
						</ClayModal.Header>

						<ClayModal.Body>
							<div style={{minHeight: '14.5rem'}}>
								{hasManagePermission && (
									<ClayRadioGroup
										defaultValue={false}
										onChange={setIsCreate}
									>
										<ClayRadio
											defaultChecked={true}
											label={Liferay.Language.get(
												'select-an-existing-account'
											)}
											value={false}
										/>

										<ClayRadio
											className={
												hasCreatePermission
													? null
													: 'hide'
											}
											label={Liferay.Language.get(
												'create-a-new-account'
											)}
											value={true}
										/>
									</ClayRadioGroup>
								)}

								{isCreate ? (
									<AccountCreationModalBody
										accountData={accountFields}
										accountEntryAllowedTypes={
											accountEntryAllowedTypes
										}
										quickCreate={true}
										setAccountData={setAccountFields}
									/>
								) : (
									<ClayForm.Group>
										<label htmlFor="available-accounts-list">
											{Liferay.Language.get(
												'available-accounts'
											)}

											<span className="ml-1 reference-mark">
												<ClayIcon symbol="asterisk" />

												<span className="hide-accessible sr-only">
													{Liferay.Language.get(
														'required'
													)}
												</span>
											</span>
										</label>

										<ClaySelect
											id="available-accounts-list"
											onChange={(event) => {
												setSelectedAccountId(
													event.target.value
												);
											}}
										>
											<ClaySelect.Option value={null} />

											{availableAccounts.map(
												({id, name}) => (
													<ClaySelect.Option
														key={id}
														label={name}
														value={id}
													/>
												)
											)}
										</ClaySelect>
									</ClayForm.Group>
								)}
							</div>
						</ClayModal.Body>

						<ClayModal.Footer
							last={
								<ClayButton.Group spaced>
									<ClayButton
										displayType="secondary"
										onClick={() => onOpenChange(false)}
									>
										{Liferay.Language.get('cancel')}
									</ClayButton>

									<ClayButton
										disabled={!canContinue}
										displayType="primary"
										onClick={() =>
											isCreate
												? createAccount()
												: changeAccount({
														id: selectedAccountId,
													})
										}
										type="button"
									>
										{Liferay.Language.get('continue')}
									</ClayButton>
								</ClayButton.Group>
							}
						/>
					</ClayForm>
				</ClayModal>
			) : null}
		</>
	);
}

AccountSelectionModal.propTypes = {
	accountEntryAllowedTypes: PropTypes.array.isRequired,
	availableAccounts: PropTypes.array,
	changeAccount: PropTypes.func,
	commerceChannelId: PropTypes.oneOfType([PropTypes.number, PropTypes.string])
		.isRequired,
	hasCreatePermission: PropTypes.bool,
	hasManagePermission: PropTypes.bool,
};

export default AccountSelectionModal;
