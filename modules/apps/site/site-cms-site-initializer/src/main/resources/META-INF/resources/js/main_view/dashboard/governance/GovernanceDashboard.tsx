/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {GovernanceContextProvider} from './GovernanceContext';
import {AttentionRequired} from './components/AttentionRequired';
import {Filters} from './components/Filters';

export default function GovernanceDashboard() {
	return (
		<GovernanceContextProvider>
			<Filters />

			<AttentionRequired />
		</GovernanceContextProvider>
	);
}
