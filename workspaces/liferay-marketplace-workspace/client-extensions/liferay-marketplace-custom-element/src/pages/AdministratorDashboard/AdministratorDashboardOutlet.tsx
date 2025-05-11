/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Outlet} from 'react-router-dom';

import {DashboardNavigation} from '../../components/DashboardNavigation/DashboardNavigation';
import i18n from '../../i18n';

export const dashboardNavigationItems = [
	{
		itemTitle: i18n.translate('summary'),
		items: [],
		path: '/',
		symbol: 'polls',
	},
	{
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
		itemTitle: i18n.translate('solutions'),
		path: '/solutions',
		symbol: 'edit-layout',
	},
	{
		itemTitle: i18n.translate('trials'),
		path: '/trial',
		symbol: 'squares-clock',
	},
	{
		itemTitle: i18n.translate('publisher-requests'),
		path: '/publisher-request',
		symbol: 'envelope-closed',
	},
];

const AdministratorDashboardOutlet = () => (
	<div className="d-flex">
		<div className="d-flex dashboard-navigation-container">
			<div className="dashboard-navigation-body">
				<DashboardNavigation
					dashboardNavigationItems={dashboardNavigationItems}
				/>
			</div>
		</div>

		<span className="h-vh-100 ml-6 w-100">
			<Outlet />
		</span>
	</div>
);

export default AdministratorDashboardOutlet;
