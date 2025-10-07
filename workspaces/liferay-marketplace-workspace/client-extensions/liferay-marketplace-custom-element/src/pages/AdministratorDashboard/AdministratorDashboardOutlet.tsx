/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Outlet} from 'react-router-dom';

import {DashboardNavigation} from '../../components/DashboardNavigation/DashboardNavigation';
import {useMarketplaceContext} from '../../context/MarketplaceContext';
import i18n from '../../i18n';

const dashboardNavigationItems = [
	{
		itemTitle: i18n.translate('summary'),
		items: [],
		path: '/',
		symbol: 'polls',
	},
	{
		adminOnly: true,
		itemTitle: i18n.translate('orders'),
		path: '/orders',
		symbol: 'order-form',
	},
	{
		itemTitle: i18n.translate('apps'),
		path: '/apps',
		symbol: 'slideshow',
	},
	{
		adminOnly: true,
		itemTitle: i18n.translate('solutions'),
		path: '/solutions',
		symbol: 'edit-layout',
	},
	{
		adminOnly: true,
		itemTitle: i18n.translate('trials'),
		path: '/trial',
		symbol: 'squares-clock',
	},
	{
		itemTitle: i18n.translate('publishers'),
		path: '/publishers',
		symbol: 'users',
	},
	{
		adminOnly: true,
		itemTitle: i18n.translate('publisher-requests'),
		path: '/publisher-request',
		symbol: 'envelope-closed',
	},
];

const AdministratorDashboardOutlet = () => {
	const {marketplaceUserAccount} = useMarketplaceContext();

	return (
		<div className="d-flex">
			<div className="d-flex dashboard-navigation-container">
				<DashboardNavigation
					dashboardNavigationItems={dashboardNavigationItems.filter(
						({adminOnly}) =>
							typeof adminOnly === 'boolean'
								? marketplaceUserAccount.isAdmin
								: true
					)}
				/>
			</div>

			<span className="h-vh-100 ml-6 w-100">
				<Outlet />
			</span>
		</div>
	);
};

export default AdministratorDashboardOutlet;
