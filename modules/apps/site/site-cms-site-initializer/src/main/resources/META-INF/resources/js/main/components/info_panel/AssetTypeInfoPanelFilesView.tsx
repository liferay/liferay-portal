/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Tabs from '@clayui/tabs';
import React, {useState} from 'react';

import {TABS} from './tab_content';

const AssetTypeInfoPanelFilesView = () => {
	const [active, setActive] = useState(0);

	const sortedTabs = [
		TABS.DETAILS,
		TABS.CATEGORIZATION,
		TABS.PERFORMANCE,
		TABS.VERSIONS,
	];

	return (
		<>
			<Tabs active={active} justified={false} onActiveChange={setActive}>
				{sortedTabs.map((tab, index) => (
					<Tabs.Item
						innerProps={{
							'aria-controls': `tabpanel-${tab.id}`,
						}}
						key={`tab_${tab.id}_${index}`}
					>
						{tab.name}
					</Tabs.Item>
				))}
			</Tabs>

			<Tabs.Content active={active} fade>
				{sortedTabs.map((tab, index) => (
					<Tabs.TabPane
						className={tab.className || ''}
						key={`pane_${tab.id}_${index}`}
					>
						<tab.component />
					</Tabs.TabPane>
				))}
			</Tabs.Content>
		</>
	);
};

export default AssetTypeInfoPanelFilesView;
