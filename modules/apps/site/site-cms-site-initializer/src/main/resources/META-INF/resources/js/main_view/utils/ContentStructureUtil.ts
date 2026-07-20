/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import StructureService from '../../common/services/StructureService';
import {AssetLibrary} from '../../common/types/AssetLibrary';

export async function isContentStructureMoveInvalid(
	embedded: ItemData['embedded'],
	destinationSpaceScopeId: number | undefined,
	assetLibraries: AssetLibrary[]
): Promise<boolean> {
	const objectFolderERC =
		embedded?.systemProperties?.objectDefinitionBrief
			?.objectFolderExternalReferenceCode;
	const entryFolderERC = embedded?.objectEntryFolderExternalReferenceCode;

	const isContentStructureItem =
		objectFolderERC === 'L_CMS_CONTENT_STRUCTURES' ||
		entryFolderERC === 'L_CONTENTS';

	if (!isContentStructureItem) {
		return false;
	}

	const structureExternalReferenceCode =
		embedded?.systemProperties?.objectDefinitionBrief
			?.externalReferenceCode;

	if (!structureExternalReferenceCode || !destinationSpaceScopeId) {
		return true;
	}

	try {
		const response = await StructureService.getStructure(
			structureExternalReferenceCode
		);

		if (response.error || !response.data) {
			return true;
		}

		const objectDefinition = response.data;
		const acceptedGroupSettings =
			objectDefinition.objectDefinitionSettings?.find(
				(setting) =>
					setting.name === 'acceptedGroupExternalReferenceCodes'
			);

		if (!acceptedGroupSettings || !acceptedGroupSettings.value) {
			return false;
		}

		const acceptedGroupERCs = acceptedGroupSettings.value.split(',');

		const destinationAssetLibrary = assetLibraries.find(
			(lib) => lib.groupId === destinationSpaceScopeId
		);

		if (
			!destinationAssetLibrary ||
			!destinationAssetLibrary.externalReferenceCode
		) {
			return true;
		}

		const destinationSpaceERC =
			destinationAssetLibrary.externalReferenceCode;
		const isStructureAvailableInDestination =
			acceptedGroupERCs.includes(destinationSpaceERC);

		return !isStructureAvailableInDestination;
	}
	catch (error) {
		return true;
	}
}
