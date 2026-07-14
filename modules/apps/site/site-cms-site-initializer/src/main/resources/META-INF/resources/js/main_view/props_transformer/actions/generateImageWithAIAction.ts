/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export type GenerateImageWithAIData = {
	action: 'generateImageWithAI';
	groupId?: string;
	message: string;
	objectEntryFolderExternalReferenceCode?: string;
};

export default function generateImageWithAIAction(
	data: GenerateImageWithAIData
) {
	Liferay.fire('openAIAssistantChat', {
		context: {
			groupId: data.groupId,
			objectEntryFolderExternalReferenceCode:
				data.objectEntryFolderExternalReferenceCode,
		},
		message: data.message,
	});
}
