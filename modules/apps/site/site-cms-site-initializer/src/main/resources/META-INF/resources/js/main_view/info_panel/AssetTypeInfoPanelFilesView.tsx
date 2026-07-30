/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Icon} from '@clayui/core';
import DropDown, {Align} from '@clayui/drop-down';
import Tabs from '@clayui/tabs';
import React, {useContext, useState} from 'react';

import {AssetTypeInfoPanelContext, IAssetTypeInfoPanelContext} from './context';
import {TABS} from './tab_content';

const DEFAULT_MAIN_TABS = [TABS.DETAILS, TABS.CATEGORIZATION, TABS.PERFORMANCE];

const AssetTypeInfoPanelFilesView = () => {
	const {actions}: IAssetTypeInfoPanelContext = useContext(
		AssetTypeInfoPanelContext
	);

	const [active, setActive] = useState(0);

	const href = actions?.versions?.href;

	const MAIN_TABS = href
		? DEFAULT_MAIN_TABS
		: [...DEFAULT_MAIN_TABS, TABS.COMMENTS];

	const DROPDOWN_TABS = [
		...(href ? [TABS.VERSIONS, TABS.COMMENTS] : []),
		TABS.PROJECTS,
	];

	const ALL_TABS = [...MAIN_TABS, ...DROPDOWN_TABS];

	return (
		<>
			<Tabs active={active} justified={false} onActiveChange={setActive}>
				{MAIN_TABS.map((tab) => (
					<Tabs.Item
						innerProps={{
							'aria-controls': `tabpanel-${tab.id}`,
						}}
						key={tab.id}
					>
						{tab.name}
					</Tabs.Item>
				))}

				{!!DROPDOWN_TABS.length && (
					<div onClick={(event) => event.stopPropagation()}>
						<DropDown
							alignmentPosition={Align.BottomLeft}
							closeOnClick={true}
							hasRightSymbols
							trigger={
								<Tabs.Item
									active={active >= MAIN_TABS.length}
									innerProps={{
										'aria-controls': `tabpanel-${
											active >= MAIN_TABS.length
												? ALL_TABS[active]?.id
												: DROPDOWN_TABS[0]?.id
										}`,
									}}
								>
									{Liferay.Language.get('more')}

									<span className="inline-item inline-item-after">
										<Icon symbol="caret-bottom" />
									</span>
								</Tabs.Item>
							}
						>
							{DROPDOWN_TABS.map((tab) => {
								const tabIndex = ALL_TABS.findIndex(
									(item) => item.id === tab.id
								);

								return (
									<DropDown.Item
										active={active === tabIndex}
										key={tab.id}
										onClick={() => setActive(tabIndex)}
									>
										{tab.name}
									</DropDown.Item>
								);
							})}
						</DropDown>
					</div>
				)}
			</Tabs>

			<Tabs.Content active={active} fade>
				{ALL_TABS.map((tab, index) => (
					<Tabs.TabPane className="p-4" key={tab.id}>
						{active === index ? <tab.component /> : null}
					</Tabs.TabPane>
				))}
			</Tabs.Content>
		</>
	);
};

export default AssetTypeInfoPanelFilesView;
