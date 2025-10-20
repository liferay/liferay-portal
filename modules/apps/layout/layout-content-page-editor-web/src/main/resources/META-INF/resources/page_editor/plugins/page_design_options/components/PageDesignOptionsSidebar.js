/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayAlert from '@clayui/alert';
import ClayCard from '@clayui/card';
import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import ClayTabs from '@clayui/tabs';
import classNames from 'classnames';
import {useId} from 'frontend-js-components-web';
import {sub} from 'frontend-js-web';
import React, {useCallback, useEffect, useMemo, useState} from 'react';

import {LAYOUT_TYPES} from '../../../app/config/constants/layoutTypes';
import {config} from '../../../app/config/index';
import {useDispatch, useSelector} from '../../../app/contexts/StoreContext';
import LayoutService from '../../../app/services/LayoutService';
import changeMasterLayout from '../../../app/thunks/changeMasterLayout';
import SidebarPanelHeader from '../../../common/components/SidebarPanelHeader';
import {useSetStyleBook, useStyleBook} from '../hooks/useStyleBook';

const OPTIONS_TYPES = {
	master: 'master',
	styleBook: 'styleBook',
};

export default function PageDesignOptionsSidebar() {
	const dispatch = useDispatch();
	const selectedStyleBook = useStyleBook();
	const setSelectedStyleBook = useSetStyleBook();

	const [styleBooks, setStyleBooks] = useState(config.styleBooks);

	const masterLayoutPlid = useSelector(
		(state) => state.masterLayout?.masterLayoutPlid
	);

	const onSelectMasterLayout = useCallback(
		(masterLayout) => {
			dispatch(
				changeMasterLayout({
					masterLayoutPlid: masterLayout.masterLayoutPlid,
				})
			).then(({styleBookEntryERC, styleBooks = []}) => {
				setStyleBooks(styleBooks);

				if (!styleBooks.length) {
					setSelectedStyleBook({
						styleBookEntryERC: '',
						tokenValues: {},
					});

					return;
				}

				if (Liferay.FeatureFlags['LPD-30204']) {
					const selectedStyleBook = styleBooks.find(
						(styleBook) =>
							styleBook.styleBookEntryERC === styleBookEntryERC
					);

					if (selectedStyleBook) {
						setSelectedStyleBook({...selectedStyleBook});
					}
					else {
						setSelectedStyleBook({...styleBooks[0]});
					}
				}
				else {

					// Changing the master layout should only affect the
					// selected stylebook if the styleBookEntryERC is equal to 0
					// which means that the stylebook is inherited

					if (selectedStyleBook.styleBookEntryERC === '') {
						setSelectedStyleBook({...styleBooks[0]});
					}
				}
			});
		},
		[dispatch, selectedStyleBook.styleBookEntryERC, setSelectedStyleBook]
	);

	const onSelectStyleBook = useCallback(
		(styleBookEntryERC) => {
			LayoutService.changeStyleBookEntry({
				onNetworkStatus: dispatch,
				styleBookEntryERC,
			}).then(({tokenValues}) => {
				setSelectedStyleBook({styleBookEntryERC, tokenValues});
			});
		},
		[setSelectedStyleBook, dispatch]
	);

	useEffect(() => {
		if (selectedStyleBook && document.documentElement) {
			Object.values(selectedStyleBook.tokenValues).forEach((token) => {
				document.documentElement.style.setProperty(
					`--${token.cssVariable}`,
					token.value
				);
			});
		}
	}, [selectedStyleBook]);

	const tabs = useMemo(
		() =>
			getTabs(
				masterLayoutPlid,
				selectedStyleBook,
				onSelectMasterLayout,
				onSelectStyleBook,
				styleBooks
			),
		[
			masterLayoutPlid,
			onSelectMasterLayout,
			onSelectStyleBook,
			selectedStyleBook,
			styleBooks,
		]
	);

	const [activeTabId, setActiveTabId] = useState(0);
	const tabIdNamespace = useId();

	const getTabId = (tabId) => `${tabIdNamespace}tab${tabId}`;
	const getTabPanelId = (tabId) => `${tabIdNamespace}tabPanel${tabId}`;

	return (
		<>
			<SidebarPanelHeader
				iconRight={
					config.lookAndFeelURL && (
						<ClayLink
							displayType="secondary"
							href={config.lookAndFeelURL}
							monospaced
							title={Liferay.Language.get(
								'more-page-design-options'
							)}
						>
							<ClayIcon symbol="cog" />
						</ClayLink>
					)
				}
			>
				{Liferay.Language.get('page-design-options')}
			</SidebarPanelHeader>

			<ClayTabs
				activation="automatic"
				active={activeTabId}
				className="flex-shrink-0 page-editor__sidebar__page-design-options__tabs px-3"
				onActiveChange={setActiveTabId}
			>
				{tabs.map((tab, index) => (
					<ClayTabs.Item
						innerProps={{
							'aria-controls': getTabPanelId(index),
							'id': getTabId(index),
						}}
						key={index}
					>
						{tab.label}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content
				activeIndex={activeTabId}
				className="overflow-auto px-3"
				fade
			>
				{tabs.map(({icon, label, options, type}, index) => (
					<ClayTabs.TabPane
						aria-label={sub(
							Liferay.Language.get('select-x'),
							label
						)}
						className="p-0"
						id={getTabPanelId(index)}
						key={index}
					>
						<OptionList icon={icon} options={options} type={type} />
					</ClayTabs.TabPane>
				))}
			</ClayTabs.Content>
		</>
	);
}

const OptionList = ({options = [], icon, type}) => {
	if (
		type === OPTIONS_TYPES.styleBook &&
		!config.styleBookEnabled &&
		!Liferay.FeatureFlags['LPD-30204']
	) {
		return (
			<ClayAlert className="mt-3" displayType="info">
				{config.isPrivateLayoutsEnabled
					? Liferay.Language.get(
							'this-page-is-using-a-different-theme-than-the-one-set-for-public-pages'
						)
					: Liferay.Language.get(
							'this-page-is-using-a-different-theme-than-the-one-set-for-all-pages'
						)}
			</ClayAlert>
		);
	}

	if (type === OPTIONS_TYPES.styleBook && !options.length) {
		return (
			<ClayAlert className="mt-3" displayType="info">
				{Liferay.Language.get(
					'the-current-theme-does-not-support-style-books'
				)}
			</ClayAlert>
		);
	}

	return (
		<>
			{Liferay.FeatureFlags['LPD-30204'] &&
				type === OPTIONS_TYPES.styleBook &&
				!!options.length && (
					<ClayAlert className="mt-3" displayType="info" title="Info">
						{sub(
							Liferay.Language.get(
								'only-style-books-based-on-the-frontend-token-definition-provided-by-x-are-visible'
							),
							config.themeName
						)}
					</ClayAlert>
				)}

			<ul className="list-unstyled mt-4">
				{options.map(
					(
						{imagePreviewURL, isActive, name, onClick, subtitle},
						index
					) => (
						<li key={index}>
							<ClayCard
								aria-label={name}
								className={classNames({
									'page-editor__sidebar__design-options__tab-card--active':
										isActive,
								})}
								displayType="file"
								onClick={() => {
									if (!isActive) {
										onClick();
									}
								}}
								onKeyDown={(event) => {
									if (event.key === 'Enter' && !isActive) {
										onClick();
									}
								}}
								role="button"
								selectable
								tabIndex="0"
							>
								<ClayCard.AspectRatio
									className="card-item-first"
									containerAspectRatio="16/9"
								>
									{imagePreviewURL ? (
										<img
											alt="thumbnail"
											className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid"
											src={imagePreviewURL}
										/>
									) : (
										<div className="aspect-ratio-item aspect-ratio-item-center-middle aspect-ratio-item-fluid card-type-asset-icon">
											<ClayIcon symbol={icon} />
										</div>
									)}

									{isActive && (
										<ClaySticker
											displayType="primary"
											position="bottom-left"
										>
											<ClayIcon symbol="check-circle" />
										</ClaySticker>
									)}
								</ClayCard.AspectRatio>

								<ClayCard.Body>
									<ClayCard.Row>
										<div className="autofit-col autofit-col-expand">
											<section className="autofit-section">
												<ClayCard.Description displayType="title">
													{name}
												</ClayCard.Description>

												{subtitle && (
													<ClayCard.Description displayType="subtitle">
														{subtitle}
													</ClayCard.Description>
												)}
											</section>
										</div>
									</ClayCard.Row>
								</ClayCard.Body>
							</ClayCard>
						</li>
					)
				)}
			</ul>
		</>
	);
};

function getTabs(
	masterLayoutPlid,
	selectedStyleBook,
	onSelectMasterLayout,
	onSelectStyleBook,
	styleBooks
) {
	const tabs = [];

	if (config.layoutType !== LAYOUT_TYPES.master) {
		tabs.push({
			icon: 'page',
			label: Liferay.Language.get('master'),
			options: config.masterLayouts.map((masterLayout) => ({
				...masterLayout,
				isActive: masterLayoutPlid === masterLayout.masterLayoutPlid,
				onClick: () => onSelectMasterLayout(masterLayout),
			})),
			type: OPTIONS_TYPES.master,
		});
	}

	tabs.push({
		icon: 'magic',
		label: Liferay.Language.get('style-book'),
		options: styleBooks.map((styleBook) => ({
			...styleBook,
			isActive:
				selectedStyleBook.styleBookEntryERC ===
				styleBook.styleBookEntryERC,
			onClick: () => onSelectStyleBook(styleBook.styleBookEntryERC),
		})),
		type: OPTIONS_TYPES.styleBook,
	});

	return tabs;
}
