/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBadge from '@clayui/badge';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import {openSelectionModal} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

type CategoryItem = {
	childCategories?: {panelApps: {portletId: string}[]}[];
	firstTimeAccess?: boolean;
	homeURL: string;
	key: string;
	label: string;
};

type Data = {
	items: {categories: CategoryItem[]; sites: GroupItem[]};
	portletNamespace: string;
	selectedPortletId: string;
	sites: Sites;
} | null;

type GroupItem = {
	children: SiteItem[];
	key: 'categories' | 'sites';
	label: string;
};

type Site = {
	current: boolean;
	key: string;
	label: string;
	url?: string;
};

type SiteItem = {
	current?: boolean;
	key: string;
	label: string;
	onClick?: () => void;
	url?: string;
};

type Sites = {recentSites: Site[]; viewAllURL: string};

export default function GlobalMenu({
	panelAppsURL,
	selectedPortletId,
}: {
	panelAppsURL: string;
	selectedPortletId: string;
}) {
	const [data, setData] = useState<Data>(null);

	useEffect(() => {
		const fetchData = async () => {
			const response = await fetch(panelAppsURL);

			const {cms, items, portletNamespace, sites} = await response.json();

			setData({
				items: {
					categories: normalizeCategoryItems({cms, items}),
					sites: normalizeSiteItems({portletNamespace, sites}),
				},
				portletNamespace,
				selectedPortletId,
				sites,
			});
		};

		fetchData();
	}, [panelAppsURL, selectedPortletId]);

	if (!data) {
		return null;
	}

	const {items} = data;

	return (
		<ClayDropdown
			trigger={
				<ClayButtonWithIcon
					aria-haspopup="dialog"
					className="control-menu-nav-link dropdown-toggle lfr-portal-tooltip"
					data-qa-id="globalMenu"
					data-title-set-as-html
					data-tooltip-align="bottom-left"
					displayType="unstyled"
					size="sm"
					symbol="grid"
				/>
			}
		>
			<ClayDropdown.ItemList items={items.categories}>
				{(item) => {
					const {
						childCategories,
						firstTimeAccess,
						homeURL,
						key,
						label,
					} = item as CategoryItem;

					return (
						<ClayDropdown.Item
							active={childCategories?.some((category) =>
								category.panelApps.some(
									(panelApp) =>
										panelApp.portletId === selectedPortletId
								)
							)}
							href={homeURL}
							key={key}
						>
							{label}

							{key === 'cms' && firstTimeAccess ? (
								<ClayBadge
									className="c-ml-3"
									displayType="primary"
									label={Liferay.Language.get(
										'new'
									).toUpperCase()}
								/>
							) : null}
						</ClayDropdown.Item>
					);
				}}
			</ClayDropdown.ItemList>

			<ClayDropdown.ItemList items={items.sites}>
				{(item) => {
					const groupItem = item as GroupItem;

					return (
						<ClayDropdown.Group
							header={groupItem.label}
							items={groupItem.children}
							key={groupItem.key}
						>
							{(item) => (
								<ClayDropdown.Item
									{...item}
									active={item.current}
									href={item.url}
									key={item.key}
								>
									{item.label}
								</ClayDropdown.Item>
							)}
						</ClayDropdown.Group>
					);
				}}
			</ClayDropdown.ItemList>
		</ClayDropdown>
	);
}

function normalizeCategoryItems({
	cms,
	items,
}: {
	cms: {key: string; label: string; url: string};
	items: CategoryItem[];
}): CategoryItem[] {
	items.splice(2, 0, {...cms, homeURL: cms.url});

	return [...items];
}

function normalizeSiteItems({
	portletNamespace,
	sites,
}: {
	portletNamespace: string;
	sites: Sites;
}): GroupItem[] {
	const children: SiteItem[] = [...sites.recentSites];

	if (sites?.viewAllURL) {
		children.push({
			key: 'view-all',
			label: Liferay.Language.get('view-all-sites'),
			onClick: () => {
				openSelectionModal({
					id: `${portletNamespace}selectSite`,
					onSelect: (selectedItem: {url: string}) => {
						navigate(selectedItem.url);
					},
					selectEventName: `${portletNamespace}selectSite`,
					title: Liferay.Language.get('select-site'),
					url: sites.viewAllURL,
				});
			},
		});
	}

	return [
		{
			children,
			key: 'sites',
			label: Liferay.Language.get('recent-sites'),
		},
	];
}
