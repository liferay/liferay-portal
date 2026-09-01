/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Tabs from '@clayui/tabs';
import React, {useState} from 'react';

import {TABS} from './tab_content';
import DetailsTabContent from './tab_content/DetailsTabContent';

const AssetTypeInfoPanelFolderView = () => {
	const [active, setActive] = useState(0);

	return (
		<Tabs active={active} fade onActiveChange={setActive}>
			<Tabs.List className="c-gap-1">
				<Tabs.ItemWithIcon
					innerProps={{className: 'lfr-portal-tooltip'}}
					label={TABS.DETAILS.name}
					symbol={TABS.DETAILS.icon}
				/>
			</Tabs.List>

			<Tabs.Panels>
				<Tabs.TabPanel className="p-4">
					<DetailsTabContent />
				</Tabs.TabPanel>
			</Tabs.Panels>
		</Tabs>
	);
};

export default AssetTypeInfoPanelFolderView;
