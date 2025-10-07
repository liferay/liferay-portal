/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Skeleton} from '~/components';
import {MENU_TYPES} from '~/features/project/utils/constants';

const SideMenuSkeleton = () => {
	return (
		<div className="bg-neutral-1 cp-side-menu ml-4 pl-4 pt-4">
			<ul className="list-unstyled mr-2">
				{Object.entries(MENU_TYPES).map((menuType) => {
					const [menuKey, menuName] = menuType;

					return (
						<li key={menuKey}>
							<Skeleton
								className="mb-1"
								height={36}
								width={
									menuName !== MENU_TYPES.activation
										? 120
										: 200
								}
							/>
						</li>
					);
				})}
			</ul>
		</div>
	);
};

export default SideMenuSkeleton;
