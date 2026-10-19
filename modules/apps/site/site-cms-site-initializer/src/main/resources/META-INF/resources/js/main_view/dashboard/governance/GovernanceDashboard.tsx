/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {GovernanceContextProvider} from './GovernanceContext';
import {Filters} from './components/Filters';
import {Freshness} from './components/Freshness';
import {GovernanceHealth} from './components/GovernanceHealth';
import {Operations} from './components/Operations';
import {Quality} from './components/Quality';
import {GovernanceAdditionalProps} from './types';

import '../../../../css/dashboard/GovernanceDashboard.scss';

export default function GovernanceDashboard({
	additionalProps,
}: {
	additionalProps: GovernanceAdditionalProps;
}) {
	return (
		<GovernanceContextProvider>
			<Filters />

			<GovernanceHealth />

			<Quality additionalProps={additionalProps} />

			<Freshness additionalProps={additionalProps} />

			<Operations additionalProps={additionalProps} />
		</GovernanceContextProvider>
	);
}
