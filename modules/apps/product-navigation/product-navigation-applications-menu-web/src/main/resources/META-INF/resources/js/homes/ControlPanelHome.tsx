/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import HomePageLayout from './components/HomePageLayout';
import {HomeProps} from './types';

const ControlPanelHome = ({icon, items, title}: HomeProps) => {
	return (
		<HomePageLayout
			displayType="horizontal"
			icon={icon}
			items={items}
			title={title}
		/>
	);
};
export default ControlPanelHome;
