/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ReactNode, createContext, useContext} from 'react';
import useSWR, {KeyedMutator} from 'swr';

import {MarketplaceUserAccount} from '../entity/MarketplaceUserAccount';
import {Liferay} from '../liferay/liferay';
import HeadlessAdminUser from '../services/rest/HeadlessAdminUser';

type Context = {
	channel: Channel;
	marketplaceUserAccount: MarketplaceUserAccount;
	mutateMyUserAccount: KeyedMutator<UserAccount | undefined>;
	myUserAccount: UserAccount;
	properties: DefaultProperties;
};

type MarketplaceContextProviderProps = {
	children: ReactNode;
	properties: DefaultProperties;
};

const MarketplaceContext = createContext<Context>({} as Context);

const MarketplaceContextProvider: React.FC<MarketplaceContextProviderProps> = ({
	children,
	properties,
}) => {
	const {data: myUserAccount, mutate} = useSWR(
		Liferay.ThemeDisplay.isSignedIn()
			? '/marketplace/my-user-account'
			: null,
		HeadlessAdminUser.getMyUserAccount
	);

	return (
		<MarketplaceContext.Provider
			value={
				{
					channel: {
						channelId: Number(
							Liferay.CommerceContext.commerceChannelId
						),
						currencyCode:
							Liferay.CommerceContext.currency.currencyCode,
						externalReferenceCode: 'MARKETPLACE',
						id: Number(Liferay.CommerceContext.commerceChannelId),
					} as Channel,
					marketplaceUserAccount: new MarketplaceUserAccount(
						myUserAccount as UserAccount
					),
					mutateMyUserAccount: mutate as KeyedMutator<UserAccount>,
					myUserAccount,
					properties,
				} as Context
			}
		>
			{children}
		</MarketplaceContext.Provider>
	);
};

const useMarketplaceContext = () => {
	return useContext(MarketplaceContext);
};

export {useMarketplaceContext, MarketplaceContext};

export default MarketplaceContextProvider;
