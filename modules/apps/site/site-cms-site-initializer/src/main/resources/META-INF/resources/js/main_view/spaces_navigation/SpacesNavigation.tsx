/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ClayButtonWithIcon} from '@clayui/button';
import {VerticalNav} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import {navigate, sub} from 'frontend-js-web';
import React from 'react';

import SpaceSticker from '../../common/components/SpaceSticker';
import {LogoColor} from '../../common/types/Space';

export interface AssetLibrary {
	active?: boolean;
	id: number;
	name: string;
	settings?: {
		logoColor: string;
	};
	url: string;
}

interface SpacesNavigationProps {
	allSpacesActive: boolean;
	allSpacesURL: string;
	assetLibraries: AssetLibrary[];
	assetLibrariesCount: number;
	newSpaceURL: string;
	showAddButton: boolean;
}

interface VerticalNavItem {
	active?: boolean;
	href?: string;
	icon?: string;
	itemClass?: string;
	items?: VerticalNavItem[];
	label: string;
	menubarAction?: {
		'aria-label'?: string;
		'onClick'?: Function;
		'role'?: string;
		'title'?: string;
	};
	stickerDisplayType?: LogoColor;
}

const SpacesNavigation: React.FC<SpacesNavigationProps> = ({
	allSpacesActive,
	allSpacesURL,
	assetLibraries,
	assetLibrariesCount,
	newSpaceURL,
	showAddButton,
}) => {
	const onAddButtonClick = (event: any) => {
		event.preventDefault();

		navigate(newSpaceURL.toString());

		event.stopPropagation();
	};

	const spacesNavigationItem = {
		items: [
			...assetLibraries.map(({active, name, settings, url}) => ({
				active,
				href: url,
				label: name,
				stickerDisplayType: settings?.logoColor as LogoColor,
			})),
			{
				active: allSpacesActive,
				href: allSpacesURL,
				icon: 'box-container',
				label: sub(
					Liferay.Language.get('all-spaces-x'),
					assetLibrariesCount
				),
			},
		],
		label: Liferay.Language.get('spaces'),
	};

	return (
		<VerticalNav
			aria-label={Liferay.Language.get('spaces')}
			defaultExpandedKeys={new Set([Liferay.Language.get('spaces')])}
			displayType="primary"
			items={[
				{
					...spacesNavigationItem,
					...(showAddButton && {
						menubarAction: {
							'aria-label': Liferay.Language.get('add-space'),
							'onClick': onAddButtonClick,
							'role': 'menuitem',
							'title': Liferay.Language.get('add-space'),
						},
					}),
				},
			]}
		>
			{(item: VerticalNavItem) => {
				return (
					<VerticalNav.Item
						active={item.active}
						href={item.href}
						items={item.items}
						key={item.label}
						menubarAction={
							item.menubarAction as React.ComponentProps<
								typeof ClayButtonWithIcon
							>
						}
						textValue={item.label}
					>
						<div className="autofit-row">
							{item.stickerDisplayType ? (
								<SpaceSticker
									aria-hidden="true"
									displayType={item.stickerDisplayType}
									name={item.label}
									size="sm"
								/>
							) : (
								<>
									{item.icon && (
										<div className="autofit-col">
											<ClayIcon symbol={item.icon} />
										</div>
									)}

									<div className="autofit-col autofit-col-expand">
										{item.label}
									</div>
								</>
							)}
						</div>
					</VerticalNav.Item>
				);
			}}
		</VerticalNav>
	);
};

export default SpacesNavigation;
