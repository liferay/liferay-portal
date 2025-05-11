/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import classNames from 'classnames';
import {NavLink} from 'react-router-dom';

import {DashboardListItems} from './DashboardNavigation';

import './DashboardNavigationList.scss';

type DashboardNavigationListProps = {
	dashboardNavigation: DashboardListItems;
};

export function DashboardNavigationList({
	dashboardNavigation,
}: DashboardNavigationListProps) {
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
				{({isActive}) => (
					<span className="align-items-center d-flex">
						<ClayIcon
							className={classNames(
								'dashboard-navigation-body-list-icon',
								{
									'dashboard-navigation-body-list-icon-selected':
										isActive,
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
										isActive,
								}
							)}
						>
							{itemTitle}
						</span>
					</span>
				)}
			</NavLink>
		</>
	);
}
