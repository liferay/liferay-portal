/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import {ReactNode, useMemo} from 'react';
import useSWR from 'swr';

import {Liferay} from '../../liferay/liferay';
import HeadlessCommerceAdminUser from '../../services/rest/HeadlessCommerceAdminUser';
import RadioCardList from '../RadioCardList/RadioCardList';

type AccountSelectionProps = {
	checkPersonalAccount?: boolean;
	children?: ReactNode;
	enabledAccountRoles?: string[];
	onSelectAccount: (account: Account) => void;
	selectedAccount: Account | undefined;
	showAccountsAvailableText?: boolean;
	showContactSupport?: boolean;
	userAccount?: UserAccount;
};

const AccountSelection: React.FC<AccountSelectionProps> = ({
	checkPersonalAccount = false,
	children,
	enabledAccountRoles,
	onSelectAccount,
	selectedAccount,
	showAccountsAvailableText = true,
	showContactSupport = true,
	userAccount,
}) => {
	const accountBriefs = useMemo(
		() => userAccount?.accountBriefs ?? [],
		[userAccount?.accountBriefs]
	);

	const accountBriefIds = accountBriefs.map(({id}) => id);

	const {data: accountsInfo = [], isLoading} = useSWR(
		{accountBriefIds, key: 'commerce-account-info'},
		() =>
			Promise.all(
				accountBriefIds.map((accountBriefId) =>
					HeadlessCommerceAdminUser.getAccountInfo(accountBriefId)
				)
			)
	);

	const accounts = useMemo(
		() =>
			accountsInfo
				.map((accountInfo, index) => {
					const accountBrief = accountBriefs[index];
					let displayAccount = checkPersonalAccount
						? accountInfo.type === 'person'
						: true;

					if (accountBrief.roleBriefs.length) {
						displayAccount = accountBriefs[index].roleBriefs.some(
							(roleBrief) =>
								enabledAccountRoles
									? enabledAccountRoles.includes(
											roleBrief.name
										)
									: true
						);
					}

					return {
						displayAccount,
						id: accountBrief.id,
						imageURL: accountInfo.logoURL,
						selected:
							selectedAccount?.externalReferenceCode ===
							accountInfo.externalReferenceCode,
						title: accountInfo.name,
						type: accountInfo.type,
						value: accountInfo,
					};
				})
				.filter(({displayAccount}) => displayAccount),
		[
			accountBriefs,
			accountsInfo,
			checkPersonalAccount,
			enabledAccountRoles,
			selectedAccount?.externalReferenceCode,
		]
	);

	const handleSelectAccount = (radioOption: RadioOption<Account>) => {
		onSelectAccount(radioOption.value);
	};

	return (
		<div>
			{showAccountsAvailableText && (
				<p className="mb-4 secondary-text">
					{`Accounts available for `}

					<strong>
						{Liferay.ThemeDisplay.getUserEmailAddress()}
					</strong>

					{` (you)`}
				</p>
			)}

			{isLoading ? (
				<ClayLoadingIndicator />
			) : accounts.length ? (
				<RadioCardList
					contentList={accounts.map((account) => ({
						...account,
						selected: selectedAccount?.id === account?.id,
						title: (
							<div className="pt-2">
								<p className="h5 mb-1">{account.title}</p>
								<p className="h5 mb-0 text-capitalize text-muted">
									{account.type}
								</p>
							</div>
						),
					}))}
					leftRadio
					onSelect={handleSelectAccount}
					showImage
				/>
			) : (
				<p className="font-weight-bold my-5">No accounts available</p>
			)}

			{children}

			{showContactSupport && (
				<>
					<span className="mr-1 secondary-text">
						Not seeing a specific Account?
					</span>

					<ClayLink
						className="font-weight-bold"
						href="http://help.liferay.com/"
						rel="noopener noreferrer"
						target="_blank"
					>
						Contact Support
					</ClayLink>
				</>
			)}
		</div>
	);
};

export default AccountSelection;
