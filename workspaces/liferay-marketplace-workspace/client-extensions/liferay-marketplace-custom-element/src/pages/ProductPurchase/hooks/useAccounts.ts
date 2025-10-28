/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMemo, useState} from 'react';
import useSWR from 'swr';

import {useMarketplaceContext} from '../../../context/MarketplaceContext';
import {Liferay} from '../../../liferay/liferay';
import fetcher from '../../../services/fetcher';
import HeadlessAdminUser from '../../../services/rest/HeadlessAdminUser';

const useAccounts = () => {
	const {myUserAccount} = useMarketplaceContext();
	const [selectedAccount, setSelectedAccount] = useState<Account>({
		id: Liferay.CommerceContext.account?.accountId as number,
		name: Liferay.CommerceContext.account?.accountName as string,
	} as Account);

	const accountBriefs = useMemo(
		() => myUserAccount?.accountBriefs || [],
		[myUserAccount?.accountBriefs]
	);

	const {data: accounts = []} = useSWR(
		{accountBriefs, key: '/accounts-briefs/'},
		() => {
			return Promise.all(
				accountBriefs.map((accountBrief) =>
					fetcher(
						`o/headless-admin-user/v1.0/accounts/${accountBrief.id}?nestedFields=accountUserAccounts`
					)
				)
			);
		}
	);

	const {data: selectedAccountWithERC} = useSWR(
		selectedAccount.externalReferenceCode
			? null
			: {accounts, key: 'accounts-with-erc', selectedAccount},
		async ({selectedAccount}) => {
			const account = accounts.find(({id}) => selectedAccount.id === id);

			if (account) {
				return account;
			}

			return HeadlessAdminUser.getAccount(selectedAccount.id);
		}
	);

	return {
		accounts,
		myUserAccount,
		selectedAccount: selectedAccountWithERC || selectedAccount,
		setSelectedAccount,
	};
};

export default useAccounts;
