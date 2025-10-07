/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {NavLink} from 'react-router-dom';

import {DashboardListItems} from './DashboardNavigation';

import './DashboardNavigationItem.scss';

type DashboardNavigationItemProps = {
	dashboardNavigation: DashboardListItems;
};

export default function DashboardNavigationItem({
	dashboardNavigation,
}: DashboardNavigationItemProps) {
	const {itemTitle, path, symbol} = dashboardNavigation;

	return (
		<>
			<NavLink
				className={({isActive}) =>
					classNames('dashboard-navigation-body-list', {
						'dashboard-navigation-body-list-selected': isActive,
					})
				}
				to={path}
			>
				{({isActive}) => {
					const active = isActive || dashboardNavigation.active;

					return (
						<span className="align-items-center d-flex">
							<ClayIcon
								className={classNames(
									'dashboard-navigation-body-list-icon',
									{
										'dashboard-navigation-body-list-icon-selected':
											active,
									}
								)}
								fontSize={16}
								symbol={symbol as string}
							/>

							<span
								className={classNames(
									'dashboard-navigation-body-list-text',
									{
										'dashboard-navigation-body-list-text-selected':
											active,
									}
								)}
							>
								{itemTitle}
							</span>
						</span>
					);
				}}
			</NavLink>
		</>
	);
}
