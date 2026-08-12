/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {GovernanceContextProvider} from './GovernanceContext';
import {AttentionRequired} from './components/AttentionRequired';
import {DuplicationAndSimilarity} from './components/DuplicationAndSimilarity';
import {Filters} from './components/Filters';
import {GovernanceHealth} from './components/GovernanceHealth';
import {NeedsReview} from './components/NeedsReview';
import {WorkflowAndContentProgress} from './components/WorkflowAndContentProgress';
import {GovernanceAdditionalProps} from './types';

import '../../../../css/dashboard/GovernanceDashboard.scss';

export default function GovernanceDashboard({
	additionalProps,
	constants,
}: {
	additionalProps: GovernanceAdditionalProps;
	constants: {[key: string]: string};
}) {
	return (
		<GovernanceContextProvider>
			<Filters />

			<GovernanceHealth />

			<AttentionRequired />

			<NeedsReview additionalProps={additionalProps} />

			<WorkflowAndContentProgress additionalProps={additionalProps} />

			<DuplicationAndSimilarity
				additionalProps={additionalProps}
				constants={constants}
			/>
		</GovernanceContextProvider>
	);
}
