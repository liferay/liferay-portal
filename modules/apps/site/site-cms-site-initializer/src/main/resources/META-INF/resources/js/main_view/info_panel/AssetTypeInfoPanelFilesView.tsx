/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Tabs from '@clayui/tabs';
import React, {useContext, useState} from 'react';

import {AssetTypeInfoPanelContext, IAssetTypeInfoPanelContext} from './context';
import {TABS} from './tab_content';

const AssetTypeInfoPanelFilesView = () => {
	const {actions}: IAssetTypeInfoPanelContext = useContext(
		AssetTypeInfoPanelContext
	);

	const [active, setActive] = useState(0);

	const tabs = [
		TABS.DETAILS,
		TABS.CATEGORIZATION,
		TABS.PERFORMANCE,
		...(actions?.versions?.href ? [TABS.VERSIONS] : []),
		TABS.COMMENTS,
		...(Liferay.FeatureFlags['LPD-58677'] ? [TABS.PROJECTS] : []),
	];

	return (
		<Tabs active={active} fade onActiveChange={setActive}>
			<Tabs.List className="c-gap-1">
				{tabs.map((tab) => (
					<Tabs.ItemWithIcon
						innerProps={{className: 'lfr-portal-tooltip'}}
						key={tab.id}
						label={tab.name}
						symbol={tab.icon}
					/>
				))}
			</Tabs.List>

			<Tabs.Panels>
				{tabs.map((tab, index) => (
					<Tabs.TabPanel className="p-4" key={tab.id}>
						{active === index ? <tab.component /> : null}
					</Tabs.TabPanel>
				))}
			</Tabs.Panels>
		</Tabs>
	);
};

export default AssetTypeInfoPanelFilesView;
