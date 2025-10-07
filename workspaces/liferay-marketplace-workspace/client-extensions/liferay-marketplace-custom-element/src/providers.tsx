/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayIconSpriteContext} from '@clayui/icon';
import {ClayModalProvider} from '@clayui/modal';
import React, {ReactNode} from 'react';
import {SWRConfig} from 'swr';

import MarketplaceContextProvider from './context/MarketplaceContext';
import {getIconSpriteMap} from './liferay/constants';
import SWRCacheProvider from './services/SWRCacheProvider';
import fetcher from './services/fetcher';
import {MarketplaceProperties} from './utils/attributes';

type ProviderProps = {
	children: ReactNode;
	properties: MarketplaceProperties;
};

const Providers: React.FC<ProviderProps> = ({children, properties}) => (
	<ClayIconSpriteContext.Provider value={getIconSpriteMap()}>
		<SWRConfig
			value={{
				fetcher,
				provider: SWRCacheProvider,
				revalidateIfStale: true,
				revalidateOnFocus: false,
			}}
		>
			<MarketplaceContextProvider properties={properties}>
				<ClayModalProvider>{children}</ClayModalProvider>
			</MarketplaceContextProvider>
		</SWRConfig>
	</ClayIconSpriteContext.Provider>
);

export default Providers;
