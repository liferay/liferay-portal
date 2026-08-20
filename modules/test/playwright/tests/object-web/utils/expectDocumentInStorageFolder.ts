/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect} from '@playwright/test';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';

/**
 * Asserts that an attachment object field stored a document in the Documents
 * and Media folder its storage folder path names.
 */
export async function expectDocumentInStorageFolder({
	apiHelpers,
	documentTitle,
	siteId,
	storageFolderName,
}: {
	apiHelpers: DataApiHelpers;
	documentTitle: string;
	siteId: number | string;
	storageFolderName: string;
}) {
	const documentFolder =
		await apiHelpers.headlessDelivery.getSiteDocumentFolderByName(
			siteId,
			storageFolderName
		);

	expect(documentFolder).toBeDefined();

	const documents =
		await apiHelpers.headlessDelivery.getDocumentFolderDocuments(
			String(documentFolder!.id)
		);

	expect(documents.items.map(({title}) => title)).toContain(documentTitle);
}
