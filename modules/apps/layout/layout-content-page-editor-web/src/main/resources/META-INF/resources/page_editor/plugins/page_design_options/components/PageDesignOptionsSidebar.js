/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClayTabs from '@clayui/tabs';
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
import MasterLayoutsList from './MasterLayoutsList';
import StyleBooksList from './StyleBooksList';

export default function PageDesignOptionsSidebar() {
	const dispatch = useDispatch();
	const selectedStyleBook = useStyleBook();
	const setSelectedStyleBook = useSetStyleBook();

	const [styleBooks, setStyleBooks] = useState(config.styleBooks);

	const masterLayoutPageTemplateEntryERC = useSelector(
		(state) => state.masterLayout?.masterLayoutPageTemplateEntryERC
	);

	const onSelectMasterLayout = useCallback(
		(masterLayout) => {
			dispatch(
				changeMasterLayout({
					masterLayoutPageTemplateEntryERC:
						masterLayout.masterLayoutPageTemplateEntryERC,
				})
			).then(
				({
					styleBookEntryERC,
					styleBookEntryScopeERC,
					styleBooks = [],
				}) => {
					setStyleBooks(styleBooks);

					if (!styleBooks.length) {
						setSelectedStyleBook({
							styleBookEntryERC: '',
							styleBookEntryScopeERC: '',
							tokenValues: {},
						});

						return;
					}

					const selectedStyleBook = styleBooks.find(
						(styleBook) =>
							styleBook.styleBookEntryERC === styleBookEntryERC &&
							(styleBook.styleBookEntryScopeERC || '') ===
								(styleBookEntryScopeERC || '')
					);

					if (selectedStyleBook) {
						setSelectedStyleBook({...selectedStyleBook});
					}
					else {
						setSelectedStyleBook({...styleBooks[0]});
					}
				}
			);
		},
		[dispatch, setSelectedStyleBook]
	);

	const onSelectStyleBook = useCallback(
		({styleBookEntryERC, styleBookEntryScopeERC}) => {
			LayoutService.changeStyleBookEntry({
				onNetworkStatus: dispatch,
				styleBookEntryERC,
				styleBookEntryScopeERC,
			}).then(({tokenValues}) => {
				setSelectedStyleBook({
					styleBookEntryERC,
					styleBookEntryScopeERC,
					tokenValues,
				});
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

	const [activeTabId, setActiveTabId] = useState(0);
	const tabIdNamespace = useId();

	const tabs = useMemo(() => {
		const result = [];

		if (config.layoutType !== LAYOUT_TYPES.master) {
			result.push({
				content: (
					<MasterLayoutsList
						masterLayoutPageTemplateEntryERC={
							masterLayoutPageTemplateEntryERC
						}
						masterLayouts={config.masterLayouts}
						onSelectMasterLayout={onSelectMasterLayout}
					/>
				),
				label: Liferay.Language.get('master'),
			});
		}

		result.push({
			content: (
				<StyleBooksList
					onSelectStyleBook={onSelectStyleBook}
					selectedStyleBook={selectedStyleBook}
					styleBooks={styleBooks}
					themeName={config.themeName}
				/>
			),
			label: Liferay.Language.get('style-book'),
		});

		return result;
	}, [
		masterLayoutPageTemplateEntryERC,
		onSelectMasterLayout,
		onSelectStyleBook,
		selectedStyleBook,
		styleBooks,
	]);

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
				{tabs.map(({content, label}, index) => (
					<ClayTabs.TabPane
						aria-label={sub(
							Liferay.Language.get('select-x'),
							label
						)}
						className="p-0"
						id={getTabPanelId(index)}
						key={index}
					>
						{content}
					</ClayTabs.TabPane>
				))}
			</ClayTabs.Content>
		</>
	);
}
