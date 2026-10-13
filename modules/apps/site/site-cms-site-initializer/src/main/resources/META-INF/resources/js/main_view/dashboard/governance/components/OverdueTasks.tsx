/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import InteractiveCard from '../../performance/components/InteractiveCard';
import {GovernanceContext} from '../GovernanceContext';
import {PercentageOfTotal} from './PercentageOfTotal';

export function OverdueTasksCard() {
	const {loadingStatistics, statistics} = useContext(GovernanceContext);

	const count = statistics?.overdueWorkflowTasksCount ?? 0;

	return (
		<InteractiveCard
			color="orange"
			description={Liferay.Language.get(
				'these-are-workflow-tasks-that-have-passed-their-due-date'
			)}
			icon="time"
			loading={loadingStatistics}
			title={Liferay.Language.get('overdue-tasks')}
			value={count}
		>
			<PercentageOfTotal
				count={count}
				template={Liferay.Language.get('x-of-x-workflow-tasks')}
				total={statistics?.workflowTasksCount ?? 0}
			/>
		</InteractiveCard>
	);
}
