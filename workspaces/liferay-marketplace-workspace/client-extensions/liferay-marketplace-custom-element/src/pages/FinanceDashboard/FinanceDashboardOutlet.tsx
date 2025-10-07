/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Outlet} from 'react-router-dom';

import {DashboardNavigation} from '../../components/DashboardNavigation/DashboardNavigation';
import {PageRenderer} from '../../components/Page';
import useAccounts, {useAccount} from '../../hooks/data/useAccounts';
import {getAccountImage} from '../../utils/util';

const FinanceDashboardOutlet = () => {
	const accountsSearch = useAccounts();
	const {data: selectedAccount, error, isLoading} = useAccount();

	return (
		<PageRenderer error={error} isLoading={isLoading}>
			<div className="finance-orders-dashboard-page-container">
				<DashboardNavigation
					accountIcon={getAccountImage(selectedAccount?.logoURL)}
					accountsSearch={accountsSearch}
					currentAccount={selectedAccount as any}
					dashboardNavigationItems={[
						{
							itemTitle: 'Orders',
							path: '/',
							symbol: 'change-list',
						},
						{
							itemTitle: 'Payments',
							path: '/payments',
							symbol: 'credit-card',
						},
					]}
				/>
				<span className="h-vh-100 ml-6 w-100">
					<Outlet />
				</span>
			</div>
		</PageRenderer>
	);
};

export default FinanceDashboardOutlet;
