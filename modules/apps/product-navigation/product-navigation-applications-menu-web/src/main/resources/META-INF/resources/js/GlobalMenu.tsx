/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayBadge from '@clayui/badge';
import {ClayButtonWithIcon} from '@clayui/button';
import ClayDropdown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {useEventListener} from '@liferay/frontend-js-react-web';
import classNames from 'classnames';
import {openSelectionModal} from 'frontend-js-components-web';
import {fetch, navigate} from 'frontend-js-web';
import React, {useCallback, useMemo, useRef, useState} from 'react';

import '../css/GlobalMenu.scss';

type CategoryItem = {
	active: boolean;
	className?: string;
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
	logoURL?: string;
	onClick?: () => void;
	url?: string;
};

type Sites = {mySites: Site[]; recentSites: Site[]; viewAllURL: string};

export default function GlobalMenu({
	panelAppsURL,
	selectedPortletId,
}: {
	panelAppsURL: string;
	selectedPortletId: string;
}) {
	const [active, setActive] = useState<boolean>(false);
	const [data, setData] = useState<Data>(null);
	const fetchedRef = useRef<boolean>(false);

	const openButtonTitle = useMemo(() => getOpenMenuTooltipMarkup(), []);

	const numberOfSiteItems = data?.items.sites.reduce(
		(total, group) => total + group.children.length,
		0
	);

	const fetchData = useCallback(async () => {
		if (data || fetchedRef.current) {
			return;
		}

		fetchedRef.current = true;

		try {
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
		}
		finally {
			fetchedRef.current = false;
		}
	}, [data, panelAppsURL, selectedPortletId]);

	useEventListener(
		'keydown',
		async (event) => {
			const {altKey, ctrlKey, key} = event as KeyboardEvent;

			const AKey = Liferay.Browser.isMac() ? 'å' : 'a';

			if (ctrlKey && altKey && key.toLowerCase() === AKey) {
				event.preventDefault();

				await fetchData();

				setActive(true);
			}
		},
		true,
		document
	);

	return (
		<ClayDropdown
			active={active}
			menuElementAttrs={{
				className: 'cadmin global-menu pb-0 pt-3',
			}}
			onActiveChange={setActive}
			trigger={
				<ClayButtonWithIcon
					aria-haspopup="dialog"
					aria-label={Liferay.Language.get('open-applications-menu')}
					className="control-menu-nav-link dropdown-toggle lfr-portal-tooltip"
					data-qa-id="globalMenu"
					data-title={openButtonTitle}
					data-title-set-as-html
					data-tooltip-align="bottom-left"
					displayType="unstyled"
					onClick={fetchData}
					onFocus={fetchData}
					onMouseOver={fetchData}
					size="sm"
					symbol="grid"
				/>
			}
		>
			<ClayDropdown.ItemList
				className="categories-list text-4"
				items={data?.items.categories}
			>
				{(item) => {
					const {
						active,
						className,
						firstTimeAccess,
						homeURL,
						key,
						label,
					} = item as CategoryItem;

					return (
						<ClayDropdown.Item
							active={active}
							className={classNames(
								'align-items-center c-mb-2 d-inline-flex',
								className
							)}
							href={homeURL}
							key={key}
						>
							<img
								alt=""
								className="c-mx-1"
								src={`${Liferay.ThemeDisplay.getPathThemeImages()}/product_icons/${key}_sm.svg`}
							/>

							<span className="c-ml-2 c-pl-1 text-dark">
								{label}
							</span>

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

			{numberOfSiteItems ? (
				<ClayDropdown.ItemList
					className="border-top c-pb-3 c-pt-2 sites-list"
					items={data?.items.sites}
				>
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
										className="c-py-1 text-primary"
										href={item.url}
										key={item.key}
									>
										{item.key !== 'view-all' ? (
											<ClaySticker
												className="c-mr-2"
												size="sm"
											>
												{item.logoURL ? (
													<ClaySticker.Image
														alt=""
														src={item.logoURL}
													/>
												) : (
													<ClayIcon symbol="sites" />
												)}
											</ClaySticker>
										) : null}

										{item.label}
									</ClayDropdown.Item>
								)}
							</ClayDropdown.Group>
						);
					}}
				</ClayDropdown.ItemList>
			) : null}
		</ClayDropdown>
	);
}

function getOpenMenuTooltipMarkup() {
	const altKey = Liferay.Browser.isMac() ? '⌥' : 'Alt';

	return `
	<div>${Liferay.Language.get('open-applications-menu')}</div>
	<kbd class="c-kbd c-kbd-dark mt-1">
		<kbd class="c-kbd">Ctrl</kbd>

		<span class="c-kbd-separator">+</span>

		<kbd class="c-kbd">${altKey}</kbd>

		<span class="c-kbd-separator">+</span>

		<kbd class="c-kbd">A</kbd>
	</kbd>
`
		.replaceAll('\n', '')
		.replaceAll('\t', '');
}

function normalizeCategoryItems({
	cms,
	items,
}: {
	cms: {active: boolean; key: string; label: string; url: string};
	items: CategoryItem[];
}): CategoryItem[] {
	const categoryItems = items.map((item) =>
		item.key === 'control_panel' ? {...item, className: 'c-mt-2'} : item
	);

	if (Liferay.FeatureFlags['LPD-17564']) {
		categoryItems.splice(2, 0, {...cms, homeURL: cms.url});
	}

	return categoryItems;
}

function normalizeSiteItems({
	portletNamespace,
	sites,
}: {
	portletNamespace: string;
	sites: Sites;
}): GroupItem[] {
	const children: SiteItem[] = [
		...(sites.recentSites ?? []),
		...(sites.mySites ?? []).filter(
			({label}) => label === Liferay.Language.get('global')
		),
	];

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
			label: Liferay.Language.get('sites'),
		},
	];
}
