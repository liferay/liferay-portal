/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useResource} from '@clayui/data-provider';
import ClayForm, {ClayInput, ClaySelect} from '@clayui/form';
import ClayIcon from '@clayui/icon';
import ClayMultiSelect from '@clayui/multi-select';
import {fetch} from 'frontend-js-web';
import React, {useMemo, useState} from 'react';

const ORGANIZATIONS_ROOT_ENDPOINT = '/o/headless-admin-user/v1.0/organizations';

const orgUrl = new URL(
	`${themeDisplay.getPathContext()}${ORGANIZATIONS_ROOT_ENDPOINT}`,
	themeDisplay.getPortalURL()
);

export default function AccountCreationModalBody({
	accountData,
	accountEntryAllowedTypes,
	quickCreate = false,
	setAccountData,
}) {
	const [organizationQuery, setOrganizationQuery] = useState('');
	const [organizationError, setOrganizationError] = useState(false);

	const [networkStatus, setNetworkStatus] = useState(4);
	const {resource = []} = useResource({
		fetch: async (link, options) => {
			const result = await fetch(link, options);
			const json = await result.json();

			return json.items.map((item) => ({
				label: item.name,
				value: item.id,
			}));
		},
		fetchPolicy: 'cache-first',
		link: orgUrl.toString(),
		onNetworkStatusChange: setNetworkStatus,
	});

	const availableOrganizations = useMemo(() => {
		if (!resource) {
			return [];
		}

		const accountValues = accountData.organizations.map(
			(item) => item.value
		);

		return resource.filter((org) => !accountValues.includes(org.value));
	}, [accountData.organizations, resource]);

	return (
		<>
			<ClayForm.Group>
				<label htmlFor="accountName">
					{Liferay.Language.get('account-name')}

					<span className="ml-1 reference-mark">
						<ClayIcon symbol="asterisk" />

						<span className="hide-accessible sr-only">
							{Liferay.Language.get('required')}
						</span>
					</span>
				</label>

				<ClayInput
					maxLength={100}
					name="accountName"
					onChange={(event) =>
						setAccountData({
							...accountData,
							name: event.target.value,
						})
					}
					required
					type="text"
					value={accountData.name}
				/>
			</ClayForm.Group>

			<ClayForm.Group className={organizationError && 'has-error'}>
				<label htmlFor="accountOrganizations">
					{Liferay.Language.get('organizations')}
				</label>

				<ClayMultiSelect
					closeButtonAriaLabel={Liferay.Language.get('remove')}
					items={accountData.organizations}
					loadingState={networkStatus}
					name="accountOrganizations"
					onChange={setOrganizationQuery}
					onItemsChange={(newItems) => {
						setOrganizationError(false);

						const validItems = [];

						newItems.map((item) => {
							if (
								resource.find(
									(organization) =>
										organization.label.toLowerCase() ===
										item.label.toLowerCase()
								)
							) {
								validItems.push(item);
							}
							else {
								setOrganizationError(true);
							}
						});

						setAccountData({
							...accountData,
							organizations: validItems,
						});
					}}
					sourceItems={availableOrganizations}
					value={organizationQuery}
				/>

				{organizationError && (
					<ClayForm.FeedbackGroup>
						<ClayForm.FeedbackItem>
							<ClayForm.FeedbackIndicator symbol="info-circle" />

							{Liferay.Language.get('select-from-list')}
						</ClayForm.FeedbackItem>
					</ClayForm.FeedbackGroup>
				)}
			</ClayForm.Group>

			<ClayForm.Group>
				<label htmlFor="accountType">
					{Liferay.Language.get('type')}

					<span className="ml-1 reference-mark">
						<ClayIcon symbol="asterisk" />

						<span className="hide-accessible sr-only">
							{Liferay.Language.get('required')}
						</span>
					</span>
				</label>

				<ClaySelect name="accountType">
					{accountEntryAllowedTypes.map((type) => (
						<ClaySelect.Option
							key={type}
							label={`${type[0].toUpperCase()}${type
								.substr(1)
								.toLowerCase()}`}
							onChange={(event) =>
								setAccountData({
									...accountData,
									type: event.target.value,
								})
							}
							value={accountData.type}
						/>
					))}
				</ClaySelect>
			</ClayForm.Group>

			{!quickCreate && (
				<>
					<ClayForm.Group>
						<label htmlFor="accountTaxId">
							<span>{Liferay.Language.get('tax-id')}</span>

							<span
								className="label-icon lfr-portal-tooltip ml-2"
								data-tooltip-align="top"
								title={Liferay.Language.get('tax-id-help')}
							>
								<ClayIcon symbol="question-circle-full" />
							</span>
						</label>

						<ClayInput
							name="accountTaxId"
							onChange={(event) =>
								setAccountData({
									...accountData,
									taxId: event.target.value,
								})
							}
							type="text"
							value={accountData.taxId}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor="accountERC">
							{Liferay.Language.get('external-reference-code')}
						</label>

						<ClayInput
							name="accountERC"
							onChange={(event) =>
								setAccountData({
									...accountData,
									externalReferenceCode: event.target.value,
								})
							}
							type="text"
							value={accountData.externalReferenceCode}
						/>
					</ClayForm.Group>

					<ClayForm.Group>
						<label htmlFor="accountDescription">
							{Liferay.Language.get('description')}
						</label>

						<ClayInput
							component="textarea"
							name="accountDescription"
							onChange={(event) =>
								setAccountData({
									...accountData,
									description: event.target.value,
								})
							}
							type="text"
							value={accountData.description}
						/>
					</ClayForm.Group>
				</>
			)}
		</>
	);
}
