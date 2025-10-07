/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {VersionRow} from './VersionRow';

interface RevisionHistoryProps {
	setWorkflowDefinitionVersions: React.Dispatch<
		React.SetStateAction<WorkflowDefinitionVersion[]>
	>;
	timeZoneId: string;
	workflowDefinitionVersions: WorkflowDefinitionVersion[];
}

export function RevisionHistory({
	setWorkflowDefinitionVersions,
	timeZoneId,
	workflowDefinitionVersions,
}: RevisionHistoryProps) {
	return (
		<>
			{workflowDefinitionVersions.map(
				({creatorName, dateCreated, version}, index) => (
					<VersionRow
						creatorName={creatorName}
						dateCreated={dateCreated}
						key={`${dateCreated}_${index}`}
						setWorkflowDefinitionVersions={
							setWorkflowDefinitionVersions
						}
						timeZoneId={timeZoneId}
						versionNumber={parseInt(version, 10)}
					/>
				)
			)}
		</>
	);
}
