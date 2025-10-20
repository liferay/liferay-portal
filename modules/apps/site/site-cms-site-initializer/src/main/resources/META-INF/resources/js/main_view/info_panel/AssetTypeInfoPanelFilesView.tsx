/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Icon} from '@clayui/core';
import DropDown, {Align} from '@clayui/drop-down';
import Tabs from '@clayui/tabs';
import React, {useState} from 'react';

import {TABS} from './tab_content';

const DropDownWithState = ({
	children,
	trigger,
	...others
}: {
	children: any;
	trigger: any;
}) => {
	return (
		<DropDown
			alignmentPosition={Align.BottomLeft}
			closeOnClick={true}
			hasRightSymbols
			trigger={trigger}
			{...others}
		>
			{children}
		</DropDown>
	);
};

const AssetTypeInfoPanelFilesView = () => {
	const [active, setActive] = useState(0);

	const dropdownTabsItems = [TABS.VERSIONS, TABS.COMMENTS];

	const tabs = [TABS.DETAILS, TABS.CATEGORIZATION, TABS.PERFORMANCE];

	const allTabs = [...tabs, ...dropdownTabsItems];

	return (
		<>
			<Tabs active={active} justified={false} onActiveChange={setActive}>
				{tabs.map((tab) => (
					<Tabs.Item
						innerProps={{
							'aria-controls': `tabpanel-${tab.id}`,
						}}
						key={tab.id}
					>
						{tab.name}
					</Tabs.Item>
				))}

				<div onClick={(event) => event.stopPropagation()}>
					<DropDownWithState
						trigger={
							<Tabs.Item
								active={active >= tabs.length}
								innerProps={{
									'aria-controls': 'tabpanel-4',
								}}
							>
								{Liferay.Language.get('more')}

								<span className="inline-item inline-item-after">
									<Icon symbol="caret-bottom" />
								</span>
							</Tabs.Item>
						}
					>
						{dropdownTabsItems.map((tab) => {
							const tabIndex = allTabs.findIndex(
								(item) => item.id === tab.id
							);

							return (
								<DropDown.Item
									active={active === tabIndex}
									key={tab.id}
									onClick={() => setActive(tabIndex)}
									role="tab"
								>
									{tab.name}
								</DropDown.Item>
							);
						})}
					</DropDownWithState>
				</div>
			</Tabs>

			<Tabs.Content active={active} fade>
				{allTabs.map((tab) => (
					<Tabs.TabPane className="p-4" key={tab.id}>
						<tab.component />
					</Tabs.TabPane>
				))}
			</Tabs.Content>
		</>
	);
};

export default AssetTypeInfoPanelFilesView;
