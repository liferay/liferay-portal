/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import SpaceService from '../../../services/SpaceService';
import SpaceSticker, {LogoColor} from '../../components/SpaceSticker';

const {useEffect, useState} = React;

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
	const [spaceLogoColor, setSpaceLogoColor] = useState<LogoColor>();
	const [spaceName, setSpaceName] = useState(Liferay.Language.get('loading'));

	const fetchSpaceName = async (externalReferenceCodes: string[]) => {
		if (!externalReferenceCodes.length) {
			setSpaceName('');
		}
		else {
			const space = await SpaceService.getSpace(
				externalReferenceCodes[0]
			);

			setSpaceLogoColor(space.settings?.logoColor as LogoColor);
			setSpaceName(space.name);
		}
	};

	const spaceExternalReferenceCodes = getSpaceExternalReferenceCodes(
		itemData.objectDefinitionSettings
	);

	useEffect(() => {
		fetchSpaceName(spaceExternalReferenceCodes);
	}, [spaceExternalReferenceCodes]);

	return !spaceExternalReferenceCodes.length ? (
		<span className="badge badge-pill badge-secondary">
			<span className="badge-item badge-item-expand">
				{Liferay.Language.get('all-spaces')}
			</span>
		</span>
	) : (
		<span className="align-items-center d-flex space-renderer-sticker">
			<SpaceSticker
				displayType={spaceLogoColor}
				name={spaceName}
				size="sm"
			/>
		</span>
	);
};

export default StructureScopeRenderer;
