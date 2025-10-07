/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useEffect, useState} from 'react';

import SpacesDisplay from '../../../common/components/SpacesDisplay';
import SpaceService from '../../../common/services/SpaceService';
import {Space} from '../../../common/types/Space';

interface ObjectDefinitionSetting {
	name: string;
	value: string;
}

const getSpaceExternalReferenceCodes = (
	objectDefinitionSettings: ObjectDefinitionSetting[]
) => {
	for (const objectDefinitionSetting of objectDefinitionSettings) {
		if (
			objectDefinitionSetting.name === 'acceptAllGroups' &&
			objectDefinitionSetting.value
		) {
			return [];
		}
		else if (
			objectDefinitionSetting.name ===
			'acceptedGroupExternalReferenceCodes'
		) {
			return objectDefinitionSetting.value.split(',');
		}
	}

	return [];
};

const StructureScopeRenderer = ({
	itemData,
}: {
	itemData: {objectDefinitionSettings: ObjectDefinitionSetting[]};
}) => {
	const [spaces, setSpaces] = useState<Space[]>([]);

	useEffect(() => {
		const fetchSpaces = async () => {
			const response = await SpaceService.getSpaces();

			setSpaces(response);
		};

		fetchSpaces();
	}, []);

	const spaceExternalReferenceCodes = getSpaceExternalReferenceCodes(
		itemData.objectDefinitionSettings
	);

	const structureSpaces = spaces.filter((space) =>
		spaceExternalReferenceCodes.includes(space.externalReferenceCode)
	);

	return <SpacesDisplay spaces={structureSpaces} />;
};

export default StructureScopeRenderer;
