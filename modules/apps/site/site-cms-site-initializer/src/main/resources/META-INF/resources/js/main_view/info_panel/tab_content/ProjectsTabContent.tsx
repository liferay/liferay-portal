/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import LinkedProjects from '../../../common/components/LinkedProjects';
import {AssetTypeInfoPanelContext} from '../context';

const ProjectsTabContent = () => {
	const {
		asset,
		cmpProjectAssetRelationshipObjectDefinitionId,
		cmpProjectObjectDefinitionId,
		cmpProjectViewURL,
		cmpTaskObjectDefinitionId,
		cmpTaskViewURL,
		entryClassName,
	} = useContext(AssetTypeInfoPanelContext);

	return (
		<LinkedProjects
			assetKeywords={asset.keywords}
			cmpProjectAssetRelationshipObjectDefinitionId={
				cmpProjectAssetRelationshipObjectDefinitionId
			}
			cmpProjectObjectDefinitionId={cmpProjectObjectDefinitionId}
			cmpTaskObjectDefinitionId={cmpTaskObjectDefinitionId}
			entryClassName={entryClassName}
			entryExternalReferenceCode={asset.externalReferenceCode}
			entryGroupExternalReferenceCode={
				asset.systemProperties?.scope?.externalReferenceCode
			}
			projectViewURL={cmpProjectViewURL}
			taskViewURL={cmpTaskViewURL}
		/>
	);
};

export default ProjectsTabContent;
