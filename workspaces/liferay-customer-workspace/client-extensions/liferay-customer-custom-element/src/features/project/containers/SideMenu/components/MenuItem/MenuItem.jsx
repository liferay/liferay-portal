/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import classNames from 'classnames';
import {memo, useEffect, useMemo} from 'react';
import {Link, useMatch, useResolvedPath} from 'react-router-dom';

import {Button} from '~/components';
import {navigationIcons} from '../../utils/navigationIcons';

const MenuItem = ({children, iconKey, setActive, to}) => {
	const isActive = !!useMatch({path: useResolvedPath(to)?.pathname});

	const isPortalIcon = iconKey === 'portal';

	useEffect(() => {
		if (setActive) {
			setActive(isActive);
		}
	}, [isActive, setActive]);

	const Icon = useMemo(() => {
		try {
			if (iconKey) {
				const [activeIcon, inactiveIcon] = navigationIcons[iconKey]
					|| navigationIcons['liferay'];

				return isActive ? activeIcon : inactiveIcon;
			}
		}
		catch (error) {
			console.error('Error:', error);
		}
	}, [iconKey, isActive]);

	return (
		<li>
			<Link to={to}>
				<Button
					className={classNames(
						'btn-borderless mb-1 px-2 py-2 rounded text-neutral-10',
						{
							'align-items-center d-flex mt-1': !!iconKey,
							'cp-menu-btn-active': isActive,
						}
					)}
				>
					{Icon && (
						<Icon
							className={classNames('mr-2', {
								'portal-icon': isPortalIcon,
							})}
							height={16}
							width={16}
						/>
					)}

					{children}
				</Button>
			</Link>
		</li>
	);
};

export default memo(MenuItem);
