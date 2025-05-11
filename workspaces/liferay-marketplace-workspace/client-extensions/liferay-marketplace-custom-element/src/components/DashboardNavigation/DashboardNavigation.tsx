/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import useAccounts from '../../hooks/data/useAccounts';
import AccountSearchDropdown from './AccountSearchDropdown';
import {DashboardNavigationList} from './DashboardNavigationList';

import './DashboardNavigation.scss';

export type DashboardListItems = {
	itemTitle: string;
	path: string;
	symbol: string;
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
	return (
		<div className="dashboard-navigation-container">
			{accountsSearch && (
				<AccountSearchDropdown
					accountAppsNumber={accountAppsNumber}
					accountIcon={accountIcon}
					accountsSearch={accountsSearch}
					currentAccount={currentAccount}
				/>
			)}

			<div className="dashboard-navigation-body">
				{dashboardNavigationItems.map((dashboardNavigation, index) => (
					<DashboardNavigationList
						dashboardNavigation={dashboardNavigation}
						key={index}
					/>
				))}
			</div>
		</div>
	);
}
