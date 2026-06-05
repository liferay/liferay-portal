/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayTabs from '@clayui/tabs';
import React, {useState} from 'react';

import {DataMasks} from './data_masks';
import {Profiles} from './profiles/Profiles';
import {Prompts} from './prompts/Prompts';

type TabId = 'profiles' | 'data-masks' | 'prompts';

interface MCPServerAppProps {
	activeTab?: TabId;
}

const TABS: {id: TabId; label: string}[] = [
	{id: 'profiles', label: Liferay.Language.get('profiles')},
	{id: 'data-masks', label: Liferay.Language.get('data-masks')},
	{id: 'prompts', label: Liferay.Language.get('prompts')},
];

export default function MCPServerApp({
	activeTab = 'data-masks',
}: MCPServerAppProps) {
	const initialIndex = Math.max(
		0,
		TABS.findIndex((tab) => tab.id === activeTab)
	);

	const [activeTabIndex, setActiveTabIndex] = useState(initialIndex);

	return (
		<div className="mcp-server-app">
			<ClayTabs
				active={activeTabIndex}
				onActiveChange={setActiveTabIndex}
			>
				{TABS.map((tab) => (
					<ClayTabs.Item
						innerProps={{
							'aria-controls': `mcp-tab-panel-${tab.id}`,
						}}
						key={tab.id}
					>
						{tab.label}
					</ClayTabs.Item>
				))}
			</ClayTabs>

			<ClayTabs.Content active={activeTabIndex} fade>
				<ClayTabs.TabPane aria-labelledby="mcp-tab-profiles">
					<Profiles />
				</ClayTabs.TabPane>

				<ClayTabs.TabPane aria-labelledby="mcp-tab-data-masks">
					<DataMasks />
				</ClayTabs.TabPane>

				<ClayTabs.TabPane aria-labelledby="mcp-tab-prompts">
					<Prompts />
				</ClayTabs.TabPane>
			</ClayTabs.Content>
		</div>
	);
}
