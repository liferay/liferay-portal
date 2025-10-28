/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import {useResource} from '@clayui/data-provider';
import ClayForm, {ClayInput, ClayRadio, ClayRadioGroup} from '@clayui/form';
import ClayModal from '@clayui/modal';
import ClayMultiSelect from '@clayui/multi-select';
import classNames from 'classnames';
import {openToast} from 'frontend-js-components-web';
import {fetch, sub} from 'frontend-js-web';
import React, {useContext, useMemo, useState} from 'react';

import ChartContext from '../ChartContext';
import {createAccount, updateAccount} from '../data/accounts';
import {MODEL_TYPE_MAP} from '../utils/constants';

function showNotFoundError(name) {
	openToast({
		message: sub(Liferay.Language.get('x-not-found'), name),
		type: 'danger',
	});
}

const ACCOUNTS_ROOT_ENDPOINT = '/o/headless-admin-user/v1.0/accounts';

export default function AddOrganizationModal({
	closeModal,
	observer,
	parentData,
}) {
	const {chartInstanceRef} = useContext(ChartContext);

	const [accountsQuery, setAccountsQuery] = useState('');
	const [newAccountName, setNewAccountName] = useState('');
	const [selectedAccounts, setSelectedAccounts] = useState([]);
	const [errors, setErrors] = useState([]);
	const [newAccountMode, setNewAccountMode] = useState(false);

	const [networkStatus, setNetworkStatus] = useState(4);
	const {resource = {}} = useResource({
		fetch,
		fetchPolicy: 'cache-first',
		link: new URL(
			`${themeDisplay.getPathContext()}${ACCOUNTS_ROOT_ENDPOINT}`,
			themeDisplay.getPortalURL()
		).toString(),
		onNetworkStatusChange: setNetworkStatus,
		variables: {
			search: accountsQuery,
		},
	});

	const accountOptions = useMemo(() => {
		const selectedAccountIds = new Set(
			selectedAccounts.map((account) => account.id)
		);

		if (!resource) {
			return [];
		}

		return resource.items.filter((account) => {
			const alreadySelected = selectedAccountIds.has(account.id);
			const alreadyDefinedAsChild = account.organizationIds.some(
				(organizationId) =>
					Number(organizationId) === Number(parentData.id)
			);

			return !alreadySelected && !alreadyDefinedAsChild;
		});
	}, [parentData, selectedAccounts, resource]);

	function handleSave() {
		if (newAccountMode) {
			createAccount(newAccountName, [parentData.id])
				.then((accountData) => {
					openToast({
						message: sub(
							Liferay.Language.get('1-account-was-added-to-x'),
							parentData.name
						),
						type: 'success',
					});

					chartInstanceRef.current.addNodes(
						[accountData],
						MODEL_TYPE_MAP.account,
						parentData
					);

					chartInstanceRef.current.updateNodeContent({
						...parentData,
						numberOfAccounts: parentData.numberOfAccounts + 1,
					});

					closeModal();
				})
				.catch((error) => {
					setErrors([error.title]);
				});
		}
		else {
			if (accountsQuery) {
				showNotFoundError(accountsQuery);
			}

			if (selectedAccounts.length) {
				Promise.allSettled(
					selectedAccounts.map((account) =>
						updateAccount(account.id, {
							organizationIds: [
								...account.organizationIds,
								parentData.id,
							],
						})
					)
				).then((results) => {
					const nodeChildren = [];
					const newErrors = [];

					results.forEach((result) => {
						if (result.status === 'fulfilled') {
							nodeChildren.push(result.value);
						}
						else {
							openToast({
								message: result.value,
								type: 'danger',
							});
							newErrors.push(result.value);
						}
					});

					setErrors(newErrors);

					const message =
						nodeChildren.length === 1
							? sub(
									Liferay.Language.get(
										'1-account-was-added-to-x'
									),
									parentData.name
								)
							: sub(
									Liferay.Language.get(
										'x-accounts-were-added-to-x'
									),
									nodeChildren.length,
									parentData.name
								);

					openToast({
						message,
						type: 'success',
					});

					chartInstanceRef.current.addNodes(
						nodeChildren,
						MODEL_TYPE_MAP.account,
						parentData
					);

					chartInstanceRef.current.updateNodeContent({
						...parentData,
						numberOfAccounts:
							parentData.numberOfAccounts + nodeChildren.length,
					});

					if (!errors.length) {
						closeModal();
					}
				});
			}
		}
	}

	function handleItemsChange(items) {
		const filteredItems = items.filter((item) => {
			const newItem = item.name === item.id;

			if (newItem) {
				showNotFoundError(item.name);
			}

			return !newItem;
		});

		setSelectedAccounts(filteredItems);
	}

	const errorsContainer = !!errors.length && (
		<ClayForm.FeedbackGroup>
			{errors.map((error, i) => (
				<ClayForm.FeedbackItem key={i}>
					<ClayForm.FeedbackIndicator symbol="info-circle" />

					{error}
				</ClayForm.FeedbackItem>
			))}
		</ClayForm.FeedbackGroup>
	);

	return (
		<ClayModal center observer={observer} size="md">
			<ClayModal.Header
				closeButtonAriaLabel={Liferay.Language.get('close')}
			>
				{Liferay.Language.get('add-accounts')}
			</ClayModal.Header>

			<ClayModal.Body>
				<ClayRadioGroup
					className="mb-4"
					onChange={setNewAccountMode}
					value={newAccountMode}
				>
					<ClayRadio
						label={Liferay.Language.get('select-accounts')}
						value={false}
					/>

					<ClayRadio
						label={Liferay.Language.get('create-new-account')}
						value={true}
					/>
				</ClayRadioGroup>

				{newAccountMode ? (
					<ClayForm.Group
						className={classNames(!!errors.length && 'has-error')}
					>
						<label htmlFor="newAccountNameInput">
							{Liferay.Language.get('name')}
						</label>

						<ClayInput
							id="newAccountNameInput"
							onChange={(event) =>
								setNewAccountName(event.target.value)
							}
							value={newAccountName}
						/>

						{errorsContainer}
					</ClayForm.Group>
				) : (
					<ClayForm.Group
						className={classNames(!!errors.length && 'has-error')}
					>
						<label htmlFor="searchAccountInput">
							{Liferay.Language.get('search-account')}
						</label>

						<ClayMultiSelect
							id="searchAccountInput"
							items={selectedAccounts}
							loadingState={networkStatus}
							locator={{label: 'name', value: 'id'}}
							onChange={setAccountsQuery}
							onItemsChange={handleItemsChange}
							sourceItems={accountOptions}
							value={accountsQuery}
						/>

						{errorsContainer}
					</ClayForm.Group>
				)}
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

						<ClayButton displayType="primary" onClick={handleSave}>
							{Liferay.Language.get('save')}
						</ClayButton>
					</ClayButton.Group>
				}
			/>
		</ClayModal>
	);
}
