/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Tabs from '@clayui/tabs';
import React, {useState} from 'react';

import {TABS} from './tab_content';
import DetailsTabContent from './tab_content/DetailsTabContent';
import PerformanceTabContent from './tab_content/PerformanceTabContent';

const AssetTypeInfoPanelFilesView = () => {
	const [active, setActive] = useState(0);

	return (
		<>
			<Tabs active={active} onActiveChange={setActive}>
				<Tabs.Item
					innerProps={{
						'aria-controls': `tabpanel-${TABS.DETAILS.id}`,
					}}
				>
					{TABS.DETAILS.name}
				</Tabs.Item>

				<Tabs.Item
					innerProps={{
						'aria-controls': `tabpanel-${TABS.PERFORMANCE.id}`,
					}}
				>
					{TABS.PERFORMANCE.name}
				</Tabs.Item>
			</Tabs>
			<Tabs.Content active={active} fade>
				<Tabs.TabPane className="p-4" key={TABS.DETAILS.id}>
					<DetailsTabContent />
				</Tabs.TabPane>

				<Tabs.TabPane className="p-4" key={TABS.PERFORMANCE.id}>
					<PerformanceTabContent />
				</Tabs.TabPane>
			</Tabs.Content>
		</>
	);
};

export default AssetTypeInfoPanelFilesView;
