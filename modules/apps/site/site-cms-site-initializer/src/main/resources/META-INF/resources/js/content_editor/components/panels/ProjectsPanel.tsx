/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import LinkedProjects from '../../../common/components/LinkedProjects';

type ProjectsPanelProps = {
	assetKeywords?: string[];
	cmpProjectAssetRelationshipObjectDefinitionId?: number | null;
	cmpProjectObjectDefinitionId?: number | null;
	cmpProjectViewURL?: string;
	cmpTaskObjectDefinitionId?: number | null;
	cmpTaskViewURL?: string;
	entryClassName?: string;
	entryExternalReferenceCode?: string;
	entryGroupExternalReferenceCode?: string;
};

export default function ProjectsPanel({
	assetKeywords,
	cmpProjectAssetRelationshipObjectDefinitionId,
	cmpProjectObjectDefinitionId,
	cmpProjectViewURL,
	cmpTaskObjectDefinitionId,
	cmpTaskViewURL,
	entryClassName,
	entryExternalReferenceCode,
	entryGroupExternalReferenceCode,
}: ProjectsPanelProps) {
	return (
		<div className="px-3">
			<LinkedProjects
				assetKeywords={assetKeywords}
				cmpProjectAssetRelationshipObjectDefinitionId={
					cmpProjectAssetRelationshipObjectDefinitionId
				}
				cmpProjectObjectDefinitionId={cmpProjectObjectDefinitionId}
				cmpTaskObjectDefinitionId={cmpTaskObjectDefinitionId}
				entryClassName={entryClassName}
				entryExternalReferenceCode={entryExternalReferenceCode}
				entryGroupExternalReferenceCode={
					entryGroupExternalReferenceCode
				}
				projectViewURL={cmpProjectViewURL}
				taskViewURL={cmpTaskViewURL}
			/>
		</div>
	);
}
