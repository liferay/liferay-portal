/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {useMarketplaceContext} from '../../context/MarketplaceContext';
import useAccounts from '../../hooks/data/useAccounts';
import AccountSearchDropdown from './AccountSearchDropdown';
import DashboardNavigationItem from './DashboardNavigationItem';

import './DashboardNavigation.scss';

export type DashboardListItems = {
	active?: boolean;
	itemTitle: string;
	path: string;
	symbol: string;
	visible?: boolean;
};

export type DashboardNavigationProps = {
	accountAppsNumber?: number;
	accountIcon?: string;
	accountsSearch?: ReturnType<typeof useAccounts>;
	currentAccount?: Account;
	dashboardNavigationItems: DashboardListItems[];
};

export function DashboardNavigation({
	accountAppsNumber,
	accountIcon,
	accountsSearch,
	currentAccount,
	dashboardNavigationItems,
}: DashboardNavigationProps) {
	const {properties} = useMarketplaceContext();

	return (
		<div className="dashboard-navigation-container">
			{!properties.featureFlags.includes('LPD-51092') &&
				accountsSearch && (
					<AccountSearchDropdown
						accountAppsNumber={accountAppsNumber}
						accountIcon={accountIcon}
						accountsSearch={accountsSearch}
						currentAccount={currentAccount}
					/>
				)}

			<div className="dashboard-navigation-body dashboard-navigation-container-dropdown">
				{dashboardNavigationItems.map((dashboardNavigation, index) => (
					<DashboardNavigationItem
						dashboardNavigation={dashboardNavigation}
						key={index}
					/>
				))}
			</div>
		</div>
	);
}
