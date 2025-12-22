/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Icon} from '@clayui/core';
import DropDown, {Align} from '@clayui/drop-down';
import Tabs from '@clayui/tabs';
import React, {useState} from 'react';

import {TABS} from './tab_content';

const DROPDOWN_TABS = [TABS.VERSIONS, TABS.COMMENTS];
const MAIN_TABS = [TABS.DETAILS, TABS.CATEGORIZATION, TABS.PERFORMANCE];

const ALL_TABS = [...MAIN_TABS, ...DROPDOWN_TABS];

const AssetTypeInfoPanelFilesView = () => {
	const [active, setActive] = useState(0);

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

				<div onClick={(event) => event.stopPropagation()}>
					<DropDown
						alignmentPosition={Align.BottomLeft}
						closeOnClick={true}
						hasRightSymbols
						trigger={
							<Tabs.Item
								active={active >= MAIN_TABS.length}
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
						{DROPDOWN_TABS.map((tab) => {
							const tabIndex = ALL_TABS.findIndex(
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
					</DropDown>
				</div>
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
