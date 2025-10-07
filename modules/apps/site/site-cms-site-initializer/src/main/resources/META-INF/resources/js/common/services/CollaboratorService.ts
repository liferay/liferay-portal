/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from './ApiHelper';

async function getCollaborators(collaboratorURL: string, itemId: number) {
	const {data, error} = await ApiHelper.get<{
		items: Array<{
			actionIds: string[];
			creator: {
				additionalName: string;
				contentType: string;
				familyName: string;
				givenName: string;
				id: number;
				name: string;
			};
			dateExpired?: string;
			externalReferenceCode: string;
			id: number;
			name: string;
			portrait?: string;
			share: boolean;
			type: string;
		}>;
	}>(
		collaboratorURL
			.replace('{objectEntryId}', itemId.toString())
			.replace('{objectEntryFolderId}', itemId.toString())
	);

	if (data) {
		return data.items;
	}

	throw new Error(error);
}

async function updateCollaborators(
	collaboratorURL: string,
	itemId: number,
	collaborators: {
		actionIds: string[];
		dateExpired?: string;
		id: string | number;
		share: boolean;
		type: string;
	}[]
) {
	return await ApiHelper.post(
		collaboratorURL
			.replace('{objectEntryId}', itemId.toString())
			.replace('{objectEntryFolderId}', itemId.toString()),
		collaborators
	);
}

export default {
	getCollaborators,
	updateCollaborators,
};
