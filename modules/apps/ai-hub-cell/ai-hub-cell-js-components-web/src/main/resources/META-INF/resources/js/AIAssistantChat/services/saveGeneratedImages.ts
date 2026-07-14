/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fetch} from 'frontend-js-web';

const AI_GENERATED_KEYWORD = 'AI-generated';

const CMS_BASIC_DOCUMENTS_ENDPOINT = '/o/cms/basic-documents/scopes';

const DEFAULT_FOLDER_EXTERNAL_REFERENCE_CODE = 'L_FILES';

export interface SaveDestination {
	groupId: number | string;
	objectEntryFolderExternalReferenceCode?: string;
}

export interface SavedDocument {
	id: number;
	title: string;
}

function toBase64(dataURI: string) {
	return dataURI.split(',')[1] ?? dataURI;
}

export function saveGeneratedImages(
	images: string[],
	{
		groupId,
		objectEntryFolderExternalReferenceCode = DEFAULT_FOLDER_EXTERNAL_REFERENCE_CODE,
	}: SaveDestination
): Promise<SavedDocument[]> {
	return Promise.all(
		images.map(async (image) => {
			const name = `AI-image-${crypto.randomUUID()}.png`;

			const response = await fetch(
				`${CMS_BASIC_DOCUMENTS_ENDPOINT}/${groupId}`,
				{
					body: JSON.stringify({
						file: {
							fileBase64: toBase64(image),
							name,
						},
						keywords: [AI_GENERATED_KEYWORD],
						objectEntryFolderExternalReferenceCode,
						title: name,
					}),
					headers: new Headers({
						'Accept': 'application/json',
						'Content-Type': 'application/json',
					}),
					method: 'POST',
				}
			);

			if (!response.ok) {
				throw new Error(
					`Unable to save generated image: ${response.statusText}`
				);
			}

			return response.json();
		})
	);
}
